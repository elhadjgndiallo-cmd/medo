package com.medo.api.achats.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "fournisseurs")
public class Fournisseur {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nom", nullable = false, length = 200)
    private String nom;

    @Column(name = "contact", length = 200)
    private String contact;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "adresse", columnDefinition = "TEXT")
    private String adresse;

    @Column(name = "actif", nullable = false)
    private Boolean actif = true;

    @OneToMany(mappedBy = "fournisseur", fetch = FetchType.LAZY)
    private List<BonCommande> bonsCommande = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Fournisseur() {}

    public UUID getId()                              { return id; }
    public void setId(UUID v)                        { this.id = v; }
    public String getNom()                           { return nom; }
    public void setNom(String v)                     { this.nom = v; }
    public String getContact()                       { return contact; }
    public void setContact(String v)                 { this.contact = v; }
    public String getEmail()                         { return email; }
    public void setEmail(String v)                   { this.email = v; }
    public String getAdresse()                       { return adresse; }
    public void setAdresse(String v)                 { this.adresse = v; }
    public Boolean getActif()                        { return actif; }
    public void setActif(Boolean v)                  { this.actif = v; }
    public List<BonCommande> getBonsCommande()        { return bonsCommande; }
    public void setBonsCommande(List<BonCommande> v)  { this.bonsCommande = v; }
    public LocalDateTime getCreatedAt()              { return createdAt; }
    public void setCreatedAt(LocalDateTime v)        { this.createdAt = v; }
    public LocalDateTime getUpdatedAt()              { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v)        { this.updatedAt = v; }
}
