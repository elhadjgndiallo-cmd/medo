package com.medo.api.auth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "utilisateurs",
    uniqueConstraints = @UniqueConstraint(name = "uk_util_email", columnNames = "email"))
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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

    @Enumerated(EnumType.STRING)
    @Column(name = "type_utilisateur", nullable = false, length = 30)
    private TypeUser typeUtilisateur;

    @Column(name = "actif", nullable = false)
    private Boolean actif = true;

    @Column(name = "avatar_initiales", length = 5)
    private String avatarInitiales;

    @Column(name = "dernier_connexion")
    private LocalDateTime dernierConnexion;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "utilisateurs_roles",
        joinColumns = @JoinColumn(name = "utilisateur_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Utilisateur() {}

    public UUID getId()                          { return id; }
    public void setId(UUID v)                    { this.id = v; }
    public String getNom()                       { return nom; }
    public void setNom(String v)                 { this.nom = v; }
    public String getPrenom()                    { return prenom; }
    public void setPrenom(String v)              { this.prenom = v; }
    public String getEmail()                     { return email; }
    public void setEmail(String v)               { this.email = v; }
    public String getMotDePasse()                { return motDePasse; }
    public void setMotDePasse(String v)          { this.motDePasse = v; }
    public String getTelephone()                 { return telephone; }
    public void setTelephone(String v)           { this.telephone = v; }
    public TypeUser getTypeUtilisateur()         { return typeUtilisateur; }
    public void setTypeUtilisateur(TypeUser v)   { this.typeUtilisateur = v; }
    public Boolean getActif()                    { return actif; }
    public void setActif(Boolean v)              { this.actif = v; }
    public String getAvatarInitiales() {
        if (avatarInitiales != null) return avatarInitiales;
        return ((nom != null && !nom.isEmpty() ? nom.substring(0,1) : "") +
                (prenom != null && !prenom.isEmpty() ? prenom.substring(0,1) : "")).toUpperCase();
    }
    public void setAvatarInitiales(String v)     { this.avatarInitiales = v; }
    public LocalDateTime getDernierConnexion()   { return dernierConnexion; }
    public void setDernierConnexion(LocalDateTime v) { this.dernierConnexion = v; }
    public Set<Role> getRoles()                  { return roles; }
    public void setRoles(Set<Role> v)            { this.roles = v; }
    public LocalDateTime getCreatedAt()          { return createdAt; }
    public void setCreatedAt(LocalDateTime v)    { this.createdAt = v; }
    public LocalDateTime getUpdatedAt()          { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v)    { this.updatedAt = v; }

    public void majDernierConnexion() { this.dernierConnexion = LocalDateTime.now(); }

    public enum TypeUser { SUPER_ADMIN, ADMIN_PHARMACIE, EMPLOYE, CLIENT_MOBILE }
}
