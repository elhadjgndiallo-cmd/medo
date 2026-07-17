package com.medo.api.mobile.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "clients_mobile", schema = "public",
    uniqueConstraints = @UniqueConstraint(name = "uk_client_mobile_email", columnNames = "email"))
public class ClientMobile {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse;

    @Column(name = "telephone", length = 30)
    private String telephone;

    @Column(name = "avatar_initiales", length = 5)
    private String avatarInitiales;

    @Column(name = "localisation_activee", nullable = false)
    private Boolean localisationActivee = true;

    @Column(name = "notifications_activees", nullable = false)
    private Boolean notificationsActivees = true;

    @Column(name = "actif", nullable = false)
    private Boolean actif = true;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Favori> favoris = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HistoriqueRecherche> historique = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public ClientMobile() {}

    public UUID getId()                                { return id; }
    public void setId(UUID v)                          { this.id = v; }
    public String getNom()                             { return nom; }
    public void setNom(String v)                       { this.nom = v; }
    public String getPrenom()                          { return prenom; }
    public void setPrenom(String v)                    { this.prenom = v; }
    public String getEmail()                           { return email; }
    public void setEmail(String v)                     { this.email = v; }
    public String getMotDePasse()                      { return motDePasse; }
    public void setMotDePasse(String v)                { this.motDePasse = v; }
    public String getTelephone()                       { return telephone; }
    public void setTelephone(String v)                 { this.telephone = v; }
    public Boolean getLocalisationActivee()            { return localisationActivee; }
    public void setLocalisationActivee(Boolean v)      { this.localisationActivee = v; }
    public Boolean getNotificationsActivees()          { return notificationsActivees; }
    public void setNotificationsActivees(Boolean v)    { this.notificationsActivees = v; }
    public Boolean getActif()                          { return actif; }
    public void setActif(Boolean v)                    { this.actif = v; }
    public List<Favori> getFavoris()                   { return favoris; }
    public void setFavoris(List<Favori> v)             { this.favoris = v; }
    public List<HistoriqueRecherche> getHistorique()   { return historique; }
    public void setHistorique(List<HistoriqueRecherche> v){ this.historique = v; }
    public LocalDateTime getCreatedAt()                { return createdAt; }
    public void setCreatedAt(LocalDateTime v)          { this.createdAt = v; }
    public LocalDateTime getUpdatedAt()                { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v)          { this.updatedAt = v; }

    public String getAvatarInitiales() {
        if (avatarInitiales != null) return avatarInitiales;
        String n = nom != null && !nom.isEmpty() ? nom.substring(0,1) : "";
        String p = prenom != null && !prenom.isEmpty() ? prenom.substring(0,1) : "";
        return (n + p).toUpperCase();
    }
    public void setAvatarInitiales(String v) { this.avatarInitiales = v; }
}
