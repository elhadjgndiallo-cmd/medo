package com.medo.api.common.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.UUID;

public class SuperAdminDtos {

    public static class RejeterDemandeRequest {
        @NotBlank(message = "Le motif est obligatoire")
        private String motif;
        public RejeterDemandeRequest() {}
        public String getMotif()       { return motif; }
        public void setMotif(String v) { this.motif = v; }
    }

    public static class PlateformeStatsResponse {
        private long pharmaciesActives;
        private long planGratuit;
        private long planPro;
        private long demandesEnAttente;
        public PlateformeStatsResponse() {}
        public PlateformeStatsResponse(long a, long g, long p, long att) {
            this.pharmaciesActives = a; this.planGratuit = g;
            this.planPro = p; this.demandesEnAttente = att;
        }
        public long getPharmaciesActives()     { return pharmaciesActives; }
        public void setPharmaciesActives(long v){ this.pharmaciesActives = v; }
        public long getPlanGratuit()           { return planGratuit; }
        public void setPlanGratuit(long v)     { this.planGratuit = v; }
        public long getPlanPro()               { return planPro; }
        public void setPlanPro(long v)         { this.planPro = v; }
        public long getDemandesEnAttente()     { return demandesEnAttente; }
        public void setDemandesEnAttente(long v){ this.demandesEnAttente = v; }
    }

    public static class DemandeResponse {
        private UUID id;
        private String nomPharmacie;
        private String emailContact;
        private String sousDomaineSouhaite;
        private String planDemande;
        private String statut;
        private LocalDateTime createdAt;
        public DemandeResponse() {}
        public DemandeResponse(UUID id, String nom, String email, String sd,
                               String plan, String statut, LocalDateTime dt) {
            this.id = id; this.nomPharmacie = nom; this.emailContact = email;
            this.sousDomaineSouhaite = sd; this.planDemande = plan;
            this.statut = statut; this.createdAt = dt;
        }
        public UUID getId()                         { return id; }
        public void setId(UUID v)                   { this.id = v; }
        public String getNomPharmacie()             { return nomPharmacie; }
        public void setNomPharmacie(String v)       { this.nomPharmacie = v; }
        public String getEmailContact()             { return emailContact; }
        public void setEmailContact(String v)       { this.emailContact = v; }
        public String getSousDomaineSouhaite()      { return sousDomaineSouhaite; }
        public void setSousDomaineSouhaite(String v){ this.sousDomaineSouhaite = v; }
        public String getPlanDemande()              { return planDemande; }
        public void setPlanDemande(String v)        { this.planDemande = v; }
        public String getStatut()                   { return statut; }
        public void setStatut(String v)             { this.statut = v; }
        public LocalDateTime getCreatedAt()         { return createdAt; }
        public void setCreatedAt(LocalDateTime v)   { this.createdAt = v; }
    }

    public static class TenantResponse {
        private UUID id;
        private String nom;
        private String sousDomaine;
        private String schemaName;
        private String statut;
        private String plan;
        private String emailContact;
        private LocalDateTime createdAt;
        public TenantResponse() {}
        public TenantResponse(UUID id, String nom, String sd, String schema,
                              String statut, String plan, String email, LocalDateTime dt) {
            this.id = id; this.nom = nom; this.sousDomaine = sd;
            this.schemaName = schema; this.statut = statut; this.plan = plan;
            this.emailContact = email; this.createdAt = dt;
        }
        public UUID getId()                    { return id; }
        public void setId(UUID v)              { this.id = v; }
        public String getNom()                 { return nom; }
        public void setNom(String v)           { this.nom = v; }
        public String getSousDomaine()         { return sousDomaine; }
        public void setSousDomaine(String v)   { this.sousDomaine = v; }
        public String getSchemaName()          { return schemaName; }
        public void setSchemaName(String v)    { this.schemaName = v; }
        public String getStatut()              { return statut; }
        public void setStatut(String v)        { this.statut = v; }
        public String getPlan()                { return plan; }
        public void setPlan(String v)          { this.plan = v; }
        public String getEmailContact()        { return emailContact; }
        public void setEmailContact(String v)  { this.emailContact = v; }
        public LocalDateTime getCreatedAt()    { return createdAt; }
        public void setCreatedAt(LocalDateTime v){ this.createdAt = v; }
    }
}
