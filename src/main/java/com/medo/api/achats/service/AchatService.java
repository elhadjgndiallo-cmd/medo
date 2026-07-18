package com.medo.api.achats.services;

import com.medo.api.achats.repository.BonCommandeRepository;
import com.medo.api.achats.repository.FournisseurRepository;
import com.medo.api.achats.dto.AchatsDtos.*;
import com.medo.api.achats.entity.BonCommande;
import com.medo.api.achats.entity.Fournisseur;
import com.medo.api.achats.entity.LigneCommande;
import com.medo.api.auth.repository.UtilisateurRepository;
import com.medo.api.auth.entity.Utilisateur;
import com.medo.api.exception.GlobalExceptionHandler.*;
import com.medo.api.inventaire.repository.LotRepository;
import com.medo.api.inventaire.repository.VarianteProduitRepository;
import com.medo.api.inventaire.entity.Lot;
import com.medo.api.inventaire.entity.VarianteProduit;
import com.medo.api.inventaire.services.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AchatService {

    private static final Logger log = LoggerFactory.getLogger(AchatService.class);

    @Autowired private FournisseurRepository     fournisseurRepository;
    @Autowired private BonCommandeRepository     bcRepository;
    @Autowired private LotRepository             lotRepository;
    @Autowired private VarianteProduitRepository varianteRepository;
    @Autowired private UtilisateurRepository     utilisateurRepository;
    @Autowired private StockService              stockService;

    // ── Fournisseurs ──

    @Transactional(readOnly = true)
    public Page<FournisseurResponse> listerFournisseurs(String search, Pageable pageable) {
        return (search != null && !search.isBlank())
            ? fournisseurRepository.findByNomContainingIgnoreCaseAndActifTrue(search, pageable)
                .map(this::toFournisseurResponse)
            : fournisseurRepository.findAll(pageable)
                .map(this::toFournisseurResponse);
    }

    @Transactional
    public FournisseurResponse creerFournisseur(CreerFournisseurRequest req) {
        if (fournisseurRepository.existsByNom(req.getNom()))
            throw new DuplicateResourceException("Fournisseur déjà existant : " + req.getNom());
        Fournisseur f = new Fournisseur();
        f.setNom(req.getNom()); f.setContact(req.getContact());
        f.setEmail(req.getEmail()); f.setAdresse(req.getAdresse()); f.setActif(true);
        return toFournisseurResponse(fournisseurRepository.save(f));
    }

    @Transactional
    public FournisseurResponse modifierFournisseur(UUID id, CreerFournisseurRequest req) {
        Fournisseur f = fournisseurRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", id.toString()));
        f.setNom(req.getNom()); f.setContact(req.getContact());
        f.setEmail(req.getEmail()); f.setAdresse(req.getAdresse());
        f.setUpdatedAt(LocalDateTime.now());
        return toFournisseurResponse(fournisseurRepository.save(f));
    }

    // ── Bons de commande ──

    @Transactional(readOnly = true)
    public Page<BonCommandeResponse> listerBonsCommande(BonCommande.StatutCommande statut, Pageable pageable) {
        return statut != null
            ? bcRepository.findByStatutOrderByCreatedAtDesc(statut, pageable).map(this::toBcResponse)
            : bcRepository.findAllByOrderByCreatedAtDesc(pageable).map(this::toBcResponse);
    }

    @Transactional(readOnly = true)
    public BonCommandeResponse getBonCommande(UUID id) {
        return toBcResponse(findBcOrThrow(id));
    }

    @Transactional
    public BonCommandeResponse creerBonCommande(CreerBonCommandeRequest req, UUID userId) {
        Fournisseur f = fournisseurRepository.findById(req.getFournisseurId())
            .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", req.getFournisseurId().toString()));

        Utilisateur user = utilisateurRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", userId.toString()));

        BonCommande bc = new BonCommande();
        bc.setReference(genererReference());
        bc.setFournisseur(f); bc.setCreatedBy(user);
        bc.setDateCommande(LocalDate.now());
        bc.setDateLivraisonPrevue(req.getDateLivraisonPrevue());
        bc.setStatut(BonCommande.StatutCommande.CONFIRME);
        bc.setNotes(req.getNotes());

        BigDecimal total = BigDecimal.ZERO;
        for (LigneBcRequest lr : req.getLignes()) {
            VarianteProduit variante = varianteRepository.findById(lr.getVarianteId())
                .orElseThrow(() -> new ResourceNotFoundException("Variante", lr.getVarianteId().toString()));

            BigDecimal st = lr.getPrixUnitaire().multiply(BigDecimal.valueOf(lr.getQuantiteCmd()));
            LigneCommande ligne = new LigneCommande();
            ligne.setBonCommande(bc);
            ligne.setVariante(variante);
            ligne.setQuantiteCmd(lr.getQuantiteCmd());
            ligne.setPrixUnitaire(lr.getPrixUnitaire());
            ligne.setSousTotal(st);
            bc.getLignes().add(ligne);
            total = total.add(st);
        }
        bc.setMontantTotal(total);

        BonCommande saved = bcRepository.save(bc);
        log.info("BC {} créé : total={} GNF", saved.getReference(), total);
        return toBcResponse(saved);
    }

    @Transactional
    public BonCommandeResponse receptionnerCommande(UUID bcId, ReceptionnerBcRequest req, UUID userId) {
        BonCommande bc = findBcOrThrow(bcId);

        if (BonCommande.StatutCommande.RECU.equals(bc.getStatut()))
            throw new BusinessRuleException("BC déjà totalement réceptionné");
        if (BonCommande.StatutCommande.ANNULE.equals(bc.getStatut()))
            throw new BusinessRuleException("BC annulé — impossible de réceptionner");

        for (LigneReceptionRequest lr : req.getLignesRecues()) {
            LigneCommande ligne = bc.getLignes().stream()
                .filter(l -> l.getId().equals(lr.getLigneCommandeId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Ligne commande", lr.getLigneCommandeId().toString()));

            if (lr.getQuantiteRecue() > ligne.getQuantiteRestante())
                throw new BusinessRuleException(
                    "Quantité reçue (" + lr.getQuantiteRecue() +
                    ") > quantité restante (" + ligne.getQuantiteRestante() + ")");

            VarianteProduit variante = varianteRepository.findById(lr.getVarianteId())
                .orElseThrow(() -> new ResourceNotFoundException("Variante", lr.getVarianteId().toString()));

            Lot lot = new Lot();
            lot.setVariante(variante);
            lot.setNumeroLot(lr.getNumeroLot());
            lot.setDateFabrication(lr.getDateFabrication());
            lot.setDatePeremption(lr.getDatePeremption());
            lot.setQuantite(lr.getQuantiteRecue());
            lotRepository.save(lot);

            stockService.incrementerStock(lot, lr.getQuantiteRecue(), bc.getId(), userId);
            ligne.recevoirQuantite(lr.getQuantiteRecue());
        }

        bc.setStatut(bc.isTotalementRecu()
            ? BonCommande.StatutCommande.RECU
            : BonCommande.StatutCommande.PARTIELLEMENT_RECU);
        bc.setUpdatedAt(LocalDateTime.now());

        BonCommande saved = bcRepository.save(bc);
        log.info("BC {} -> {}", bc.getReference(), saved.getStatut());
        return toBcResponse(saved);
    }

    @Transactional
    public BonCommandeResponse annulerCommande(UUID id) {
        BonCommande bc = findBcOrThrow(id);
        if (!BonCommande.StatutCommande.BROUILLON.equals(bc.getStatut()) &&
            !BonCommande.StatutCommande.CONFIRME.equals(bc.getStatut()))
            throw new BusinessRuleException("Seuls BROUILLON et CONFIRME peuvent être annulés");
        bc.setStatut(BonCommande.StatutCommande.ANNULE);
        bc.setUpdatedAt(LocalDateTime.now());
        return toBcResponse(bcRepository.save(bc));
    }

    @Transactional(readOnly = true)
    public List<TimelineResponse> getTimeline(UUID bcId) {
        BonCommande bc = findBcOrThrow(bcId);
        return List.of(
            new TimelineResponse("CREE",       "Bon de commande créé",              bc.getCreatedAt(), true),
            new TimelineResponse("CONFIRME",   "Confirmé et envoyé au fournisseur",
                !BonCommande.StatutCommande.BROUILLON.equals(bc.getStatut()) ? bc.getUpdatedAt() : null,
                !BonCommande.StatutCommande.BROUILLON.equals(bc.getStatut())),
            new TimelineResponse("EN_ATTENTE", "En attente de livraison", null,
                BonCommande.StatutCommande.CONFIRME.equals(bc.getStatut()) ||
                BonCommande.StatutCommande.PARTIELLEMENT_RECU.equals(bc.getStatut())),
            new TimelineResponse("RECU",       "Réceptionné complet",
                BonCommande.StatutCommande.RECU.equals(bc.getStatut()) ? bc.getUpdatedAt() : null,
                BonCommande.StatutCommande.RECU.equals(bc.getStatut()))
        );
    }

    // ── Helpers ──

    private String genererReference() {
        int last = bcRepository.findLastBcNumber();
        return String.format("BC-%d-%04d", LocalDate.now().getYear(), last + 1);
    }

    private BonCommande findBcOrThrow(UUID id) {
        return bcRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("BonCommande", id.toString()));
    }

    private FournisseurResponse toFournisseurResponse(Fournisseur f) {
        return new FournisseurResponse(f.getId(), f.getNom(), f.getContact(),
            f.getEmail(), f.getAdresse(), Boolean.TRUE.equals(f.getActif()),
            f.getBonsCommande() != null ? f.getBonsCommande().size() : 0);
    }

    private BonCommandeResponse toBcResponse(BonCommande bc) {
        List<LigneCommandeResponse> lignes = bc.getLignes() == null ? List.of()
            : bc.getLignes().stream().map(l -> new LigneCommandeResponse(
                l.getId(),
                l.getVariante() != null ? l.getVariante().getLibelleComplet() : "",
                l.getVariante() != null ? l.getVariante().getDosage() : "",
                l.getQuantiteCmd(), l.getQuantiteRecue(), l.getQuantiteRestante(),
                l.getPrixUnitaire(), l.getSousTotal(), l.estTotalementRecu()
            )).collect(Collectors.toList());

        return new BonCommandeResponse(bc.getId(), bc.getReference(),
            toFournisseurResponse(bc.getFournisseur()),
            bc.getDateCommande(), bc.getDateLivraisonPrevue(),
            bc.getStatut().name(), bc.getMontantTotal(), bc.getNotes(),
            bc.getCreatedBy() != null
                ? bc.getCreatedBy().getNom() + " " + bc.getCreatedBy().getPrenom() : "",
            bc.getCreatedAt(), lignes);
    }
}
