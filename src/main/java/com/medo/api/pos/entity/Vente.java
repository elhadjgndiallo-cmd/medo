package com.medo.api.pos.entity;

import com.medo.api.auth.entity.Utilisateur;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ventes",
    uniqueConstraints = @UniqueConstraint(name = "uk_vente_ticket", columnNames = "numero_ticket"))
public class Vente {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private SessionCaisse session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @Column(name = "date_vente", nullable = false)
    private LocalDateTime dateVente = LocalDateTime.now();

    @Column(name = "numero_ticket", nullable = false, length = 50)
    private String numeroTicket;

    @Column(name = "montant_total", nullable = false, precision = 15, scale = 2)
    private BigDecimal montantTotal = BigDecimal.ZERO;

    @Column(name = "montant_remise", nullable = false, precision = 15, scale = 2)
    private BigDecimal montantRemise = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode_paiement", nullable = false, length = 30)
    private ModePaiement modePaiement = ModePaiement.ESPECES;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private StatutVente statut = StatutVente.VALIDEE;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "vente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LigneVente> lignes = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Vente() {}

    public UUID getId()                          { return id; }
    public void setId(UUID v)                    { this.id = v; }
    public SessionCaisse getSession()            { return session; }
    public void setSession(SessionCaisse v)      { this.session = v; }
    public Client getClient()                    { return client; }
    public void setClient(Client v)              { this.client = v; }
    public Utilisateur getUtilisateur()          { return utilisateur; }
    public void setUtilisateur(Utilisateur v)    { this.utilisateur = v; }
    public LocalDateTime getDateVente()          { return dateVente; }
    public void setDateVente(LocalDateTime v)    { this.dateVente = v; }
    public String getNumeroTicket()              { return numeroTicket; }
    public void setNumeroTicket(String v)        { this.numeroTicket = v; }
    public BigDecimal getMontantTotal()          { return montantTotal; }
    public void setMontantTotal(BigDecimal v)    { this.montantTotal = v; }
    public BigDecimal getMontantRemise()         { return montantRemise; }
    public void setMontantRemise(BigDecimal v)   { this.montantRemise = v; }
    public ModePaiement getModePaiement()        { return modePaiement; }
    public void setModePaiement(ModePaiement v)  { this.modePaiement = v; }
    public StatutVente getStatut()               { return statut; }
    public void setStatut(StatutVente v)         { this.statut = v; }
    public String getNotes()                     { return notes; }
    public void setNotes(String v)               { this.notes = v; }
    public List<LigneVente> getLignes()          { return lignes; }
    public void setLignes(List<LigneVente> v)    { this.lignes = v; }
    public LocalDateTime getCreatedAt()          { return createdAt; }
    public void setCreatedAt(LocalDateTime v)    { this.createdAt = v; }

    public void annuler() { this.statut = StatutVente.ANNULEE; }

    public enum ModePaiement  { ESPECES, MOBILE_MONEY, CARTE, CREDIT }
    public enum StatutVente   { VALIDEE, ANNULEE, EN_ATTENTE }
}
