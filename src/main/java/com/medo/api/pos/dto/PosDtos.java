package com.medo.api.pos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class PosDtos {

    // ── Requêtes ──

    public static class OuvrirSessionRequest {
        @NotNull private UUID caisseId;
        @NotNull @PositiveOrZero private BigDecimal fondCaisse;
        private String notes;
        public OuvrirSessionRequest() {}
        public UUID getCaisseId()              { return caisseId; }
        public void setCaisseId(UUID v)        { this.caisseId = v; }
        public BigDecimal getFondCaisse()      { return fondCaisse; }
        public void setFondCaisse(BigDecimal v){ this.fondCaisse = v; }
        public String getNotes()               { return notes; }
        public void setNotes(String v)         { this.notes = v; }
    }

    public static class FermerSessionRequest {
        @NotNull @PositiveOrZero private BigDecimal montantCloture;
        private String notes;
        public FermerSessionRequest() {}
        public BigDecimal getMontantCloture()      { return montantCloture; }
        public void setMontantCloture(BigDecimal v){ this.montantCloture = v; }
        public String getNotes()                   { return notes; }
        public void setNotes(String v)             { this.notes = v; }
    }

    public static class CreerVenteRequest {
        @NotNull private UUID sessionId;
        private UUID clientId;
        @NotEmpty @Valid private List<LigneVenteRequest> lignes;
        @NotBlank private String modePaiement;
        @PositiveOrZero private BigDecimal montantRemise;
        private String notes;
        public CreerVenteRequest() {}
        public UUID getSessionId()                     { return sessionId; }
        public void setSessionId(UUID v)               { this.sessionId = v; }
        public UUID getClientId()                      { return clientId; }
        public void setClientId(UUID v)                { this.clientId = v; }
        public List<LigneVenteRequest> getLignes()     { return lignes; }
        public void setLignes(List<LigneVenteRequest> v){ this.lignes = v; }
        public String getModePaiement()                { return modePaiement; }
        public void setModePaiement(String v)          { this.modePaiement = v; }
        public BigDecimal getMontantRemise()            { return montantRemise; }
        public void setMontantRemise(BigDecimal v)      { this.montantRemise = v; }
        public String getNotes()                       { return notes; }
        public void setNotes(String v)                 { this.notes = v; }
    }

    public static class LigneVenteRequest {
        @NotNull private UUID varianteId;
        private UUID lotId;
        @NotNull @Positive private int quantite;
        public LigneVenteRequest() {}
        public UUID getVarianteId()        { return varianteId; }
        public void setVarianteId(UUID v)  { this.varianteId = v; }
        public UUID getLotId()             { return lotId; }
        public void setLotId(UUID v)       { this.lotId = v; }
        public int getQuantite()           { return quantite; }
        public void setQuantite(int v)     { this.quantite = v; }
    }

    public static class CreerClientRequest {
        @NotBlank @Size(max=200) private String nom;
        private String telephone;
        private String email;
        private String notes;
        public CreerClientRequest() {}
        public String getNom()           { return nom; }
        public void setNom(String v)     { this.nom = v; }
        public String getTelephone()     { return telephone; }
        public void setTelephone(String v){ this.telephone = v; }
        public String getEmail()         { return email; }
        public void setEmail(String v)   { this.email = v; }
        public String getNotes()         { return notes; }
        public void setNotes(String v)   { this.notes = v; }
    }

    // ── Réponses ──

    public static class CaisseResponse {
        private UUID id; private String nom; private String reference;
        private boolean actif; private boolean disponible;
        private SessionCaisseResponse sessionActive;
        public CaisseResponse() {}
        public CaisseResponse(UUID id, String nom, String ref, boolean actif,
                              boolean dispo, SessionCaisseResponse sa) {
            this.id=id; this.nom=nom; this.reference=ref;
            this.actif=actif; this.disponible=dispo; this.sessionActive=sa;
        }
        public UUID getId()                             { return id; }
        public void setId(UUID v)                       { this.id = v; }
        public String getNom()                          { return nom; }
        public void setNom(String v)                    { this.nom = v; }
        public String getReference()                    { return reference; }
        public void setReference(String v)              { this.reference = v; }
        public boolean isActif()                        { return actif; }
        public void setActif(boolean v)                 { this.actif = v; }
        public boolean isDisponible()                   { return disponible; }
        public void setDisponible(boolean v)            { this.disponible = v; }
        public SessionCaisseResponse getSessionActive() { return sessionActive; }
        public void setSessionActive(SessionCaisseResponse v){ this.sessionActive = v; }
    }

    public static class SessionCaisseResponse {
        private UUID id; private UUID caisseId; private String caisseNom;
        private String utilisateurNom; private LocalDateTime dateOuverture;
        private LocalDateTime dateFermeture; private BigDecimal fondCaisse;
        private BigDecimal montantCloture; private BigDecimal ecart;
        private String statut; private String dureeOuverte;
        private int nombreVentes; private BigDecimal totalVentes;
        public SessionCaisseResponse() {}
        public SessionCaisseResponse(UUID id, UUID caisseId, String caisseNom, String utilNom,
                                     LocalDateTime ouv, LocalDateTime ferm, BigDecimal fond,
                                     BigDecimal clot, BigDecimal ecart, String statut,
                                     String duree, int nbVentes, BigDecimal total) {
            this.id=id; this.caisseId=caisseId; this.caisseNom=caisseNom;
            this.utilisateurNom=utilNom; this.dateOuverture=ouv; this.dateFermeture=ferm;
            this.fondCaisse=fond; this.montantCloture=clot; this.ecart=ecart;
            this.statut=statut; this.dureeOuverte=duree; this.nombreVentes=nbVentes; this.totalVentes=total;
        }
        public UUID getId()                            { return id; }
        public void setId(UUID v)                      { this.id = v; }
        public UUID getCaisseId()                      { return caisseId; }
        public void setCaisseId(UUID v)                { this.caisseId = v; }
        public String getCaisseNom()                   { return caisseNom; }
        public void setCaisseNom(String v)             { this.caisseNom = v; }
        public String getUtilisateurNom()              { return utilisateurNom; }
        public void setUtilisateurNom(String v)        { this.utilisateurNom = v; }
        public LocalDateTime getDateOuverture()        { return dateOuverture; }
        public void setDateOuverture(LocalDateTime v)  { this.dateOuverture = v; }
        public LocalDateTime getDateFermeture()        { return dateFermeture; }
        public void setDateFermeture(LocalDateTime v)  { this.dateFermeture = v; }
        public BigDecimal getFondCaisse()              { return fondCaisse; }
        public void setFondCaisse(BigDecimal v)        { this.fondCaisse = v; }
        public BigDecimal getMontantCloture()          { return montantCloture; }
        public void setMontantCloture(BigDecimal v)    { this.montantCloture = v; }
        public BigDecimal getEcart()                   { return ecart; }
        public void setEcart(BigDecimal v)             { this.ecart = v; }
        public String getStatut()                      { return statut; }
        public void setStatut(String v)                { this.statut = v; }
        public String getDureeOuverte()                { return dureeOuverte; }
        public void setDureeOuverte(String v)          { this.dureeOuverte = v; }
        public int getNombreVentes()                   { return nombreVentes; }
        public void setNombreVentes(int v)             { this.nombreVentes = v; }
        public BigDecimal getTotalVentes()             { return totalVentes; }
        public void setTotalVentes(BigDecimal v)       { this.totalVentes = v; }
    }

    public static class VenteResponse {
        private UUID id; private String numeroTicket; private LocalDateTime dateVente;
        private BigDecimal montantTotal; private BigDecimal montantRemise;
        private String modePaiement; private String statut; private String utilisateurNom;
        private ClientResponse client; private List<LigneVenteResponse> lignes;
        public VenteResponse() {}
        public VenteResponse(UUID id, String ticket, LocalDateTime date, BigDecimal total,
                             BigDecimal remise, String mode, String statut, String utilNom,
                             ClientResponse client, List<LigneVenteResponse> lignes) {
            this.id=id; this.numeroTicket=ticket; this.dateVente=date;
            this.montantTotal=total; this.montantRemise=remise;
            this.modePaiement=mode; this.statut=statut; this.utilisateurNom=utilNom;
            this.client=client; this.lignes=lignes;
        }
        public UUID getId()                          { return id; }
        public void setId(UUID v)                    { this.id = v; }
        public String getNumeroTicket()              { return numeroTicket; }
        public void setNumeroTicket(String v)        { this.numeroTicket = v; }
        public LocalDateTime getDateVente()          { return dateVente; }
        public void setDateVente(LocalDateTime v)    { this.dateVente = v; }
        public BigDecimal getMontantTotal()          { return montantTotal; }
        public void setMontantTotal(BigDecimal v)    { this.montantTotal = v; }
        public BigDecimal getMontantRemise()         { return montantRemise; }
        public void setMontantRemise(BigDecimal v)   { this.montantRemise = v; }
        public String getModePaiement()              { return modePaiement; }
        public void setModePaiement(String v)        { this.modePaiement = v; }
        public String getStatut()                    { return statut; }
        public void setStatut(String v)              { this.statut = v; }
        public String getUtilisateurNom()            { return utilisateurNom; }
        public void setUtilisateurNom(String v)      { this.utilisateurNom = v; }
        public ClientResponse getClient()            { return client; }
        public void setClient(ClientResponse v)      { this.client = v; }
        public List<LigneVenteResponse> getLignes()  { return lignes; }
        public void setLignes(List<LigneVenteResponse> v){ this.lignes = v; }
    }

    public static class LigneVenteResponse {
        private UUID id; private String produitNom; private String varianteDosage;
        private String lotNumero; private int quantite;
        private BigDecimal prixUnitaire; private BigDecimal sousTotal;
        public LigneVenteResponse() {}
        public LigneVenteResponse(UUID id, String prod, String dos, String lot,
                                  int qte, BigDecimal pu, BigDecimal st) {
            this.id=id; this.produitNom=prod; this.varianteDosage=dos;
            this.lotNumero=lot; this.quantite=qte; this.prixUnitaire=pu; this.sousTotal=st;
        }
        public UUID getId()                      { return id; }
        public void setId(UUID v)                { this.id = v; }
        public String getProduitNom()            { return produitNom; }
        public void setProduitNom(String v)      { this.produitNom = v; }
        public String getVarianteDosage()        { return varianteDosage; }
        public void setVarianteDosage(String v)  { this.varianteDosage = v; }
        public String getLotNumero()             { return lotNumero; }
        public void setLotNumero(String v)       { this.lotNumero = v; }
        public int getQuantite()                 { return quantite; }
        public void setQuantite(int v)           { this.quantite = v; }
        public BigDecimal getPrixUnitaire()      { return prixUnitaire; }
        public void setPrixUnitaire(BigDecimal v){ this.prixUnitaire = v; }
        public BigDecimal getSousTotal()         { return sousTotal; }
        public void setSousTotal(BigDecimal v)   { this.sousTotal = v; }
    }

    public static class ClientResponse {
        private UUID id; private String nom; private String telephone; private String email;
        public ClientResponse() {}
        public ClientResponse(UUID id, String nom, String tel, String email) {
            this.id=id; this.nom=nom; this.telephone=tel; this.email=email;
        }
        public UUID getId()            { return id; }
        public void setId(UUID v)      { this.id = v; }
        public String getNom()         { return nom; }
        public void setNom(String v)   { this.nom = v; }
        public String getTelephone()   { return telephone; }
        public void setTelephone(String v){ this.telephone = v; }
        public String getEmail()       { return email; }
        public void setEmail(String v) { this.email = v; }
    }

    public static class StatsPOSResponse {
        private long nombreCaisses; private long sessionsOuvertes;
        private long ventesAujourdhui; private BigDecimal chiffreAffairesAujourdhui;
        private BigDecimal chiffreAffairesMois;
        public StatsPOSResponse() {}
        public StatsPOSResponse(long c, long s, long v, BigDecimal caj, BigDecimal cam) {
            this.nombreCaisses=c; this.sessionsOuvertes=s; this.ventesAujourdhui=v;
            this.chiffreAffairesAujourdhui=caj; this.chiffreAffairesMois=cam;
        }
        public long getNombreCaisses()                    { return nombreCaisses; }
        public void setNombreCaisses(long v)              { this.nombreCaisses = v; }
        public long getSessionsOuvertes()                 { return sessionsOuvertes; }
        public void setSessionsOuvertes(long v)           { this.sessionsOuvertes = v; }
        public long getVentesAujourdhui()                 { return ventesAujourdhui; }
        public void setVentesAujourdhui(long v)           { this.ventesAujourdhui = v; }
        public BigDecimal getChiffreAffairesAujourdhui()  { return chiffreAffairesAujourdhui; }
        public void setChiffreAffairesAujourdhui(BigDecimal v){ this.chiffreAffairesAujourdhui=v; }
        public BigDecimal getChiffreAffairesMois()        { return chiffreAffairesMois; }
        public void setChiffreAffairesMois(BigDecimal v)  { this.chiffreAffairesMois = v; }
    }
}
