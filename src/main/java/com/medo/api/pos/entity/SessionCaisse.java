package com.medo.api.pos.entity;

import com.medo.api.auth.entity.Utilisateur;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sessions_caisse")
public class SessionCaisse {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caisse_id", nullable = false)
    private Caisse caisse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @Column(name = "date_ouverture", nullable = false)
    private LocalDateTime dateOuverture = LocalDateTime.now();

    @Column(name = "date_fermeture")
    private LocalDateTime dateFermeture;

    @Column(name = "fond_caisse", nullable = false, precision = 15, scale = 2)
    private BigDecimal fondCaisse = BigDecimal.ZERO;

    @Column(name = "montant_cloture", precision = 15, scale = 2)
    private BigDecimal montantCloture;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private StatutSession statut = StatutSession.OUVERTE;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "session", fetch = FetchType.LAZY)
    private List<Vente> ventes = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public SessionCaisse() {}

    public UUID getId()                          { return id; }
    public void setId(UUID v)                    { this.id = v; }
    public Caisse getCaisse()                    { return caisse; }
    public void setCaisse(Caisse v)              { this.caisse = v; }
    public Utilisateur getUtilisateur()          { return utilisateur; }
    public void setUtilisateur(Utilisateur v)    { this.utilisateur = v; }
    public LocalDateTime getDateOuverture()      { return dateOuverture; }
    public void setDateOuverture(LocalDateTime v){ this.dateOuverture = v; }
    public LocalDateTime getDateFermeture()      { return dateFermeture; }
    public void setDateFermeture(LocalDateTime v){ this.dateFermeture = v; }
    public BigDecimal getFondCaisse()            { return fondCaisse; }
    public void setFondCaisse(BigDecimal v)      { this.fondCaisse = v; }
    public BigDecimal getMontantCloture()        { return montantCloture; }
    public void setMontantCloture(BigDecimal v)  { this.montantCloture = v; }
    public StatutSession getStatut()             { return statut; }
    public void setStatut(StatutSession v)       { this.statut = v; }
    public String getNotes()                     { return notes; }
    public void setNotes(String v)               { this.notes = v; }
    public List<Vente> getVentes()               { return ventes; }
    public void setVentes(List<Vente> v)         { this.ventes = v; }
    public LocalDateTime getCreatedAt()          { return createdAt; }
    public void setCreatedAt(LocalDateTime v)    { this.createdAt = v; }

    public void fermer(BigDecimal montantCloture) {
        this.statut = StatutSession.FERMEE;
        this.dateFermeture = LocalDateTime.now();
        this.montantCloture = montantCloture;
    }

    public BigDecimal calculerEcart() {
        if (montantCloture == null) return BigDecimal.ZERO;
        BigDecimal totalVentes = ventes.stream()
            .filter(v -> Vente.StatutVente.VALIDEE.equals(v.getStatut()))
            .map(Vente::getMontantTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        return montantCloture.subtract(fondCaisse.add(totalVentes));
    }

    public String getDureeOuverte() {
        LocalDateTime fin = dateFermeture != null ? dateFermeture : LocalDateTime.now();
        Duration d = Duration.between(dateOuverture, fin);
        return String.format("%dh%02d", d.toHours(), d.toMinutesPart());
    }

    public enum StatutSession { OUVERTE, FERMEE }
}
