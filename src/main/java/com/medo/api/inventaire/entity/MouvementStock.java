package com.medo.api.inventaire.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "mouvements_stock")
public class MouvementStock {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id", nullable = false)
    private Lot lot;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private TypeMouvement type;

    @Column(name = "quantite", nullable = false)
    private Integer quantite;

    @Column(name = "motif", length = 200)
    private String motif;

    @Column(name = "reference_id")
    private UUID referenceId;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public MouvementStock() {}

    public UUID getId()                    { return id; }
    public void setId(UUID v)              { this.id = v; }
    public Lot getLot()                    { return lot; }
    public void setLot(Lot v)              { this.lot = v; }
    public TypeMouvement getType()         { return type; }
    public void setType(TypeMouvement v)   { this.type = v; }
    public Integer getQuantite()           { return quantite; }
    public void setQuantite(Integer v)     { this.quantite = v; }
    public String getMotif()               { return motif; }
    public void setMotif(String v)         { this.motif = v; }
    public UUID getReferenceId()           { return referenceId; }
    public void setReferenceId(UUID v)     { this.referenceId = v; }
    public UUID getCreatedBy()             { return createdBy; }
    public void setCreatedBy(UUID v)       { this.createdBy = v; }
    public LocalDateTime getCreatedAt()    { return createdAt; }
    public void setCreatedAt(LocalDateTime v){ this.createdAt = v; }

    public enum TypeMouvement { ENTREE, SORTIE, AJUSTEMENT, REBUT }
}
