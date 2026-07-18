package com.medo.api.common.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tenants", schema = "public",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_tenant_sous_domaine", columnNames = "sous_domaine"),
        @UniqueConstraint(name = "uk_tenant_schema_name",  columnNames = "schema_name")
    })
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nom", nullable = false, length = 150)
    private String nom;

    @Column(name = "sous_domaine", nullable = false, length = 100)
    private String sousDomaine;

    @Column(name = "domain_personnalise", length = 255)
    private String domainPersonnalise;

    @Column(name = "schema_name", nullable = false, length = 100)
    private String schemaName;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private StatutTenant statut = StatutTenant.EN_ATTENTE;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan", nullable = false, length = 20)
    private PlanAbonnement plan = PlanAbonnement.GRATUIT;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "couleur_primaire", length = 20)
    private String couleurPrimaire = "#1B3A6B";

    @Column(name = "couleur_secondaire", length = 20)
    private String couleurSecondaire = "#059669";

    @Column(name = "email_contact", nullable = false, length = 255)
    private String emailContact;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Tenant() {}

    public UUID getId()                        { return id; }
    public void setId(UUID v)                  { this.id = v; }
    public String getNom()                     { return nom; }
    public void setNom(String v)               { this.nom = v; }
    public String getSousDomaine()             { return sousDomaine; }
    public void setSousDomaine(String v)       { this.sousDomaine = v; }
    public String getDomainPersonnalise()      { return domainPersonnalise; }
    public void setDomainPersonnalise(String v){ this.domainPersonnalise = v; }
    public String getSchemaName()              { return schemaName; }
    public void setSchemaName(String v)        { this.schemaName = v; }
    public StatutTenant getStatut()            { return statut; }
    public void setStatut(StatutTenant v)      { this.statut = v; }
    public PlanAbonnement getPlan()            { return plan; }
    public void setPlan(PlanAbonnement v)      { this.plan = v; }
    public String getLogoUrl()                 { return logoUrl; }
    public void setLogoUrl(String v)           { this.logoUrl = v; }
    public String getCouleurPrimaire()         { return couleurPrimaire; }
    public void setCouleurPrimaire(String v)   { this.couleurPrimaire = v; }
    public String getCouleurSecondaire()       { return couleurSecondaire; }
    public void setCouleurSecondaire(String v) { this.couleurSecondaire = v; }
    public String getEmailContact()            { return emailContact; }
    public void setEmailContact(String v)      { this.emailContact = v; }
    public LocalDateTime getCreatedAt()        { return createdAt; }
    public void setCreatedAt(LocalDateTime v)  { this.createdAt = v; }
    public LocalDateTime getUpdatedAt()        { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v)  { this.updatedAt = v; }

    public void valider()   { this.statut = StatutTenant.ACTIF; this.updatedAt = LocalDateTime.now(); }
    public void suspendre() { this.statut = StatutTenant.SUSPENDU; this.updatedAt = LocalDateTime.now(); }
    public void reactiver() { this.statut = StatutTenant.ACTIF; this.updatedAt = LocalDateTime.now(); }
    public boolean isActif(){ return StatutTenant.ACTIF.equals(this.statut); }
    
    // Méthodes additionnelles pour SuperAdminService
    public String getSlug() { return sousDomaine; }
    public void setActif(boolean actif) { 
        this.statut = actif ? StatutTenant.ACTIF : StatutTenant.SUSPENDU; 
        this.updatedAt = LocalDateTime.now();
    }

    public static String buildSchemaName(String sousDomaine) {
        return sousDomaine.toLowerCase().replaceAll("[^a-z0-9_]", "_") + "_schema";
    }

    public enum StatutTenant   { EN_ATTENTE, ACTIF, SUSPENDU }
    public enum PlanAbonnement { GRATUIT, PRO }
}
