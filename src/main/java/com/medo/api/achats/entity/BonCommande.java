package com.medo.api.achats.entity;

import com.medo.api.auth.entity.Utilisateur;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "bons_commande",
    uniqueConstraints = @UniqueConstraint(name = "uk_bc_ref", columnNames = "reference"))
public class BonCommande {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "reference", nullable = false, length = 50)
    private String reference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fournisseur_id", nullable = false)
    private Fournisseur fournisseur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Utilisateur createdBy;

    @Column(name = "date_commande", nullable = false)
    private LocalDate dateCommande = LocalDate.now();

    @Column(name = "date_livraison_prevue")
    private LocalDate dateLivraisonPrevue;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 30)
    private StatutCommande statut = StatutCommande.CONFIRME;

    @Column(name = "montant_total", nullable = false, precision = 15, scale = 2)
    private BigDecimal montantTotal = BigDecimal.ZERO;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "bonCommande", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LigneCommande> lignes = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public BonCommande() {}

    public UUID getId()                              { return id; }
    public void setId(UUID v)                        { this.id = v; }
    public String getReference()                     { return reference; }
    public void setReference(String v)               { this.reference = v; }
    public Fournisseur getFournisseur()               { return fournisseur; }
    public void setFournisseur(Fournisseur v)         { this.fournisseur = v; }
    public Utilisateur getCreatedBy()                { return createdBy; }
    public void setCreatedBy(Utilisateur v)          { this.createdBy = v; }
    public LocalDate getDateCommande()               { return dateCommande; }
    public void setDateCommande(LocalDate v)         { this.dateCommande = v; }
    public LocalDate getDateLivraisonPrevue()        { return dateLivraisonPrevue; }
    public void setDateLivraisonPrevue(LocalDate v)  { this.dateLivraisonPrevue = v; }
    public StatutCommande getStatut()                { return statut; }
    public void setStatut(StatutCommande v)          { this.statut = v; }
    public BigDecimal getMontantTotal()              { return montantTotal; }
    public void setMontantTotal(BigDecimal v)        { this.montantTotal = v; }
    public String getNotes()                         { return notes; }
    public void setNotes(String v)                   { this.notes = v; }
    public List<LigneCommande> getLignes()           { return lignes; }
    public void setLignes(List<LigneCommande> v)     { this.lignes = v; }
    public LocalDateTime getCreatedAt()              { return createdAt; }
    public void setCreatedAt(LocalDateTime v)        { this.createdAt = v; }
    public LocalDateTime getUpdatedAt()              { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v)        { this.updatedAt = v; }

    public boolean isTotalementRecu() {
        return !lignes.isEmpty() && lignes.stream().allMatch(LigneCommande::estTotalementRecu);
    }

    public boolean isPartiellementRecu() {
        boolean auMoinsUne = lignes.stream().anyMatch(l -> l.getQuantiteRecue() > 0);
        boolean pasTout    = lignes.stream().anyMatch(l -> l.getQuantiteRecue() < l.getQuantiteCmd());
        return auMoinsUne && pasTout;
    }

    public enum StatutCommande { BROUILLON, CONFIRME, PARTIELLEMENT_RECU, RECU, ANNULE }
}
