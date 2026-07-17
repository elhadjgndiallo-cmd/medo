package com.medo.api.achats.controllers;

import com.medo.api.achats.dto.AchatsDtos.*;
import com.medo.api.achats.entity.BonCommande;
import com.medo.api.achats.services.AchatService;
import com.medo.api.security.MedoUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping("/api/v1/achats")
@Tag(name = "Achats")
public class AchatController {

    @Autowired
    private AchatService achatService;

    @GetMapping("/fournisseurs")
    @PreAuthorize("hasAnyAuthority('TYPE_ADMIN_PHARMACIE','TYPE_EMPLOYE')")
    @Operation(summary = "Lister les fournisseurs")
    public ResponseEntity<Page<FournisseurResponse>> listerFournisseurs(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(achatService.listerFournisseurs(search, pageable));
    }

    @PostMapping("/fournisseurs")
    @PreAuthorize("hasAuthority('TYPE_ADMIN_PHARMACIE')")
    @Operation(summary = "Créer un fournisseur")
    public ResponseEntity<FournisseurResponse> creerFournisseur(
            @Valid @RequestBody CreerFournisseurRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(achatService.creerFournisseur(req));
    }

    @PutMapping("/fournisseurs/{id}")
    @PreAuthorize("hasAuthority('TYPE_ADMIN_PHARMACIE')")
    @Operation(summary = "Modifier un fournisseur")
    public ResponseEntity<FournisseurResponse> modifierFournisseur(
            @PathVariable UUID id, @Valid @RequestBody CreerFournisseurRequest req) {
        return ResponseEntity.ok(achatService.modifierFournisseur(id, req));
    }

    @GetMapping("/bons-commande")
    @PreAuthorize("hasAnyAuthority('TYPE_ADMIN_PHARMACIE','TYPE_EMPLOYE')")
    @Operation(summary = "Lister les bons de commande")
    public ResponseEntity<Page<BonCommandeResponse>> listerBonsCommande(
            @RequestParam(required = false) BonCommande.StatutCommande statut,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(achatService.listerBonsCommande(statut, pageable));
    }

    @GetMapping("/bons-commande/{id}")
    @PreAuthorize("hasAnyAuthority('TYPE_ADMIN_PHARMACIE','TYPE_EMPLOYE')")
    @Operation(summary = "Détail d'un bon de commande")
    public ResponseEntity<BonCommandeResponse> getBonCommande(@PathVariable UUID id) {
        return ResponseEntity.ok(achatService.getBonCommande(id));
    }

    @PostMapping("/bons-commande")
    @PreAuthorize("hasAnyAuthority('TYPE_ADMIN_PHARMACIE','TYPE_EMPLOYE')")
    @Operation(summary = "Créer un bon de commande")
    public ResponseEntity<BonCommandeResponse> creerBonCommande(
            @Valid @RequestBody CreerBonCommandeRequest req,
            @AuthenticationPrincipal MedoUserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(achatService.creerBonCommande(req, UUID.fromString(principal.getUserId())));
    }

    @PostMapping("/bons-commande/{id}/recevoir")
    @PreAuthorize("hasAnyAuthority('TYPE_ADMIN_PHARMACIE','TYPE_EMPLOYE')")
    @Operation(summary = "Réceptionner une commande")
    public ResponseEntity<BonCommandeResponse> receptionner(
            @PathVariable UUID id,
            @Valid @RequestBody ReceptionnerBcRequest req,
            @AuthenticationPrincipal MedoUserPrincipal principal) {
        return ResponseEntity.ok(
            achatService.receptionnerCommande(id, req, UUID.fromString(principal.getUserId())));
    }

    @PatchMapping("/bons-commande/{id}/annuler")
    @PreAuthorize("hasAuthority('TYPE_ADMIN_PHARMACIE')")
    @Operation(summary = "Annuler un bon de commande")
    public ResponseEntity<BonCommandeResponse> annuler(@PathVariable UUID id) {
        return ResponseEntity.ok(achatService.annulerCommande(id));
    }

    @GetMapping("/bons-commande/{id}/timeline")
    @PreAuthorize("hasAnyAuthority('TYPE_ADMIN_PHARMACIE','TYPE_EMPLOYE')")
    @Operation(summary = "Timeline d'un bon de commande")
    public ResponseEntity<List<TimelineResponse>> getTimeline(@PathVariable UUID id) {
        return ResponseEntity.ok(achatService.getTimeline(id));
    }
}
