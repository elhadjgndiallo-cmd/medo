package com.medo.api.common.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "domaines_personnalises", schema = "public",
    uniqueConstraints = @UniqueConstraint(name = "uk_domaine", columnNames = "nom_domaine"))
public class DomainePersonnalise {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(name = "nom_domaine", nullable = false, length = 255)
    private String nomDomaine;

    @Column(name = "actif", nullable = false)
    private Boolean actif = false;

    @Column(name = "verifie", nullable = false)
    private Boolean verifie = false;

    @Column(name = "certificat_ssl", nullable = false)
    private Boolean certificatSsl = false;

    @Column(name = "date_verification")
    private LocalDateTime dateVerification;

    @Column(name = "token_verification", length = 100)
    private String tokenVerification;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public DomainePersonnalise() {}

    public UUID getId() { return id; }
    public void setId(UUID v) { this.id = v; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant v) { this.tenant = v; }
    public String getNomDomaine() { return nomDomaine; }
    public void setNomDomaine(String v) { this.nomDomaine = v; }
    public Boolean getActif() { return actif; }
    public void setActif(Boolean v) { this.actif = v; }
    public Boolean getVerifie() { return verifie; }
    public void setVerifie(Boolean v) { this.verifie = v; }
    public Boolean getCertificatSsl() { return certificatSsl; }
    public void setCertificatSsl(Boolean v) { this.certificatSsl = v; }
    public LocalDateTime getDateVerification() { return dateVerification; }
    public void setDateVerification(LocalDateTime v) { this.dateVerification = v; }
    public String getTokenVerification() { return tokenVerification; }
    public void setTokenVerification(String v) { this.tokenVerification = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }

    public void verifier() {
        this.verifie = true;
        this.actif = true;
        this.dateVerification = LocalDateTime.now();
        this.tokenVerification = null;
    }
}
