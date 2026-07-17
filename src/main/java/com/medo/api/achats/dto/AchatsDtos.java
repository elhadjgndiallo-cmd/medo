package com.medo.api.achats.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class AchatsDtos {

    // ── Requêtes ──

    public static class CreerFournisseurRequest {
        @NotBlank @Size(max=200) private String nom;
        private String contact; private String email; private String adresse;
        public CreerFournisseurRequest() {}
        public String getNom()           { return nom; }
        public void setNom(String v)     { this.nom = v; }
        public String getContact()       { return contact; }
        public void setContact(String v) { this.contact = v; }
        public String getEmail()         { return email; }
        public void setEmail(String v)   { this.email = v; }
        public String getAdresse()       { return adresse; }
        public void setAdresse(String v) { this.adresse = v; }
    }

    public static class CreerBonCommandeRequest {
        @NotNull private UUID fournisseurId;
        private LocalDate dateLivraisonPrevue;
        @NotEmpty @Valid private List<LigneBcRequest> lignes;
        private String notes;
        public CreerBonCommandeRequest() {}
        public UUID getFournisseurId()                  { return fournisseurId; }
        public void setFournisseurId(UUID v)            { this.fournisseurId = v; }
        public LocalDate getDateLivraisonPrevue()       { return dateLivraisonPrevue; }
        public void setDateLivraisonPrevue(LocalDate v) { this.dateLivraisonPrevue = v; }
        public List<LigneBcRequest> getLignes()         { return lignes; }
        public void setLignes(List<LigneBcRequest> v)   { this.lignes = v; }
        public String getNotes()                        { return notes; }
        public void setNotes(String v)                  { this.notes = v; }
    }

    public static class LigneBcRequest {
        @NotNull private UUID varianteId;
        @NotNull @Positive private int quantiteCmd;
        @NotNull @PositiveOrZero private BigDecimal prixUnitaire;
        public LigneBcRequest() {}
        public UUID getVarianteId()              { return varianteId; }
        public void setVarianteId(UUID v)        { this.varianteId = v; }
        public int getQuantiteCmd()              { return quantiteCmd; }
        public void setQuantiteCmd(int v)        { this.quantiteCmd = v; }
        public BigDecimal getPrixUnitaire()      { return prixUnitaire; }
        public void setPrixUnitaire(BigDecimal v){ this.prixUnitaire = v; }
    }

    public static class ReceptionnerBcRequest {
        @NotEmpty @Valid private List<LigneReceptionRequest> lignesRecues;
        public ReceptionnerBcRequest() {}
        public List<LigneReceptionRequest> getLignesRecues()         { return lignesRecues; }
        public void setLignesRecues(List<LigneReceptionRequest> v)   { this.lignesRecues = v; }
    }

    public static class LigneReceptionRequest {
        @NotNull private UUID ligneCommandeId;
        @NotNull private UUID varianteId;
        @NotNull @Positive private int quantiteRecue;
        @NotBlank private String numeroLot;
        @NotNull @Future private LocalDate datePeremption;
        private LocalDate dateFabrication;
        private UUID emplacementId;
        public LigneReceptionRequest() {}
        public UUID getLigneCommandeId()             { return ligneCommandeId; }
        public void setLigneCommandeId(UUID v)       { this.ligneCommandeId = v; }
        public UUID getVarianteId()                  { return varianteId; }
        public void setVarianteId(UUID v)            { this.varianteId = v; }
        public int getQuantiteRecue()                { return quantiteRecue; }
        public void setQuantiteRecue(int v)          { this.quantiteRecue = v; }
        public String getNumeroLot()                 { return numeroLot; }
        public void setNumeroLot(String v)           { this.numeroLot = v; }
        public LocalDate getDatePeremption()         { return datePeremption; }
        public void setDatePeremption(LocalDate v)   { this.datePeremption = v; }
        public LocalDate getDateFabrication()        { return dateFabrication; }
        public void setDateFabrication(LocalDate v)  { this.dateFabrication = v; }
        public UUID getEmplacementId()               { return emplacementId; }
        public void setEmplacementId(UUID v)         { this.emplacementId = v; }
    }

    // ── Réponses ──

    public static class FournisseurResponse {
        private UUID id; private String nom; private String contact;
        private String email; private String adresse; private boolean actif; private int nombreCommandes;
        public FournisseurResponse() {}
        public FournisseurResponse(UUID id, String nom, String contact, String email,
                                   String adresse, boolean actif, int nb) {
            this.id=id; this.nom=nom; this.contact=contact; this.email=email;
            this.adresse=adresse; this.actif=actif; this.nombreCommandes=nb;
        }
        public UUID getId()                  { return id; }
        public void setId(UUID v)            { this.id = v; }
        public String getNom()               { return nom; }
        public void setNom(String v)         { this.nom = v; }
        public String getContact()           { return contact; }
        public void setContact(String v)     { this.contact = v; }
        public String getEmail()             { return email; }
        public void setEmail(String v)       { this.email = v; }
        public String getAdresse()           { return adresse; }
        public void setAdresse(String v)     { this.adresse = v; }
        public boolean isActif()             { return actif; }
        public void setActif(boolean v)      { this.actif = v; }
        public int getNombreCommandes()      { return nombreCommandes; }
        public void setNombreCommandes(int v){ this.nombreCommandes = v; }
    }

    public static class BonCommandeResponse {
        private UUID id; private String reference; private FournisseurResponse fournisseur;
        private LocalDate dateCommande; private LocalDate dateLivraisonPrevue;
        private String statut; private BigDecimal montantTotal; private String notes;
        private String createdByNom; private LocalDateTime createdAt;
        private List<LigneCommandeResponse> lignes;
        public BonCommandeResponse() {}
        public BonCommandeResponse(UUID id, String ref, FournisseurResponse f, LocalDate dc,
                                   LocalDate dlp, String statut, BigDecimal mt, String notes,
                                   String cbn, LocalDateTime ca, List<LigneCommandeResponse> lignes) {
            this.id=id; this.reference=ref; this.fournisseur=f; this.dateCommande=dc;
            this.dateLivraisonPrevue=dlp; this.statut=statut; this.montantTotal=mt;
            this.notes=notes; this.createdByNom=cbn; this.createdAt=ca; this.lignes=lignes;
        }
        public UUID getId()                              { return id; }
        public void setId(UUID v)                        { this.id = v; }
        public String getReference()                     { return reference; }
        public void setReference(String v)               { this.reference = v; }
        public FournisseurResponse getFournisseur()      { return fournisseur; }
        public void setFournisseur(FournisseurResponse v){ this.fournisseur = v; }
        public LocalDate getDateCommande()               { return dateCommande; }
        public void setDateCommande(LocalDate v)         { this.dateCommande = v; }
        public LocalDate getDateLivraisonPrevue()        { return dateLivraisonPrevue; }
        public void setDateLivraisonPrevue(LocalDate v)  { this.dateLivraisonPrevue = v; }
        public String getStatut()                        { return statut; }
        public void setStatut(String v)                  { this.statut = v; }
        public BigDecimal getMontantTotal()              { return montantTotal; }
        public void setMontantTotal(BigDecimal v)        { this.montantTotal = v; }
        public String getNotes()                         { return notes; }
        public void setNotes(String v)                   { this.notes = v; }
        public String getCreatedByNom()                  { return createdByNom; }
        public void setCreatedByNom(String v)            { this.createdByNom = v; }
        public LocalDateTime getCreatedAt()              { return createdAt; }
        public void setCreatedAt(LocalDateTime v)        { this.createdAt = v; }
        public List<LigneCommandeResponse> getLignes()   { return lignes; }
        public void setLignes(List<LigneCommandeResponse> v){ this.lignes = v; }
    }

    public static class LigneCommandeResponse {
        private UUID id; private String varianteNom; private String varianteDosage;
        private int quantiteCmd; private int quantiteRecue; private int quantiteRestante;
        private BigDecimal prixUnitaire; private BigDecimal sousTotal; private boolean totalementRecu;
        public LigneCommandeResponse() {}
        public LigneCommandeResponse(UUID id, String nom, String dos, int qc, int qr, int qrest,
                                     BigDecimal pu, BigDecimal st, boolean tr) {
            this.id=id; this.varianteNom=nom; this.varianteDosage=dos; this.quantiteCmd=qc;
            this.quantiteRecue=qr; this.quantiteRestante=qrest; this.prixUnitaire=pu;
            this.sousTotal=st; this.totalementRecu=tr;
        }
        public UUID getId()                      { return id; }
        public void setId(UUID v)                { this.id = v; }
        public String getVarianteNom()           { return varianteNom; }
        public void setVarianteNom(String v)     { this.varianteNom = v; }
        public String getVarianteDosage()        { return varianteDosage; }
        public void setVarianteDosage(String v)  { this.varianteDosage = v; }
        public int getQuantiteCmd()              { return quantiteCmd; }
        public void setQuantiteCmd(int v)        { this.quantiteCmd = v; }
        public int getQuantiteRecue()            { return quantiteRecue; }
        public void setQuantiteRecue(int v)      { this.quantiteRecue = v; }
        public int getQuantiteRestante()         { return quantiteRestante; }
        public void setQuantiteRestante(int v)   { this.quantiteRestante = v; }
        public BigDecimal getPrixUnitaire()      { return prixUnitaire; }
        public void setPrixUnitaire(BigDecimal v){ this.prixUnitaire = v; }
        public BigDecimal getSousTotal()         { return sousTotal; }
        public void setSousTotal(BigDecimal v)   { this.sousTotal = v; }
        public boolean isTotalementRecu()        { return totalementRecu; }
        public void setTotalementRecu(boolean v) { this.totalementRecu = v; }
    }

    public static class TimelineResponse {
        private String statut; private String description;
        private LocalDateTime date; private boolean effectue;
        public TimelineResponse() {}
        public TimelineResponse(String s, String d, LocalDateTime dt, boolean e) {
            this.statut=s; this.description=d; this.date=dt; this.effectue=e;
        }
        public String getStatut()              { return statut; }
        public void setStatut(String v)        { this.statut = v; }
        public String getDescription()         { return description; }
        public void setDescription(String v)   { this.description = v; }
        public LocalDateTime getDate()         { return date; }
        public void setDate(LocalDateTime v)   { this.date = v; }
        public boolean isEffectue()            { return effectue; }
        public void setEffectue(boolean v)     { this.effectue = v; }
    }
}
