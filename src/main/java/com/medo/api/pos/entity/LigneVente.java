package com.medo.api.pos.entity;

import com.medo.api.inventaire.entity.Lot;
import com.medo.api.inventaire.entity.VarianteProduit;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "lignes_vente")
public class LigneVente {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vente_id", nullable = false)
    private Vente vente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variante_id", nullable = false)
    private VarianteProduit variante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id")
    private Lot lot;

    @Column(name = "quantite", nullable = false)
    private Integer quantite;

    @Column(name = "prix_unitaire", nullable = false, precision = 15, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(name = "sous_total", nullable = false, precision = 15, scale = 2)
    private BigDecimal sousTotal;

    public LigneVente() {}

    public UUID getId()                      { return id; }
    public void setId(UUID v)                { this.id = v; }
    public Vente getVente()                  { return vente; }
    public void setVente(Vente v)            { this.vente = v; }
    public VarianteProduit getVariante()     { return variante; }
    public void setVariante(VarianteProduit v){ this.variante = v; }
    public Lot getLot()                      { return lot; }
    public void setLot(Lot v)               { this.lot = v; }
    public Integer getQuantite()             { return quantite; }
    public void setQuantite(Integer v)       { this.quantite = v; }
    public BigDecimal getPrixUnitaire()      { return prixUnitaire; }
    public void setPrixUnitaire(BigDecimal v){ this.prixUnitaire = v; }
    public BigDecimal getSousTotal()         { return sousTotal; }
    public void setSousTotal(BigDecimal v)   { this.sousTotal = v; }
}
