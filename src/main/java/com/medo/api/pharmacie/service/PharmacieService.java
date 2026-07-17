package com.medo.api.pharmacie.services;

import com.medo.api.exception.GlobalExceptionHandler.ResourceNotFoundException;
import com.medo.api.pharmacie.dao.PharmacieRepository;
import com.medo.api.pharmacie.dto.PharmacieDtos.*;
import com.medo.api.pharmacie.entity.Pharmacie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class PharmacieService {

    private static final Logger log = LoggerFactory.getLogger(PharmacieService.class);

    @Autowired
    private PharmacieRepository pharmacieRepository;

    @Transactional(readOnly = true)
    public PharmacieResponse getProfil() {
        Pharmacie p = pharmacieRepository.findFirstByOrderByCreatedAtAsc()
            .orElseThrow(() -> new ResourceNotFoundException(
                "Profil pharmacie non configuré."));
        return toResponse(p);
    }

    @Transactional
    public PharmacieResponse configurerProfil(ConfigurerPharmacieRequest req) {
        Pharmacie p = pharmacieRepository.findFirstByOrderByCreatedAtAsc()
            .orElseGet(Pharmacie::new);

        p.setNom(req.getNom());
        p.setAdresse(req.getAdresse());
        p.setVille(req.getVille());
        p.setTelephone(req.getTelephone());
        p.setUpdatedAt(LocalDateTime.now());
        if (req.getLatitude()  != null) p.setLatitude(req.getLatitude());
        if (req.getLongitude() != null) p.setLongitude(req.getLongitude());
        if (req.getHoraires()  != null) p.setHoraires(req.getHoraires());

        Pharmacie saved = pharmacieRepository.save(p);
        log.info("Profil pharmacie mis à jour : {}", saved.getNom());
        return toResponse(saved);
    }

    @Transactional
    public PharmacieResponse configurerWhiteLabel(WhiteLabelRequest req) {
        Pharmacie p = pharmacieRepository.findFirstByOrderByCreatedAtAsc()
            .orElseThrow(() -> new ResourceNotFoundException("Profil pharmacie introuvable."));

        if (req.getLogoUrl()       != null) p.setLogoUrl(req.getLogoUrl());
        if (req.getCouleurPrimaire()!= null) p.setCouleurPrimaire(req.getCouleurPrimaire());
        p.setUpdatedAt(LocalDateTime.now());

        Pharmacie saved = pharmacieRepository.save(p);
        log.info("White-label mis à jour : logo={}, couleur={}",
            saved.getLogoUrl(), saved.getCouleurPrimaire());
        return toResponse(saved);
    }

    @Transactional
    public PharmacieResponse mettreAJourGps(GpsRequest req) {
        Pharmacie p = pharmacieRepository.findFirstByOrderByCreatedAtAsc()
            .orElseThrow(() -> new ResourceNotFoundException("Profil pharmacie introuvable."));

        p.setLatitude(req.getLatitude());
        p.setLongitude(req.getLongitude());
        p.setUpdatedAt(LocalDateTime.now());

        Pharmacie saved = pharmacieRepository.save(p);
        log.info("GPS mis à jour : ({}, {})", saved.getLatitude(), saved.getLongitude());
        return toResponse(saved);
    }

    private PharmacieResponse toResponse(Pharmacie p) {
        return new PharmacieResponse(
            p.getId(), p.getNom(), p.getAdresse(), p.getVille(),
            p.getLatitude(), p.getLongitude(), p.getTelephone(),
            p.getHoraires(), p.getLogoUrl(), p.getCouleurPrimaire(),
            p.aCoordonnees(), p.getUpdatedAt()
        );
    }
}
