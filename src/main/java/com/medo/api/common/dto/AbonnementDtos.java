package com.medo.api.common.dto;

import com.medo.api.common.entity.Abonnement;
import com.medo.api.common.entity.Tenant;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class AbonnementDtos {

    public static class CreerAbonnementRequest {
        @NotNull
        private UUID tenantId;
        
        @NotNull
        private Tenant.PlanAbonnement plan;
        
        @NotNull
        private Abonnement.PeriodeFacturation periodeFacturation;

        public CreerAbonnementRequest() {}

        public UUID getTenantId() { return tenantId; }
        public void setTenantId(UUID v) { this.tenantId = v; }
        public Tenant.PlanAbonnement getPlan() { return plan; }
        public void setPlan(Tenant.PlanAbonnement v) { this.plan = v; }
        public Abonnement.PeriodeFacturation getPeriodeFacturation() { return periodeFacturation; }
        public void setPeriodeFacturation(Abonnement.PeriodeFacturation v) { this.periodeFacturation = v; }
    }

    public static class AbonnementResponse {
        private UUID id;
        private UUID tenantId;
        private String tenantNom;
        private Tenant.PlanAbonnement plan;
        private LocalDate dateDebut;
        private LocalDate dateFin;
        private Abonnement.StatutAbonnement statut;
        private BigDecimal montant;
        private Abonnement.PeriodeFacturation periodeFacturation;
        private Boolean autoRenouvelable;
        private Boolean essaiGratuit;
        private LocalDate dateFinEssai;

        public AbonnementResponse() {}

        public AbonnementResponse(UUID id, UUID tenantId, String tenantNom, Tenant.PlanAbonnement plan,
                                 LocalDate debut, LocalDate fin, Abonnement.StatutAbonnement statut,
                                 BigDecimal montant, Abonnement.PeriodeFacturation periode,
                                 Boolean autoRenew, Boolean essai, LocalDate finEssai) {
            this.id = id;
            this.tenantId = tenantId;
            this.tenantNom = tenantNom;
            this.plan = plan;
            this.dateDebut = debut;
            this.dateFin = fin;
            this.statut = statut;
            this.montant = montant;
            this.periodeFacturation = periode;
            this.autoRenouvelable = autoRenew;
            this.essaiGratuit = essai;
            this.dateFinEssai = finEssai;
        }

        // Getters & Setters
        public UUID getId() { return id; }
        public void setId(UUID v) { this.id = v; }
        public UUID getTenantId() { return tenantId; }
        public void setTenantId(UUID v) { this.tenantId = v; }
        public String getTenantNom() { return tenantNom; }
        public void setTenantNom(String v) { this.tenantNom = v; }
        public Tenant.PlanAbonnement getPlan() { return plan; }
        public void setPlan(Tenant.PlanAbonnement v) { this.plan = v; }
        public LocalDate getDateDebut() { return dateDebut; }
        public void setDateDebut(LocalDate v) { this.dateDebut = v; }
        public LocalDate getDateFin() { return dateFin; }
        public void setDateFin(LocalDate v) { this.dateFin = v; }
        public Abonnement.StatutAbonnement getStatut() { return statut; }
        public void setStatut(Abonnement.StatutAbonnement v) { this.statut = v; }
        public BigDecimal getMontant() { return montant; }
        public void setMontant(BigDecimal v) { this.montant = v; }
        public Abonnement.PeriodeFacturation getPeriodeFacturation() { return periodeFacturation; }
        public void setPeriodeFacturation(Abonnement.PeriodeFacturation v) { this.periodeFacturation = v; }
        public Boolean getAutoRenouvelable() { return autoRenouvelable; }
        public void setAutoRenouvelable(Boolean v) { this.autoRenouvelable = v; }
        public Boolean getEssaiGratuit() { return essaiGratuit; }
        public void setEssaiGratuit(Boolean v) { this.essaiGratuit = v; }
        public LocalDate getDateFinEssai() { return dateFinEssai; }
        public void setDateFinEssai(LocalDate v) { this.dateFinEssai = v; }
    }
}
