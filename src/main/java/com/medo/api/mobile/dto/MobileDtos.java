package com.medo.api.mobile.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class MobileDtos {

    // ── Réponses ──

    public static class ResultatRechercheResponse {
        private UUID pharmacieTenantId; private String pharmacieNom;
        private String pharmacieAdresse; private String pharmacieVille;
        private String pharmacieTelephone; private String horaires;
        private double distanceKm; private boolean disponible;
        private boolean estFavori; private boolean estOuverte;

        public ResultatRechercheResponse() {}
        public ResultatRechercheResponse(UUID tid, String nom, String adr, String ville,
                                         String tel, String hor, double dist, boolean dispo,
                                         boolean fav, boolean ouv) {
            this.pharmacieTenantId=tid; this.pharmacieNom=nom; this.pharmacieAdresse=adr;
            this.pharmacieVille=ville; this.pharmacieTelephone=tel; this.horaires=hor;
            this.distanceKm=dist; this.disponible=dispo; this.estFavori=fav; this.estOuverte=ouv;
        }
        public UUID getPharmacieTenanId()              { return pharmacieTenantId; }
        public void setPharmacieTenanId(UUID v)        { this.pharmacieTenantId = v; }
        public String getPharmacieNom()                { return pharmacieNom; }
        public void setPharmacieNom(String v)          { this.pharmacieNom = v; }
        public String getPharmacieAdresse()            { return pharmacieAdresse; }
        public void setPharmacieAdresse(String v)      { this.pharmacieAdresse = v; }
        public String getPharmacieVille()              { return pharmacieVille; }
        public void setPharmacieVille(String v)        { this.pharmacieVille = v; }
        public String getPharmacieTeléphone()          { return pharmacieTelephone; }
        public void setPharmacieTeléphone(String v)    { this.pharmacieTelephone = v; }
        public String getHoraires()                    { return horaires; }
        public void setHoraires(String v)              { this.horaires = v; }
        public double getDistanceKm()                  { return distanceKm; }
        public void setDistanceKm(double v)            { this.distanceKm = v; }
        public boolean isDisponible()                  { return disponible; }
        public void setDisponible(boolean v)           { this.disponible = v; }
        public boolean isEstFavori()                   { return estFavori; }
        public void setEstFavori(boolean v)            { this.estFavori = v; }
        public boolean isEstOuverte()                  { return estOuverte; }
        public void setEstOuverte(boolean v)           { this.estOuverte = v; }
    }

    public static class FavoriResponse {
        private UUID id; private UUID pharmacieTenantId;
        private String pharmacieNom; private LocalDateTime createdAt;
        public FavoriResponse() {}
        public FavoriResponse(UUID id, UUID tid, String nom, LocalDateTime dt) {
            this.id=id; this.pharmacieTenantId=tid; this.pharmacieNom=nom; this.createdAt=dt;
        }
        public UUID getId()                       { return id; }
        public void setId(UUID v)                 { this.id = v; }
        public UUID getPharmacieTenantId()        { return pharmacieTenantId; }
        public void setPharmacieTenantId(UUID v)  { this.pharmacieTenantId = v; }
        public String getPharmacieNom()           { return pharmacieNom; }
        public void setPharmacieNom(String v)     { this.pharmacieNom = v; }
        public LocalDateTime getCreatedAt()       { return createdAt; }
        public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    }

    public static class HistoriqueResponse {
        private UUID id; private String termeProduit; private String categorie;
        private int nombreResultats; private String tempsEcoule; private LocalDateTime createdAt;
        public HistoriqueResponse() {}
        public HistoriqueResponse(UUID id, String terme, String cat, int nb, String te, LocalDateTime dt) {
            this.id=id; this.termeProduit=terme; this.categorie=cat;
            this.nombreResultats=nb; this.tempsEcoule=te; this.createdAt=dt;
        }
        public UUID getId()                        { return id; }
        public void setId(UUID v)                  { this.id = v; }
        public String getTermeProduit()            { return termeProduit; }
        public void setTermeProduit(String v)      { this.termeProduit = v; }
        public String getCategorie()               { return categorie; }
        public void setCategorie(String v)         { this.categorie = v; }
        public int getNombreResultats()            { return nombreResultats; }
        public void setNombreResultats(int v)      { this.nombreResultats = v; }
        public String getTempsEcoule()             { return tempsEcoule; }
        public void setTempsEcoule(String v)       { this.tempsEcoule = v; }
        public LocalDateTime getCreatedAt()        { return createdAt; }
        public void setCreatedAt(LocalDateTime v)  { this.createdAt = v; }
    }

    public static class ProfilStatsResponse {
        private long nombreRecherches; private long nombrePharmaciesTouchees; private long nombreFavoris;
        public ProfilStatsResponse() {}
        public ProfilStatsResponse(long r, long p, long f) {
            this.nombreRecherches=r; this.nombrePharmaciesTouchees=p; this.nombreFavoris=f;
        }
        public long getNombreRecherches()             { return nombreRecherches; }
        public void setNombreRecherches(long v)       { this.nombreRecherches = v; }
        public long getNombrePharmaciesTouchees()     { return nombrePharmaciesTouchees; }
        public void setNombrePharmaciesTouchees(long v){ this.nombrePharmaciesTouchees = v; }
        public long getNombreFavoris()                { return nombreFavoris; }
        public void setNombreFavoris(long v)          { this.nombreFavoris = v; }
    }

    public static class ClientMobileResponse {
        private UUID id; private String nom; private String prenom; private String email;
        private String telephone; private String avatarInitiales;
        private boolean localisationActivee; private boolean notificationsActivees;
        private ProfilStatsResponse stats;
        public ClientMobileResponse() {}
        public ClientMobileResponse(UUID id, String nom, String prenom, String email,
                                    String tel, String av, boolean loc, boolean notif,
                                    ProfilStatsResponse stats) {
            this.id=id; this.nom=nom; this.prenom=prenom; this.email=email;
            this.telephone=tel; this.avatarInitiales=av;
            this.localisationActivee=loc; this.notificationsActivees=notif; this.stats=stats;
        }
        public UUID getId()                                { return id; }
        public void setId(UUID v)                          { this.id = v; }
        public String getNom()                             { return nom; }
        public void setNom(String v)                       { this.nom = v; }
        public String getPrenom()                          { return prenom; }
        public void setPrenom(String v)                    { this.prenom = v; }
        public String getEmail()                           { return email; }
        public void setEmail(String v)                     { this.email = v; }
        public String getTelephone()                       { return telephone; }
        public void setTelephone(String v)                 { this.telephone = v; }
        public String getAvatarInitiales()                 { return avatarInitiales; }
        public void setAvatarInitiales(String v)           { this.avatarInitiales = v; }
        public boolean isLocalisationActivee()             { return localisationActivee; }
        public void setLocalisationActivee(boolean v)      { this.localisationActivee = v; }
        public boolean isNotificationsActivees()           { return notificationsActivees; }
        public void setNotificationsActivees(boolean v)    { this.notificationsActivees = v; }
        public ProfilStatsResponse getStats()              { return stats; }
        public void setStats(ProfilStatsResponse v)        { this.stats = v; }
    }
}
