package com.medo.api.rapports.controller;

import com.medo.api.rapports.dto.RapportsDtos.*;
import com.medo.api.rapports.service.StatistiqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/rapports")
@PreAuthorize("hasAnyAuthority('TYPE_ADMIN_PHARMACIE','TYPE_EMPLOYE')")
@Tag(name = "Rapports et Statistiques")
public class RapportController {

    @Autowired
    private StatistiqueService statistiqueService;

    @GetMapping("/dashboard")
    @Operation(summary = "Statistiques du tableau de bord")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        
        if (dateDebut == null) dateDebut = LocalDate.now().minusDays(30);
        if (dateFin == null) dateFin = LocalDate.now();
        
        return ResponseEntity.ok(statistiqueService.getStatsDashboard(dateDebut, dateFin));
    }

    @GetMapping("/ventes/par-jour")
    @Operation(summary = "Ventes par jour sur une période")
    public ResponseEntity<List<VenteParJourResponse>> getVentesParJour(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        
        return ResponseEntity.ok(statistiqueService.getVentesParJour(dateDebut, dateFin));
    }

    @GetMapping("/produits/top")
    @Operation(summary = "Top produits les plus vendus")
    public ResponseEntity<List<TopProduitResponse>> getTopProduits(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(defaultValue = "10") int limit) {
        
        if (dateDebut == null) dateDebut = LocalDate.now().minusDays(30);
        if (dateFin == null) dateFin = LocalDate.now();
        
        return ResponseEntity.ok(statistiqueService.getTopProduits(dateDebut, dateFin, limit));
    }

    @GetMapping("/inventaire")
    @Operation(summary = "Rapport d'inventaire complet")
    public ResponseEntity<RapportInventaireResponse> getRapportInventaire() {
        return ResponseEntity.ok(statistiqueService.getRapportInventaire());
    }

    @GetMapping("/ventes")
    @Operation(summary = "Rapport des ventes sur une période")
    public ResponseEntity<RapportVentesResponse> getRapportVentes(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        
        return ResponseEntity.ok(statistiqueService.getRapportVentes(dateDebut, dateFin));
    }

    @GetMapping("/statistiques/avancees")
    @PreAuthorize("hasAuthority('TYPE_ADMIN_PHARMACIE')")
    @Operation(summary = "Statistiques avancées (admin uniquement)")
    public ResponseEntity<Map<String, Object>> getStatistiquesAvancees(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        
        if (dateDebut == null) dateDebut = LocalDate.now().minusDays(30);
        if (dateFin == null) dateFin = LocalDate.now();
        
        return ResponseEntity.ok(statistiqueService.getStatistiquesAvancees(dateDebut, dateFin));
    }
}
