package com.medo.api.pos.controllers;

import com.medo.api.pos.dto.PosDtos.*;
import com.medo.api.pos.services.VenteService;
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
@RequestMapping("/api/v1/pos")
@Tag(name = "Point de Vente")
public class VenteController {

    @Autowired
    private VenteService venteService;

    @GetMapping("/stats")
    @PreAuthorize("hasAnyAuthority('TYPE_ADMIN_PHARMACIE','TYPE_EMPLOYE')")
    @Operation(summary = "Statistiques POS")
    public ResponseEntity<StatsPOSResponse> getStats() {
        return ResponseEntity.ok(venteService.getStats());
    }

    // ── Caisses ──

    @GetMapping("/caisses")
    @PreAuthorize("hasAnyAuthority('TYPE_ADMIN_PHARMACIE','TYPE_EMPLOYE')")
    @Operation(summary = "Lister les caisses")
    public ResponseEntity<List<CaisseResponse>> listerCaisses() {
        return ResponseEntity.ok(venteService.listerCaisses());
    }

    @PostMapping("/caisses")
    @PreAuthorize("hasAuthority('TYPE_ADMIN_PHARMACIE')")
    @Operation(summary = "Créer une caisse")
    public ResponseEntity<CaisseResponse> creerCaisse(
            @RequestParam String nom,
            @RequestParam(required = false) String reference) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(venteService.creerCaisse(nom, reference));
    }

    // ── Sessions ──

    @PostMapping("/sessions/ouvrir")
    @PreAuthorize("hasAnyAuthority('TYPE_ADMIN_PHARMACIE','TYPE_EMPLOYE')")
    @Operation(summary = "Ouvrir une session de caisse")
    public ResponseEntity<SessionCaisseResponse> ouvrirSession(
            @Valid @RequestBody OuvrirSessionRequest req,
            @AuthenticationPrincipal MedoUserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(venteService.ouvrirSession(req, UUID.fromString(principal.getUserId())));
    }

    @PatchMapping("/sessions/{id}/fermer")
    @PreAuthorize("hasAnyAuthority('TYPE_ADMIN_PHARMACIE','TYPE_EMPLOYE')")
    @Operation(summary = "Fermer une session de caisse")
    public ResponseEntity<SessionCaisseResponse> fermerSession(
            @PathVariable UUID id,
            @Valid @RequestBody FermerSessionRequest req,
            @AuthenticationPrincipal MedoUserPrincipal principal) {
        return ResponseEntity.ok(
            venteService.fermerSession(id, req, UUID.fromString(principal.getUserId())));
    }

    @GetMapping("/sessions/{id}")
    @PreAuthorize("hasAnyAuthority('TYPE_ADMIN_PHARMACIE','TYPE_EMPLOYE')")
    @Operation(summary = "Détail d'une session")
    public ResponseEntity<SessionCaisseResponse> getSession(@PathVariable UUID id) {
        return ResponseEntity.ok(venteService.getSession(id));
    }

    // ── Ventes ──

    @PostMapping("/ventes")
    @PreAuthorize("hasAnyAuthority('TYPE_ADMIN_PHARMACIE','TYPE_EMPLOYE')")
    @Operation(summary = "Enregistrer une vente")
    public ResponseEntity<VenteResponse> creerVente(
            @Valid @RequestBody CreerVenteRequest req,
            @AuthenticationPrincipal MedoUserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(venteService.creerVente(req, UUID.fromString(principal.getUserId())));
    }

    @GetMapping("/ventes/session/{sessionId}")
    @PreAuthorize("hasAnyAuthority('TYPE_ADMIN_PHARMACIE','TYPE_EMPLOYE')")
    @Operation(summary = "Ventes d'une session")
    public ResponseEntity<Page<VenteResponse>> getVentesSession(
            @PathVariable UUID sessionId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(venteService.getVentesSession(sessionId, pageable));
    }

    @PatchMapping("/ventes/{id}/annuler")
    @PreAuthorize("hasAnyAuthority('TYPE_ADMIN_PHARMACIE','TYPE_EMPLOYE')")
    @Operation(summary = "Annuler une vente")
    public ResponseEntity<VenteResponse> annulerVente(
            @PathVariable UUID id,
            @AuthenticationPrincipal MedoUserPrincipal principal) {
        return ResponseEntity.ok(
            venteService.annulerVente(id, UUID.fromString(principal.getUserId())));
    }

    // ── Clients ──

    @GetMapping("/clients")
    @PreAuthorize("hasAnyAuthority('TYPE_ADMIN_PHARMACIE','TYPE_EMPLOYE')")
    @Operation(summary = "Lister les clients")
    public ResponseEntity<Page<ClientResponse>> listerClients(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(venteService.listerClients(search, pageable));
    }

    @PostMapping("/clients")
    @PreAuthorize("hasAnyAuthority('TYPE_ADMIN_PHARMACIE','TYPE_EMPLOYE')")
    @Operation(summary = "Créer un client")
    public ResponseEntity<ClientResponse> creerClient(@Valid @RequestBody CreerClientRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(venteService.creerClient(req));
    }
}
