package com.medo.api.inventaire.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stocks",
    uniqueConstraints = @UniqueConstraint(name = "uk_stock_variante", columnNames = "variante_id"))
public class Stock {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variante_id", nullable = false)
    private VarianteProduit variante;

    @Column(name = "quantite_totale", nullable = false)
    private Integer quantiteTotale = 0;

    @Column(name = "seuil_min", nullable = false)
    private Integer seuilMin = 0;

    @Column(name = "seuil_max")
    private Integer seuilMax;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Stock() {}

    public UUID getId()                       { return id; }
    public void setId(UUID v)                 { this.id = v; }
    public VarianteProduit getVariante()      { return variante; }
    public void setVariante(VarianteProduit v){ this.variante = v; }
    public Integer getQuantiteTotale()        { return quantiteTotale; }
    public void setQuantiteTotale(Integer v)  { this.quantiteTotale = v; }
    public Integer getSeuilMin()              { return seuilMin; }
    public void setSeuilMin(Integer v)        { this.seuilMin = v; }
    public Integer getSeuilMax()              { return seuilMax; }
    public void setSeuilMax(Integer v)        { this.seuilMax = v; }
    public LocalDateTime getUpdatedAt()       { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }

    public boolean estEnRupture() { return quantiteTotale == null || quantiteTotale <= 0; }
    public boolean estSousSeuil() { return !estEnRupture() && quantiteTotale <= seuilMin; }

    public void decrementer(int qte) {
        if (qte > quantiteTotale)
            throw new IllegalArgumentException(
                "Stock insuffisant : demandé=" + qte + ", dispo=" + quantiteTotale);
        this.quantiteTotale -= qte;
        this.updatedAt = LocalDateTime.now();
    }

    public void incrementer(int qte) {
        this.quantiteTotale += qte;
        this.updatedAt = LocalDateTime.now();
    }

    public String getStatutBadge() {
        if (estEnRupture()) return "RUPTURE";
        if (estSousSeuil()) return "BAS";
        return "OK";
    }
}
