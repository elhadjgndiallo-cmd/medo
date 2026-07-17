package com.medo.api.mobile.services;

import com.medo.api.common.dao.TenantRepository;
import com.medo.api.common.entity.Tenant;
import com.medo.api.exception.GlobalExceptionHandler.*;
import com.medo.api.mobile.dao.ClientMobileRepository;
import com.medo.api.mobile.dao.FavoriRepository;
import com.medo.api.mobile.dao.HistoriqueRechercheRepository;
import com.medo.api.mobile.dto.MobileDtos.*;
import com.medo.api.mobile.entity.ClientMobile;
import com.medo.api.mobile.entity.Favori;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClientMobileService {

    private static final Logger log = LoggerFactory.getLogger(ClientMobileService.class);

    @Autowired private ClientMobileRepository        clientRepository;
    @Autowired private FavoriRepository              favoriRepository;
    @Autowired private HistoriqueRechercheRepository historiqueRepository;
    @Autowired private TenantRepository              tenantRepository;

    @Transactional(readOnly = true)
    public ClientMobileResponse getProfil(UUID clientId) {
        ClientMobile client = findOrThrow(clientId);
        ProfilStatsResponse stats = new ProfilStatsResponse(
            historiqueRepository.countByClientId(clientId),
            historiqueRepository.countPharmaciesTouchees(clientId),
            favoriRepository.countByClientId(clientId)
        );
        return toClientResponse(client, stats);
    }

    @Transactional
    public ClientMobileResponse mettreAJourProfil(UUID clientId, String nom, String prenom,
                                                    String telephone, Boolean localisation,
                                                    Boolean notifications) {
        ClientMobile client = findOrThrow(clientId);
        if (nom           != null) client.setNom(nom);
        if (prenom        != null) client.setPrenom(prenom);
        if (telephone     != null) client.setTelephone(telephone);
        if (localisation  != null) client.setLocalisationActivee(localisation);
        if (notifications != null) client.setNotificationsActivees(notifications);
        client.setUpdatedAt(LocalDateTime.now());
        clientRepository.save(client);
        return getProfil(clientId);
    }

    @Transactional(readOnly = true)
    public List<FavoriResponse> getFavoris(UUID clientId) {
        return favoriRepository.findByClientIdOrderByCreatedAtDesc(clientId)
            .stream().map(this::toFavoriResponse).collect(Collectors.toList());
    }

    @Transactional
    public FavoriResponse ajouterFavori(UUID clientId, UUID pharmacieTenantId) {
        Tenant tenant = tenantRepository.findById(pharmacieTenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Pharmacie", pharmacieTenantId.toString()));

        if (favoriRepository.existsByClientIdAndPharmacieTenantId(clientId, pharmacieTenantId))
            throw new DuplicateResourceException("Pharmacie déjà dans vos favoris");

        ClientMobile client = findOrThrow(clientId);

        Favori favori = new Favori();
        favori.setClient(client);
        favori.setPharmacieTenantId(pharmacieTenantId);
        favori.setPharmacieNom(tenant.getNom());

        Favori saved = favoriRepository.save(favori);
        log.info("Favori ajouté : client={}, pharmacie={}", clientId, tenant.getNom());
        return toFavoriResponse(saved);
    }

    @Transactional
    public void retirerFavori(UUID clientId, UUID pharmacieTenantId) {
        if (!favoriRepository.existsByClientIdAndPharmacieTenantId(clientId, pharmacieTenantId))
            throw new ResourceNotFoundException("Favori introuvable");
        favoriRepository.deleteByClientIdAndPharmacieTenantId(clientId, pharmacieTenantId);
        log.info("Favori retiré : client={}, pharmacie={}", clientId, pharmacieTenantId);
    }

    @Transactional(readOnly = true)
    public Page<HistoriqueResponse> getHistorique(UUID clientId, Pageable pageable) {
        return historiqueRepository.findByClientIdOrderByCreatedAtDesc(clientId, pageable)
            .map(h -> new HistoriqueResponse(h.getId(), h.getTermeProduit(),
                h.getCategorie(), h.getNombreResultats(), h.getTempsEcoule(), h.getCreatedAt()));
    }

    @Transactional
    public void viderHistorique(UUID clientId) {
        historiqueRepository.deleteByClientId(clientId);
        log.info("Historique vidé : client={}", clientId);
    }

    private ClientMobile findOrThrow(UUID id) {
        return clientRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Client mobile", id.toString()));
    }

    private ClientMobileResponse toClientResponse(ClientMobile c, ProfilStatsResponse stats) {
        return new ClientMobileResponse(c.getId(), c.getNom(), c.getPrenom(),
            c.getEmail(), c.getTelephone(), c.getAvatarInitiales(),
            Boolean.TRUE.equals(c.getLocalisationActivee()),
            Boolean.TRUE.equals(c.getNotificationsActivees()), stats);
    }

    private FavoriResponse toFavoriResponse(Favori f) {
        return new FavoriResponse(f.getId(), f.getPharmacieTenantId(),
            f.getPharmacieNom(), f.getCreatedAt());
    }
}
