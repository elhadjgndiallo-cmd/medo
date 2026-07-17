package com.medo.api.pharmacie.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pharmacie")
public class Pharmacie {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nom", nullable = false, length = 150)
    private String nom;

    @Column(name = "adresse", nullable = false, length = 500)
    private String adresse;

    @Column(name = "ville", length = 100)
    private String ville;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "telephone", length = 30)
    private String telephone;

    @Column(name = "horaires", columnDefinition = "TEXT")
    private String horaires;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "couleur_primaire", length = 20)
    private String couleurPrimaire = "#1B3A6B";

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Pharmacie() {}

    public UUID getId()                        { return id; }
    public void setId(UUID v)                  { this.id = v; }
    public String getNom()                     { return nom; }
    public void setNom(String v)               { this.nom = v; }
    public String getAdresse()                 { return adresse; }
    public void setAdresse(String v)           { this.adresse = v; }
    public String getVille()                   { return ville; }
    public void setVille(String v)             { this.ville = v; }
    public Double getLatitude()                { return latitude; }
    public void setLatitude(Double v)          { this.latitude = v; }
    public Double getLongitude()               { return longitude; }
    public void setLongitude(Double v)         { this.longitude = v; }
    public String getTelephone()               { return telephone; }
    public void setTelephone(String v)         { this.telephone = v; }
    public String getHoraires()                { return horaires; }
    public void setHoraires(String v)          { this.horaires = v; }
    public String getLogoUrl()                 { return logoUrl; }
    public void setLogoUrl(String v)           { this.logoUrl = v; }
    public String getCouleurPrimaire()         { return couleurPrimaire; }
    public void setCouleurPrimaire(String v)   { this.couleurPrimaire = v; }
    public LocalDateTime getCreatedAt()        { return createdAt; }
    public void setCreatedAt(LocalDateTime v)  { this.createdAt = v; }
    public LocalDateTime getUpdatedAt()        { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v)  { this.updatedAt = v; }

    public boolean aCoordonnees() {
        return latitude != null && longitude != null;
    }
}
