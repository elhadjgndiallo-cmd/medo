package com.medo.api.inventaire.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(name = "lots")
public class Lot {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variante_id", nullable = false)
    private VarianteProduit variante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emplacement_id")
    private Emplacement emplacement;

    @Column(name = "numero_lot", nullable = false, length = 100)
    private String numeroLot;

    @Column(name = "date_fabrication")
    private LocalDate dateFabrication;

    @Column(name = "date_peremption", nullable = false)
    private LocalDate datePeremption;

    @Column(name = "quantite", nullable = false)
    private Integer quantite = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Lot() {}

    public UUID getId()                       { return id; }
    public void setId(UUID v)                 { this.id = v; }
    public VarianteProduit getVariante()      { return variante; }
    public void setVariante(VarianteProduit v){ this.variante = v; }
    public Emplacement getEmplacement()       { return emplacement; }
    public void setEmplacement(Emplacement v) { this.emplacement = v; }
    public String getNumeroLot()              { return numeroLot; }
    public void setNumeroLot(String v)        { this.numeroLot = v; }
    public LocalDate getDateFabrication()     { return dateFabrication; }
    public void setDateFabrication(LocalDate v){ this.dateFabrication = v; }
    public LocalDate getDatePeremption()      { return datePeremption; }
    public void setDatePeremption(LocalDate v){ this.datePeremption = v; }
    public Integer getQuantite()              { return quantite; }
    public void setQuantite(Integer v)        { this.quantite = v; }
    public LocalDateTime getCreatedAt()       { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }

    public boolean estPerime() { return LocalDate.now().isAfter(datePeremption); }

    public long joursAvantPeremption() {
        return ChronoUnit.DAYS.between(LocalDate.now(), datePeremption);
    }

    public String getStatutPeremption() {
        long j = joursAvantPeremption();
        if (j < 0)   return "PERIME";
        if (j <= 30) return "URGENT";
        if (j <= 90) return "SOON";
        return "OK";
    }
}
