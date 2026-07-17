package com.medo.api.pharmacie.controllers;

import com.medo.api.pharmacie.dto.PharmacieDtos.*;
import com.medo.api.pharmacie.services.PharmacieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pharmacie/config")
@PreAuthorize("hasAuthority('TYPE_ADMIN_PHARMACIE')")
@Tag(name = "Pharmacie Config")
public class PharmacieController {

    @Autowired
    private PharmacieService pharmacieService;

    @GetMapping
    @Operation(summary = "Lire le profil de la pharmacie")
    public ResponseEntity<PharmacieResponse> getProfil() {
        return ResponseEntity.ok(pharmacieService.getProfil());
    }

    @PutMapping
    @Operation(summary = "Configurer le profil général")
    public ResponseEntity<PharmacieResponse> configurerProfil(
            @Valid @RequestBody ConfigurerPharmacieRequest req) {
        return ResponseEntity.ok(pharmacieService.configurerProfil(req));
    }

    @PatchMapping("/white-label")
    @Operation(summary = "Configurer le white-label (logo, couleur)")
    public ResponseEntity<PharmacieResponse> configurerWhiteLabel(
            @Valid @RequestBody WhiteLabelRequest req) {
        return ResponseEntity.ok(pharmacieService.configurerWhiteLabel(req));
    }

    @PatchMapping("/gps")
    @Operation(summary = "Mettre à jour les coordonnées GPS")
    public ResponseEntity<PharmacieResponse> mettreAJourGps(
            @RequestBody GpsRequest req) {
        return ResponseEntity.ok(pharmacieService.mettreAJourGps(req));
    }
}
