package com.medo.api.mobile.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(name = "historique_recherches", schema = "public")
public class HistoriqueRecherche {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientMobile client;

    @Column(name = "terme_produit", nullable = false, length = 200)
    private String termeProduit;

    @Column(name = "categorie", length = 100)
    private String categorie;

    @Column(name = "nombre_resultats", nullable = false)
    private Integer nombreResultats = 0;

    @Column(name = "latitude_client")
    private Double latitudeClient;

    @Column(name = "longitude_client")
    private Double longitudeClient;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public HistoriqueRecherche() {}

    public UUID getId()                        { return id; }
    public void setId(UUID v)                  { this.id = v; }
    public ClientMobile getClient()            { return client; }
    public void setClient(ClientMobile v)      { this.client = v; }
    public String getTermeProduit()            { return termeProduit; }
    public void setTermeProduit(String v)      { this.termeProduit = v; }
    public String getCategorie()               { return categorie; }
    public void setCategorie(String v)         { this.categorie = v; }
    public Integer getNombreResultats()        { return nombreResultats; }
    public void setNombreResultats(Integer v)  { this.nombreResultats = v; }
    public Double getLatitudeClient()          { return latitudeClient; }
    public void setLatitudeClient(Double v)    { this.latitudeClient = v; }
    public Double getLongitudeClient()         { return longitudeClient; }
    public void setLongitudeClient(Double v)   { this.longitudeClient = v; }
    public LocalDateTime getCreatedAt()        { return createdAt; }
    public void setCreatedAt(LocalDateTime v)  { this.createdAt = v; }

    public String getTempsEcoule() {
        long min = ChronoUnit.MINUTES.between(createdAt, LocalDateTime.now());
        if (min < 60) return "il y a " + min + " min";
        long h = ChronoUnit.HOURS.between(createdAt, LocalDateTime.now());
        if (h < 24) return "il y a " + h + "h";
        return "il y a " + ChronoUnit.DAYS.between(createdAt, LocalDateTime.now()) + "j";
    }
}
