package com.medo.api.mobile.controllers;

import com.medo.api.mobile.dto.MobileDtos.*;
import com.medo.api.mobile.services.ClientMobileService;
import com.medo.api.mobile.services.RechercheGeolocService;
import com.medo.api.security.MedoUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/clients")
@PreAuthorize("hasAuthority('TYPE_CLIENT_MOBILE')")
@Tag(name = "Client Mobile")
public class ClientMobileController {

    @Autowired private ClientMobileService   clientService;
    @Autowired private RechercheGeolocService rechercheService;

    // ── Profil ──

    @GetMapping("/me")
    @Operation(summary = "Profil du client connecté")
    public ResponseEntity<ClientMobileResponse> getProfil(
            @AuthenticationPrincipal MedoUserPrincipal principal) {
        return ResponseEntity.ok(
            clientService.getProfil(UUID.fromString(principal.getUserId())));
    }

    @PatchMapping("/me")
    @Operation(summary = "Mettre à jour le profil")
    public ResponseEntity<ClientMobileResponse> mettreAJourProfil(
            @AuthenticationPrincipal MedoUserPrincipal principal,
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String prenom,
            @RequestParam(required = false) String telephone,
            @RequestParam(required = false) Boolean localisation,
            @RequestParam(required = false) Boolean notifications) {
        return ResponseEntity.ok(clientService.mettreAJourProfil(
            UUID.fromString(principal.getUserId()),
            nom, prenom, telephone, localisation, notifications));
    }

    // ── Recherche authentifiée ──

    @GetMapping("/me/recherche")
    @Operation(summary = "Recherche authentifiée (enregistre l'historique)")
    public ResponseEntity<List<ResultatRechercheResponse>> rechercher(
            @RequestParam String q,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false, defaultValue = "5.0") Double rayon,
            @AuthenticationPrincipal MedoUserPrincipal principal) {
        UUID clientId = UUID.fromString(principal.getUserId());
        List<ResultatRechercheResponse> resultats =
            rechercheService.rechercherMedicament(q, lat, lng, rayon, clientId);
        return ResponseEntity.ok(resultats);
    }

    // ── Favoris ──

    @GetMapping("/me/favoris")
    @Operation(summary = "Liste des pharmacies favorites")
    public ResponseEntity<List<FavoriResponse>> getFavoris(
            @AuthenticationPrincipal MedoUserPrincipal principal) {
        return ResponseEntity.ok(
            clientService.getFavoris(UUID.fromString(principal.getUserId())));
    }

    @PostMapping("/me/favoris/{pharmacieTenantId}")
    @Operation(summary = "Ajouter une pharmacie aux favoris")
    public ResponseEntity<FavoriResponse> ajouterFavori(
            @PathVariable UUID pharmacieTenantId,
            @AuthenticationPrincipal MedoUserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            clientService.ajouterFavori(
                UUID.fromString(principal.getUserId()), pharmacieTenantId));
    }

    @DeleteMapping("/me/favoris/{pharmacieTenantId}")
    @Operation(summary = "Retirer une pharmacie des favoris")
    public ResponseEntity<Void> retirerFavori(
            @PathVariable UUID pharmacieTenantId,
            @AuthenticationPrincipal MedoUserPrincipal principal) {
        clientService.retirerFavori(
            UUID.fromString(principal.getUserId()), pharmacieTenantId);
        return ResponseEntity.noContent().build();
    }

    // ── Historique ──

    @GetMapping("/me/historique")
    @Operation(summary = "Historique des recherches")
    public ResponseEntity<Page<HistoriqueResponse>> getHistorique(
            @AuthenticationPrincipal MedoUserPrincipal principal,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(
            clientService.getHistorique(
                UUID.fromString(principal.getUserId()), pageable));
    }

    @DeleteMapping("/me/historique")
    @Operation(summary = "Vider l'historique")
    public ResponseEntity<Void> viderHistorique(
            @AuthenticationPrincipal MedoUserPrincipal principal) {
        clientService.viderHistorique(UUID.fromString(principal.getUserId()));
        return ResponseEntity.noContent().build();
    }
}
