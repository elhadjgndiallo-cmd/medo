package com.medo.api.auth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "roles",
    uniqueConstraints = @UniqueConstraint(name = "uk_role_nom", columnNames = "nom"))
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "est_systeme", nullable = false)
    private Boolean estSysteme = false;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "roles_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions = new HashSet<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Role() {}
    public Role(UUID id, String nom, String description, Boolean estSysteme) {
        this.id = id; this.nom = nom;
        this.description = description; this.estSysteme = estSysteme;
    }

    public UUID getId()                      { return id; }
    public void setId(UUID v)                { this.id = v; }
    public String getNom()                   { return nom; }
    public void setNom(String v)             { this.nom = v; }
    public String getDescription()           { return description; }
    public void setDescription(String v)     { this.description = v; }
    public Boolean getEstSysteme()           { return estSysteme; }
    public void setEstSysteme(Boolean v)     { this.estSysteme = v; }
    public Set<Permission> getPermissions()  { return permissions; }
    public void setPermissions(Set<Permission> v) { this.permissions = v; }
    public LocalDateTime getCreatedAt()      { return createdAt; }
    public void setCreatedAt(LocalDateTime v){ this.createdAt = v; }

    public boolean hasPermission(String module, String action) {
        return permissions.stream().anyMatch(p ->
            p.getModule().name().equals(module) && p.getAction().name().equals(action));
    }
}
