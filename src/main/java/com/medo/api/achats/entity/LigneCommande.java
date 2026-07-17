package com.medo.api.achats.entity;

import com.medo.api.inventaire.entity.VarianteProduit;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "lignes_commande")
public class LigneCommande {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bc_id", nullable = false)
    private BonCommande bonCommande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variante_id", nullable = false)
    private VarianteProduit variante;

    @Column(name = "quantite_cmd", nullable = false)
    private Integer quantiteCmd;

    @Column(name = "quantite_recue", nullable = false)
    private Integer quantiteRecue = 0;

    @Column(name = "prix_unitaire", nullable = false, precision = 15, scale = 2)
    private BigDecimal prixUnitaire = BigDecimal.ZERO;

    @Column(name = "sous_total", nullable = false, precision = 15, scale = 2)
    private BigDecimal sousTotal = BigDecimal.ZERO;

    public LigneCommande() {}

    public UUID getId()                          { return id; }
    public void setId(UUID v)                    { this.id = v; }
    public BonCommande getBonCommande()          { return bonCommande; }
    public void setBonCommande(BonCommande v)    { this.bonCommande = v; }
    public VarianteProduit getVariante()         { return variante; }
    public void setVariante(VarianteProduit v)   { this.variante = v; }
    public Integer getQuantiteCmd()              { return quantiteCmd; }
    public void setQuantiteCmd(Integer v)        { this.quantiteCmd = v; }
    public Integer getQuantiteRecue()            { return quantiteRecue; }
    public void setQuantiteRecue(Integer v)      { this.quantiteRecue = v; }
    public BigDecimal getPrixUnitaire()          { return prixUnitaire; }
    public void setPrixUnitaire(BigDecimal v)    { this.prixUnitaire = v; }
    public BigDecimal getSousTotal()             { return sousTotal; }
    public void setSousTotal(BigDecimal v)       { this.sousTotal = v; }

    public boolean estTotalementRecu()           { return quantiteRecue >= quantiteCmd; }
    public int getQuantiteRestante()             { return quantiteCmd - quantiteRecue; }
    public void recevoirQuantite(int qte)        { this.quantiteRecue += qte; }
}
