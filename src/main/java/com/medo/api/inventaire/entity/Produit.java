package com.medo.api.inventaire.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "produits")
public class Produit {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nom", nullable = false, length = 200)
    private String nom;

    @Column(name = "dci", length = 200)
    private String dci;

    @Column(name = "categorie", length = 100)
    private String categorie;

    @Column(name = "code_barres", length = 100)
    private String codeBarres;

    @Column(name = "icone_type", length = 50)
    private String iconeType = "pill";

    @Column(name = "actif", nullable = false)
    private Boolean actif = true;

    @Column(name = "prix_vente", nullable = false, precision = 15, scale = 2)
    private BigDecimal prixVente = BigDecimal.ZERO;

    @Column(name = "prix_achat", nullable = false, precision = 15, scale = 2)
    private BigDecimal prixAchat = BigDecimal.ZERO;

    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VarianteProduit> variantes = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Produit() {}

    public UUID getId()                      { return id; }
    public void setId(UUID v)                { this.id = v; }
    public String getNom()                   { return nom; }
    public void setNom(String v)             { this.nom = v; }
    public String getDci()                   { return dci; }
    public void setDci(String v)             { this.dci = v; }
    public String getCategorie()             { return categorie; }
    public void setCategorie(String v)       { this.categorie = v; }
    public String getCodeBarres()            { return codeBarres; }
    public void setCodeBarres(String v)      { this.codeBarres = v; }
    public String getIconeType()             { return iconeType; }
    public void setIconeType(String v)       { this.iconeType = v; }
    public Boolean getActif()                { return actif; }
    public void setActif(Boolean v)          { this.actif = v; }
    public BigDecimal getPrixVente()         { return prixVente; }
    public void setPrixVente(BigDecimal v)   { this.prixVente = v; }
    public BigDecimal getPrixAchat()         { return prixAchat; }
    public void setPrixAchat(BigDecimal v)   { this.prixAchat = v; }
    public List<VarianteProduit> getVariantes()          { return variantes; }
    public void setVariantes(List<VarianteProduit> v)    { this.variantes = v; }
    public LocalDateTime getCreatedAt()      { return createdAt; }
    public void setCreatedAt(LocalDateTime v){ this.createdAt = v; }
    public LocalDateTime getUpdatedAt()      { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v){ this.updatedAt = v; }

    public void desactiver() {
        this.actif = false;
        this.updatedAt = LocalDateTime.now();
    }
}
