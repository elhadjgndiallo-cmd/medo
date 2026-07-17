package com.medo.api.common.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "demandes_inscription", schema = "public",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_demande_email",        columnNames = "email_contact"),
        @UniqueConstraint(name = "uk_demande_sous_domaine", columnNames = "sous_domaine_souhaite")
    })
public class DemandeInscription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nom_pharmacie", nullable = false, length = 150)
    private String nomPharmacie;

    @Column(name = "email_contact", nullable = false, length = 255)
    private String emailContact;

    @Column(name = "mot_de_passe_hash", nullable = false)
    private String motDePasseHash;

    @Column(name = "sous_domaine_souhaite", nullable = false, length = 100)
    private String sousDomaineSouhaite;

    @Column(name = "adresse", nullable = false, length = 500)
    private String adresse;

    @Column(name = "telephone", length = 30)
    private String telephone;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_demande", nullable = false, length = 20)
    private Tenant.PlanAbonnement planDemande = Tenant.PlanAbonnement.GRATUIT;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private StatutDemande statut = StatutDemande.EN_ATTENTE;

    @Column(name = "motif_rejet", length = 500)
    private String motifRejet;

    @Column(name = "tenant_id")
    private UUID tenantId;

    @Column(name = "traite_par")
    private UUID traiteParId;

    @Column(name = "traite_le")
    private LocalDateTime traiteLe;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public DemandeInscription() {}

    public UUID getId()                             { return id; }
    public void setId(UUID v)                       { this.id = v; }
    public String getNomPharmacie()                 { return nomPharmacie; }
    public void setNomPharmacie(String v)           { this.nomPharmacie = v; }
    public String getEmailContact()                 { return emailContact; }
    public void setEmailContact(String v)           { this.emailContact = v; }
    public String getMotDePasseHash()               { return motDePasseHash; }
    public void setMotDePasseHash(String v)         { this.motDePasseHash = v; }
    public String getSousDomaineSouhaite()          { return sousDomaineSouhaite; }
    public void setSousDomaineSouhaite(String v)    { this.sousDomaineSouhaite = v; }
    public String getAdresse()                      { return adresse; }
    public void setAdresse(String v)                { this.adresse = v; }
    public String getTelephone()                    { return telephone; }
    public void setTelephone(String v)              { this.telephone = v; }
    public Tenant.PlanAbonnement getPlanDemande()   { return planDemande; }
    public void setPlanDemande(Tenant.PlanAbonnement v){ this.planDemande = v; }
    public StatutDemande getStatut()                { return statut; }
    public void setStatut(StatutDemande v)          { this.statut = v; }
    public String getMotifRejet()                   { return motifRejet; }
    public void setMotifRejet(String v)             { this.motifRejet = v; }
    public UUID getTenantId()                       { return tenantId; }
    public void setTenantId(UUID v)                 { this.tenantId = v; }
    public UUID getTraiteParId()                    { return traiteParId; }
    public void setTraiteParId(UUID v)              { this.traiteParId = v; }
    public LocalDateTime getTraiteLe()              { return traiteLe; }
    public void setTraiteLe(LocalDateTime v)        { this.traiteLe = v; }
    public LocalDateTime getCreatedAt()             { return createdAt; }
    public void setCreatedAt(LocalDateTime v)       { this.createdAt = v; }
    public LocalDateTime getUpdatedAt()             { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v)       { this.updatedAt = v; }

    public void accepter(UUID tenantId, UUID superAdminId) {
        this.statut = StatutDemande.ACCEPTEE;
        this.tenantId = tenantId;
        this.traiteParId = superAdminId;
        this.traiteLe = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void rejeter(String motif, UUID superAdminId) {
        this.statut = StatutDemande.REJETEE;
        this.motifRejet = motif;
        this.traiteParId = superAdminId;
        this.traiteLe = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isEnAttente() { return StatutDemande.EN_ATTENTE.equals(this.statut); }

    public enum StatutDemande { EN_ATTENTE, ACCEPTEE, REJETEE }
}
