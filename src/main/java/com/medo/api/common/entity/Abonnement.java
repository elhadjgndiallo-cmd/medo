package com.medo.api.common.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "abonnements", schema = "public")
public class Abonnement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan", nullable = false, length = 20)
    private Tenant.PlanAbonnement plan;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    @Column(name = "date_renouvellement")
    private LocalDate dateRenouvellement;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private StatutAbonnement statut = StatutAbonnement.ACTIF;

    @Column(name = "montant", precision = 10, scale = 2)
    private BigDecimal montant;

    @Enumerated(EnumType.STRING)
    @Column(name = "periode_facturation", length = 20)
    private PeriodeFacturation periodeFacturation = PeriodeFacturation.MENSUEL;

    @Column(name = "auto_renouvelable", nullable = false)
    private Boolean autoRenouvelable = true;

    @Column(name = "essai_gratuit", nullable = false)
    private Boolean essaiGratuit = false;

    @Column(name = "date_fin_essai")
    private LocalDate dateFinEssai;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Abonnement() {}

    public UUID getId() { return id; }
    public void setId(UUID v) { this.id = v; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant v) { this.tenant = v; }
    public Tenant.PlanAbonnement getPlan() { return plan; }
    public void setPlan(Tenant.PlanAbonnement v) { this.plan = v; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate v) { this.dateDebut = v; }
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate v) { this.dateFin = v; }
    public LocalDate getDateRenouvellement() { return dateRenouvellement; }
    public void setDateRenouvellement(LocalDate v) { this.dateRenouvellement = v; }
    public StatutAbonnement getStatut() { return statut; }
    public void setStatut(StatutAbonnement v) { this.statut = v; }
    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal v) { this.montant = v; }
    public PeriodeFacturation getPeriodeFacturation() { return periodeFacturation; }
    public void setPeriodeFacturation(PeriodeFacturation v) { this.periodeFacturation = v; }
    public Boolean getAutoRenouvelable() { return autoRenouvelable; }
    public void setAutoRenouvelable(Boolean v) { this.autoRenouvelable = v; }
    public Boolean getEssaiGratuit() { return essaiGratuit; }
    public void setEssaiGratuit(Boolean v) { this.essaiGratuit = v; }
    public LocalDate getDateFinEssai() { return dateFinEssai; }
    public void setDateFinEssai(LocalDate v) { this.dateFinEssai = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }

    public boolean isExpire() {
        return LocalDate.now().isAfter(dateFin);
    }

    public boolean estEnEssai() {
        return Boolean.TRUE.equals(essaiGratuit) && 
               dateFinEssai != null && 
               LocalDate.now().isBefore(dateFinEssai);
    }

    public void suspendre() {
        this.statut = StatutAbonnement.SUSPENDU;
        this.updatedAt = LocalDateTime.now();
    }

    public void reactiver() {
        this.statut = StatutAbonnement.ACTIF;
        this.updatedAt = LocalDateTime.now();
    }

    public void renouveler() {
        this.dateDebut = this.dateFin.plusDays(1);
        this.dateFin = calculerDateFin(this.dateDebut, this.periodeFacturation);
        this.statut = StatutAbonnement.ACTIF;
        this.updatedAt = LocalDateTime.now();
    }

    private LocalDate calculerDateFin(LocalDate debut, PeriodeFacturation periode) {
        return switch (periode) {
            case MENSUEL -> debut.plusMonths(1);
            case TRIMESTRIEL -> debut.plusMonths(3);
            case SEMESTRIEL -> debut.plusMonths(6);
            case ANNUEL -> debut.plusYears(1);
        };
    }

    public enum StatutAbonnement { ACTIF, EXPIRE, SUSPENDU, ANNULE }
    public enum PeriodeFacturation { MENSUEL, TRIMESTRIEL, SEMESTRIEL, ANNUEL }
}
