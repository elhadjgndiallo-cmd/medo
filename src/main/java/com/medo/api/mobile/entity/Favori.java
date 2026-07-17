package com.medo.api.mobile.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "favoris_mobile", schema = "public",
    uniqueConstraints = @UniqueConstraint(name = "uk_favori_client_pharmacie",
        columnNames = {"client_id", "pharmacie_tenant_id"}))
public class Favori {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientMobile client;

    @Column(name = "pharmacie_tenant_id", nullable = false)
    private UUID pharmacieTenantId;

    @Column(name = "pharmacie_nom", nullable = false, length = 200)
    private String pharmacieNom;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Favori() {}

    public UUID getId()                       { return id; }
    public void setId(UUID v)                 { this.id = v; }
    public ClientMobile getClient()           { return client; }
    public void setClient(ClientMobile v)     { this.client = v; }
    public UUID getPharmacieTenantId()        { return pharmacieTenantId; }
    public void setPharmacieTenantId(UUID v)  { this.pharmacieTenantId = v; }
    public String getPharmacieNom()           { return pharmacieNom; }
    public void setPharmacieNom(String v)     { this.pharmacieNom = v; }
    public LocalDateTime getCreatedAt()       { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
}
