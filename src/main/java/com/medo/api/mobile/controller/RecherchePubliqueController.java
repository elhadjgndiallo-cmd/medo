package com.medo.api.mobile.controllers;

import com.medo.api.mobile.dto.MobileDtos.ResultatRechercheResponse;
import com.medo.api.mobile.services.RechercheGeolocService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Endpoint public — sans JWT.
 * Retourne uniquement la disponibilité (boolean), jamais prix ni quantité.
 */
@RestController
@RequestMapping("/api/v1/public")
@Tag(name = "Recherche Publique")
public class RecherchePubliqueController {

    @Autowired
    private RechercheGeolocService rechercheService;

    @GetMapping("/recherche")
    @Operation(summary = "Recherche géolocalisée de médicaments",
               description = "Endpoint public, sans authentification. " +
                             "Cache Redis 5 min. Ne retourne jamais le prix ni le stock exact.")
    public ResponseEntity<List<ResultatRechercheResponse>> rechercher(
            @RequestParam String q,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false, defaultValue = "5.0") Double rayon) {
        return ResponseEntity.ok(
            rechercheService.rechercherMedicament(q, lat, lng, rayon, null));
    }
}
