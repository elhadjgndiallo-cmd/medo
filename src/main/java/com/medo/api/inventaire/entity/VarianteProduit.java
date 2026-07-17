package com.medo.api.inventaire.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "variantes_produit")
public class VarianteProduit {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    @Column(name = "dosage", length = 100)
    private String dosage;

    @Column(name = "forme", length = 100)
    private String forme;

    @Column(name = "unite", length = 50)
    private String unite;

    @Column(name = "code_barres", length = 100)
    private String codeBarres;

    @Column(name = "prix_vente", precision = 15, scale = 2)
    private BigDecimal prixVente;

    @Column(name = "actif", nullable = false)
    private Boolean actif = true;

    @OneToMany(mappedBy = "variante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Lot> lots = new ArrayList<>();

    @OneToOne(mappedBy = "variante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Stock stock;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public VarianteProduit() {}

    public UUID getId()                    { return id; }
    public void setId(UUID v)              { this.id = v; }
    public Produit getProduit()            { return produit; }
    public void setProduit(Produit v)      { this.produit = v; }
    public String getDosage()              { return dosage; }
    public void setDosage(String v)        { this.dosage = v; }
    public String getForme()               { return forme; }
    public void setForme(String v)         { this.forme = v; }
    public String getUnite()               { return unite; }
    public void setUnite(String v)         { this.unite = v; }
    public String getCodeBarres()          { return codeBarres; }
    public void setCodeBarres(String v)    { this.codeBarres = v; }
    public BigDecimal getPrixVente()       { return prixVente; }
    public void setPrixVente(BigDecimal v) { this.prixVente = v; }
    public Boolean getActif()              { return actif; }
    public void setActif(Boolean v)        { this.actif = v; }
    public List<Lot> getLots()             { return lots; }
    public void setLots(List<Lot> v)       { this.lots = v; }
    public Stock getStock()                { return stock; }
    public void setStock(Stock v)          { this.stock = v; }
    public LocalDateTime getCreatedAt()    { return createdAt; }
    public void setCreatedAt(LocalDateTime v){ this.createdAt = v; }

    public BigDecimal getPrixEffectif() {
        return prixVente != null ? prixVente : (produit != null ? produit.getPrixVente() : BigDecimal.ZERO);
    }

    public String getLibelleComplet() {
        StringBuilder sb = new StringBuilder(produit != null ? produit.getNom() : "");
        if (dosage != null) sb.append(" ").append(dosage);
        if (forme  != null) sb.append(" ").append(forme);
        return sb.toString();
    }
}
