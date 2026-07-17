package com.medo.api.inventaire.controllers;

import com.medo.api.inventaire.dto.InventaireDtos.*;
import com.medo.api.inventaire.services.ProduitService;
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
@RequestMapping("/api/v1/inventaire")
@Tag(name = "Inventaire")
public class ProduitController {

    @Autowired
    private ProduitService produitService;

    @GetMapping("/stats")
    @PreAuthorize("hasAnyAuthority('TYPE_ADMIN_PHARMACIE','TYPE_EMPLOYE')")
    @Operation(summary = "Statistiques inventaire")
    public ResponseEntity<StatsInventaireResponse> getStats() {
        return ResponseEntity.ok(produitService.getStats());
    }

    @GetMapping("/produits")
    @PreAuthorize("hasAnyAuthority('TYPE_ADMIN_PHARMACIE','TYPE_EMPLOYE')")
    @Operation(summary = "Lister les produits")
    public ResponseEntity<Page<ProduitResponse>> listerProduits(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(produitService.listerProduits(search, pageable));
    }

    @GetMapping("/produits/{id}")
    @PreAuthorize("hasAnyAuthority('TYPE_ADMIN_PHARMACIE','TYPE_EMPLOYE')")
    @Operation(summary = "Détail produit")
    public ResponseEntity<ProduitResponse> getProduit(@PathVariable UUID id) {
        return ResponseEntity.ok(produitService.getProduit(id));
    }

    @GetMapping("/produits/barcode/{code}")
    @PreAuthorize("hasAnyAuthority('TYPE_ADMIN_PHARMACIE','TYPE_EMPLOYE')")
    @Operation(summary = "Produit par code-barres")
    public ResponseEntity<ProduitResponse> getParCodeBarres(@PathVariable String code) {
        return ResponseEntity.ok(produitService.getParCodeBarres(code));
    }

    @GetMapping("/produits/categories")
    @PreAuthorize("hasAnyAuthority('TYPE_ADMIN_PHARMACIE','TYPE_EMPLOYE')")
    @Operation(summary = "Liste des catégories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(produitService.getCategories());
    }

    @PostMapping("/produits")
    @PreAuthorize("hasAuthority('TYPE_ADMIN_PHARMACIE')")
    @Operation(summary = "Créer un produit")
    public ResponseEntity<ProduitResponse> creerProduit(@Valid @RequestBody CreerProduitRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(produitService.creerProduit(req));
    }

    @PutMapping("/produits/{id}")
    @PreAuthorize("hasAuthority('TYPE_ADMIN_PHARMACIE')")
    @Operation(summary = "Modifier un produit")
    public ResponseEntity<ProduitResponse> modifierProduit(
            @PathVariable UUID id, @Valid @RequestBody CreerProduitRequest req) {
        return ResponseEntity.ok(produitService.modifierProduit(id, req));
    }

    @DeleteMapping("/produits/{id}")
    @PreAuthorize("hasAuthority('TYPE_ADMIN_PHARMACIE')")
    @Operation(summary = "Désactiver un produit")
    public ResponseEntity<Void> desactiver(@PathVariable UUID id) {
        produitService.desactiverProduit(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/variantes")
    @PreAuthorize("hasAuthority('TYPE_ADMIN_PHARMACIE')")
    @Operation(summary = "Créer une variante")
    public ResponseEntity<VarianteResponse> creerVariante(@Valid @RequestBody CreerVarianteRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(produitService.creerVariante(req));
    }

    @PostMapping("/lots")
    @PreAuthorize("hasAnyAuthority('TYPE_ADMIN_PHARMACIE','TYPE_EMPLOYE')")
    @Operation(summary = "Créer un lot")
    public ResponseEntity<LotResponse> creerLot(
            @Valid @RequestBody CreerLotRequest req,
            @AuthenticationPrincipal MedoUserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(produitService.creerLot(req, UUID.fromString(principal.getUserId())));
    }
}
