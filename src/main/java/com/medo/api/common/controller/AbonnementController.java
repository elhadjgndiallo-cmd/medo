package com.medo.api.common.controller;

import com.medo.api.common.entity.Abonnement;
import com.medo.api.common.entity.Tenant;
import com.medo.api.common.service.SuperAdminService;
import com.medo.api.common.dto.AbonnementDtos.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/abonnements")
@PreAuthorize("hasAuthority('TYPE_SUPER_ADMIN')")
@Tag(name = "Gestion des Abonnements (Super Admin)")
public class AbonnementController {

    @Autowired
    private SuperAdminService superAdminService;

    @GetMapping("/tenant/{tenantId}")
    @Operation(summary = "Lister les abonnements d'un tenant")
    public ResponseEntity<List<AbonnementResponse>> getAbonnementsTenant(@PathVariable UUID tenantId) {
        List<Abonnement> abonnements = superAdminService.getAbonnementsTenant(tenantId);
        return ResponseEntity.ok(
            abonnements.stream()
                .map(this::toAbonnementResponse)
                .collect(Collectors.toList())
        );
    }

    @PostMapping
    @Operation(summary = "Créer un nouvel abonnement")
    public ResponseEntity<AbonnementResponse> creerAbonnement(@Valid @RequestBody CreerAbonnementRequest req) {
        Abonnement abonnement = superAdminService.creerAbonnement(
            req.getTenantId(),
            req.getPlan(),
            req.getPeriodeFacturation()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toAbonnementResponse(abonnement));
    }

    private AbonnementResponse toAbonnementResponse(Abonnement a) {
        return new AbonnementResponse(
            a.getId(),
            a.getTenant() != null ? a.getTenant().getId() : null,
            a.getTenant() != null ? a.getTenant().getNom() : null,
            a.getPlan(),
            a.getDateDebut(),
            a.getDateFin(),
            a.getStatut(),
            a.getMontant(),
            a.getPeriodeFacturation(),
            a.getAutoRenouvelable(),
            a.getEssaiGratuit(),
            a.getDateFinEssai()
        );
    }
}
