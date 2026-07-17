package com.medo.api.common.controllers;

import com.medo.api.common.dto.SuperAdminDtos.*;
import com.medo.api.common.services.TenantService;
import com.medo.api.security.MedoUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasAuthority('TYPE_SUPER_ADMIN')")
@Tag(name = "Super Admin")
public class SuperAdminController {

    @Autowired
    private TenantService tenantService;

    @GetMapping("/stats")
    @Operation(summary = "Statistiques plateforme")
    public ResponseEntity<PlateformeStatsResponse> getStats() {
        return ResponseEntity.ok(tenantService.getStats());
    }

    @GetMapping("/demandes")
    @Operation(summary = "Toutes les demandes")
    public ResponseEntity<List<DemandeResponse>> getToutesDemandes() {
        return ResponseEntity.ok(tenantService.getToutesDemandes());
    }

    @GetMapping("/demandes/en-attente")
    @Operation(summary = "Demandes en attente")
    public ResponseEntity<List<DemandeResponse>> getDemandesEnAttente() {
        return ResponseEntity.ok(tenantService.getDemandesEnAttente());
    }

    @PatchMapping("/demandes/{id}/accepter")
    @Operation(summary = "Valider une demande")
    public ResponseEntity<TenantResponse> accepter(
            @PathVariable UUID id,
            @AuthenticationPrincipal MedoUserPrincipal principal) {
        return ResponseEntity.ok(
            tenantService.accepterDemande(id, UUID.fromString(principal.getUserId())));
    }

    @PatchMapping("/demandes/{id}/rejeter")
    @Operation(summary = "Rejeter une demande")
    public ResponseEntity<Void> rejeter(
            @PathVariable UUID id,
            @Valid @RequestBody RejeterDemandeRequest req,
            @AuthenticationPrincipal MedoUserPrincipal principal) {
        tenantService.rejeterDemande(id, req.getMotif(), UUID.fromString(principal.getUserId()));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pharmacies")
    @Operation(summary = "Lister les pharmacies")
    public ResponseEntity<List<TenantResponse>> getAllPharmacies() {
        return ResponseEntity.ok(tenantService.getAllTenants());
    }

    @GetMapping("/pharmacies/{id}")
    @Operation(summary = "Détail pharmacie")
    public ResponseEntity<TenantResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(tenantService.getTenant(id));
    }

    @PatchMapping("/pharmacies/{id}/suspendre")
    @Operation(summary = "Suspendre une pharmacie")
    public ResponseEntity<TenantResponse> suspendre(@PathVariable UUID id) {
        return ResponseEntity.ok(tenantService.suspendre(id));
    }

    @PatchMapping("/pharmacies/{id}/reactiver")
    @Operation(summary = "Réactiver une pharmacie")
    public ResponseEntity<TenantResponse> reactiver(@PathVariable UUID id) {
        return ResponseEntity.ok(tenantService.reactiver(id));
    }
}
