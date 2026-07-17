package com.medo.api.pos.services;

import com.medo.api.auth.dao.UtilisateurRepository;
import com.medo.api.auth.entity.Utilisateur;
import com.medo.api.exception.GlobalExceptionHandler.*;
import com.medo.api.inventaire.dao.VarianteProduitRepository;
import com.medo.api.inventaire.entity.VarianteProduit;
import com.medo.api.inventaire.services.StockService;
import com.medo.api.pos.dao.*;
import com.medo.api.pos.dto.PosDtos.*;
import com.medo.api.pos.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VenteService {

    private static final Logger log = LoggerFactory.getLogger(VenteService.class);

    @Autowired private CaisseRepository          caisseRepository;
    @Autowired private SessionCaisseRepository    sessionRepository;
    @Autowired private VenteRepository            venteRepository;
    @Autowired private ClientRepository           clientRepository;
    @Autowired private UtilisateurRepository      utilisateurRepository;
    @Autowired private VarianteProduitRepository  varianteRepository;
    @Autowired private StockService               stockService;

    // ── Caisses ──

    @Transactional(readOnly = true)
    public List<CaisseResponse> listerCaisses() {
        return caisseRepository.findAllByActifTrue()
            .stream().map(this::toCaisseResponse).collect(Collectors.toList());
    }

    @Transactional
    public CaisseResponse creerCaisse(String nom, String reference) {
        if (caisseRepository.existsByNom(nom))
            throw new DuplicateResourceException("Caisse déjà existante : " + nom);
        Caisse c = new Caisse();
        c.setNom(nom); c.setReference(reference); c.setActif(true);
        return toCaisseResponse(caisseRepository.save(c));
    }

    // ── Sessions ──

    @Transactional
    public SessionCaisseResponse ouvrirSession(OuvrirSessionRequest req, UUID userId) {
        Caisse caisse = caisseRepository.findById(req.getCaisseId())
            .orElseThrow(() -> new ResourceNotFoundException("Caisse", req.getCaisseId().toString()));

        sessionRepository.findByCaisseIdAndStatut(req.getCaisseId(), SessionCaisse.StatutSession.OUVERTE)
            .ifPresent(s -> { throw new SessionCaisseException(
                "Session déjà ouverte sur : " + caisse.getNom()); });

        Utilisateur utilisateur = utilisateurRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", userId.toString()));

        SessionCaisse session = new SessionCaisse();
        session.setCaisse(caisse);
        session.setUtilisateur(utilisateur);
        session.setFondCaisse(req.getFondCaisse());
        session.setNotes(req.getNotes());
        session.setStatut(SessionCaisse.StatutSession.OUVERTE);

        SessionCaisse saved = sessionRepository.save(session);
        log.info("Session ouverte : caisse={}, user={}", caisse.getNom(), userId);
        return toSessionResponse(saved);
    }

    @Transactional
    public SessionCaisseResponse fermerSession(UUID sessionId, FermerSessionRequest req, UUID userId) {
        SessionCaisse session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new ResourceNotFoundException("Session", sessionId.toString()));

        if (!SessionCaisse.StatutSession.OUVERTE.equals(session.getStatut()))
            throw new SessionCaisseException("Session déjà fermée");

        session.fermer(req.getMontantCloture());
        if (req.getNotes() != null) session.setNotes(req.getNotes());

        SessionCaisse saved = sessionRepository.save(session);
        log.info("Session fermée : {} (écart: {})", sessionId, saved.calculerEcart());
        return toSessionResponse(saved);
    }

    @Transactional(readOnly = true)
    public SessionCaisseResponse getSession(UUID id) {
        return toSessionResponse(sessionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Session", id.toString())));
    }

    // ── Ventes ──

    @Transactional
    public VenteResponse creerVente(CreerVenteRequest req, UUID userId) {
        SessionCaisse session = sessionRepository.findById(req.getSessionId())
            .orElseThrow(() -> new ResourceNotFoundException("Session", req.getSessionId().toString()));

        if (!SessionCaisse.StatutSession.OUVERTE.equals(session.getStatut()))
            throw new SessionCaisseException("La session de caisse est fermée");

        Utilisateur utilisateur = utilisateurRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", userId.toString()));

        Client client = null;
        if (req.getClientId() != null)
            client = clientRepository.findById(req.getClientId()).orElse(null);

        Vente vente = new Vente();
        vente.setSession(session);
        vente.setClient(client);
        vente.setUtilisateur(utilisateur);
        vente.setNumeroTicket(genererNumeroTicket());
        vente.setModePaiement(Vente.ModePaiement.valueOf(req.getModePaiement()));
        vente.setMontantRemise(req.getMontantRemise() != null ? req.getMontantRemise() : BigDecimal.ZERO);
        vente.setStatut(Vente.StatutVente.VALIDEE);
        vente.setNotes(req.getNotes());

        BigDecimal total = BigDecimal.ZERO;

        for (LigneVenteRequest lr : req.getLignes()) {
            // Charger la vraie variante avec son produit pour le prix réel
            VarianteProduit variante = varianteRepository.findByIdWithProduit(lr.getVarianteId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Variante", lr.getVarianteId().toString()));

            BigDecimal prixUnitaire = variante.getPrixEffectif();

            LigneVente ligne = new LigneVente();
            ligne.setVente(vente);
            ligne.setVariante(variante);
            ligne.setQuantite(lr.getQuantite());
            ligne.setPrixUnitaire(prixUnitaire);
            ligne.setSousTotal(prixUnitaire.multiply(BigDecimal.valueOf(lr.getQuantite())));
            vente.getLignes().add(ligne);

            stockService.decrementerStock(lr.getVarianteId(), lr.getQuantite(), null, userId);
            total = total.add(ligne.getSousTotal());
        }

        vente.setMontantTotal(total.subtract(vente.getMontantRemise()));
        Vente saved = venteRepository.save(vente);
        log.info("Vente {} : {} GNF ({} lignes)",
            saved.getNumeroTicket(), saved.getMontantTotal(), req.getLignes().size());
        return toVenteResponse(saved);
    }

    @Transactional
    public VenteResponse annulerVente(UUID venteId, UUID userId) {
        Vente vente = venteRepository.findById(venteId)
            .orElseThrow(() -> new ResourceNotFoundException("Vente", venteId.toString()));
        if (Vente.StatutVente.ANNULEE.equals(vente.getStatut()))
            throw new BusinessRuleException("Vente déjà annulée");
        vente.annuler();
        log.info("Vente {} annulée par {}", venteId, userId);
        return toVenteResponse(venteRepository.save(vente));
    }

    @Transactional(readOnly = true)
    public Page<VenteResponse> getVentesSession(UUID sessionId, Pageable pageable) {
        return venteRepository.findBySessionIdOrderByDateVenteDesc(sessionId, pageable)
            .map(this::toVenteResponse);
    }

    // ── Stats ──

    @Transactional(readOnly = true)
    public StatsPOSResponse getStats() {
        LocalDateTime debutJour = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime debutMois = LocalDateTime.now().withDayOfMonth(1).toLocalDate().atStartOfDay();
        LocalDateTime now       = LocalDateTime.now();
        return new StatsPOSResponse(
            caisseRepository.findAllByActifTrue().size(),
            sessionRepository.countOuvertes(),
            venteRepository.countVentesPeriode(debutJour, now),
            venteRepository.sumMontantPeriode(debutJour, now),
            venteRepository.sumMontantPeriode(debutMois, now)
        );
    }

    // ── Clients ──

    @Transactional
    public ClientResponse creerClient(CreerClientRequest req) {
        Client c = new Client();
        c.setNom(req.getNom()); c.setTelephone(req.getTelephone());
        c.setEmail(req.getEmail()); c.setNotes(req.getNotes());
        return toClientResponse(clientRepository.save(c));
    }

    @Transactional(readOnly = true)
    public Page<ClientResponse> listerClients(String search, Pageable pageable) {
        return clientRepository
            .findByNomContainingIgnoreCase(search != null ? search : "", pageable)
            .map(this::toClientResponse);
    }

    // ── Helpers ──

    private String genererNumeroTicket() {
        int last = venteRepository.findLastTicketNumber();
        return String.format("T%06d", last + 1);
    }

    private CaisseResponse toCaisseResponse(Caisse c) {
        SessionCaisse active = c.getSessionActive();
        return new CaisseResponse(c.getId(), c.getNom(), c.getReference(),
            Boolean.TRUE.equals(c.getActif()), c.isDisponible(),
            active != null ? toSessionResponse(active) : null);
    }

    private SessionCaisseResponse toSessionResponse(SessionCaisse s) {
        BigDecimal total = s.getVentes() == null ? BigDecimal.ZERO
            : s.getVentes().stream()
                .filter(v -> Vente.StatutVente.VALIDEE.equals(v.getStatut()))
                .map(Vente::getMontantTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new SessionCaisseResponse(
            s.getId(), s.getCaisse().getId(), s.getCaisse().getNom(),
            s.getUtilisateur().getNom() + " " + s.getUtilisateur().getPrenom(),
            s.getDateOuverture(), s.getDateFermeture(), s.getFondCaisse(),
            s.getMontantCloture(),
            s.getMontantCloture() != null ? s.calculerEcart() : null,
            s.getStatut().name(), s.getDureeOuverte(),
            s.getVentes() != null ? s.getVentes().size() : 0, total);
    }

    private VenteResponse toVenteResponse(Vente v) {
        List<LigneVenteResponse> lignes = v.getLignes() == null ? List.of()
            : v.getLignes().stream().map(l -> new LigneVenteResponse(
                l.getId(),
                l.getVariante() != null ? l.getVariante().getLibelleComplet() : "",
                l.getVariante() != null ? l.getVariante().getDosage() : "",
                l.getLot()      != null ? l.getLot().getNumeroLot() : "",
                l.getQuantite(), l.getPrixUnitaire(), l.getSousTotal()
            )).collect(Collectors.toList());

        return new VenteResponse(v.getId(), v.getNumeroTicket(), v.getDateVente(),
            v.getMontantTotal(), v.getMontantRemise(), v.getModePaiement().name(),
            v.getStatut().name(),
            v.getUtilisateur().getNom() + " " + v.getUtilisateur().getPrenom(),
            v.getClient() != null ? toClientResponse(v.getClient()) : null, lignes);
    }

    private ClientResponse toClientResponse(Client c) {
        return new ClientResponse(c.getId(), c.getNom(), c.getTelephone(), c.getEmail());
    }
}
