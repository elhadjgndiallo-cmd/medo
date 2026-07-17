package com.medo.api.common.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tickets_support", schema = "public")
public class TicketSupport {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(name = "numero_ticket", nullable = false, unique = true, length = 50)
    private String numeroTicket;

    @Column(name = "sujet", nullable = false, length = 200)
    private String sujet;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "priorite", nullable = false, length = 20)
    private Priorite priorite = Priorite.NORMALE;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private StatutTicket statut = StatutTicket.OUVERT;

    @Enumerated(EnumType.STRING)
    @Column(name = "categorie", nullable = false, length = 50)
    private Categorie categorie;

    @Column(name = "email_contact", nullable = false, length = 255)
    private String emailContact;

    @Column(name = "telephone_contact", length = 30)
    private String telephoneContact;

    @Column(name = "assigne_a")
    private UUID assigneA;

    @Column(name = "date_resolution")
    private LocalDateTime dateResolution;

    @Column(name = "commentaire_resolution", columnDefinition = "TEXT")
    private String commentaireResolution;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public TicketSupport() {}

    public UUID getId() { return id; }
    public void setId(UUID v) { this.id = v; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant v) { this.tenant = v; }
    public String getNumeroTicket() { return numeroTicket; }
    public void setNumeroTicket(String v) { this.numeroTicket = v; }
    public String getSujet() { return sujet; }
    public void setSujet(String v) { this.sujet = v; }
    public String getDescription() { return description; }
    public void setDescription(String v) { this.description = v; }
    public Priorite getPriorite() { return priorite; }
    public void setPriorite(Priorite v) { this.priorite = v; }
    public StatutTicket getStatut() { return statut; }
    public void setStatut(StatutTicket v) { this.statut = v; }
    public Categorie getCategorie() { return categorie; }
    public void setCategorie(Categorie v) { this.categorie = v; }
    public String getEmailContact() { return emailContact; }
    public void setEmailContact(String v) { this.emailContact = v; }
    public String getTelephoneContact() { return telephoneContact; }
    public void setTelephoneContact(String v) { this.telephoneContact = v; }
    public UUID getAssigneA() { return assigneA; }
    public void setAssigneA(UUID v) { this.assigneA = v; }
    public LocalDateTime getDateResolution() { return dateResolution; }
    public void setDateResolution(LocalDateTime v) { this.dateResolution = v; }
    public String getCommentaireResolution() { return commentaireResolution; }
    public void setCommentaireResolution(String v) { this.commentaireResolution = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }

    public void resoudre(String commentaire, UUID resolvedBy) {
        this.statut = StatutTicket.RESOLU;
        this.dateResolution = LocalDateTime.now();
        this.commentaireResolution = commentaire;
        this.assigneA = resolvedBy;
        this.updatedAt = LocalDateTime.now();
    }

    public void fermer() {
        this.statut = StatutTicket.FERME;
        this.updatedAt = LocalDateTime.now();
    }

    public enum Priorite { BASSE, NORMALE, HAUTE, URGENTE }
    public enum StatutTicket { OUVERT, EN_COURS, RESOLU, FERME, ANNULE }
    public enum Categorie {
        TECHNIQUE, FACTURATION, FONCTIONNALITE, 
        BUG, DEMANDE_INFO, AUTRE
    }
}
