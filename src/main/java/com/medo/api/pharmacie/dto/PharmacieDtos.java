package com.medo.api.pharmacie.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

public class PharmacieDtos {

    public static class ConfigurerPharmacieRequest {
        @NotBlank @Size(max=150) private String nom;
        @NotBlank @Size(max=500) private String adresse;
        private String ville;
        private Double latitude;
        private Double longitude;
        private String telephone;
        private String horaires;

        public ConfigurerPharmacieRequest() {}
        public String getNom()               { return nom; }
        public void setNom(String v)         { this.nom = v; }
        public String getAdresse()           { return adresse; }
        public void setAdresse(String v)     { this.adresse = v; }
        public String getVille()             { return ville; }
        public void setVille(String v)       { this.ville = v; }
        public Double getLatitude()          { return latitude; }
        public void setLatitude(Double v)    { this.latitude = v; }
        public Double getLongitude()         { return longitude; }
        public void setLongitude(Double v)   { this.longitude = v; }
        public String getTelephone()         { return telephone; }
        public void setTelephone(String v)   { this.telephone = v; }
        public String getHoraires()          { return horaires; }
        public void setHoraires(String v)    { this.horaires = v; }
    }

    public static class WhiteLabelRequest {
        @Size(max=500) private String logoUrl;
        @Size(max=20)  private String couleurPrimaire;

        public WhiteLabelRequest() {}
        public String getLogoUrl()               { return logoUrl; }
        public void setLogoUrl(String v)         { this.logoUrl = v; }
        public String getCouleurPrimaire()       { return couleurPrimaire; }
        public void setCouleurPrimaire(String v) { this.couleurPrimaire = v; }
    }

    public static class GpsRequest {
        private Double latitude;
        private Double longitude;

        public GpsRequest() {}
        public Double getLatitude()         { return latitude; }
        public void setLatitude(Double v)   { this.latitude = v; }
        public Double getLongitude()        { return longitude; }
        public void setLongitude(Double v)  { this.longitude = v; }
    }

    public static class PharmacieResponse {
        private UUID id;
        private String nom;
        private String adresse;
        private String ville;
        private Double latitude;
        private Double longitude;
        private String telephone;
        private String horaires;
        private String logoUrl;
        private String couleurPrimaire;
        private boolean aCoordonnees;
        private LocalDateTime updatedAt;

        public PharmacieResponse() {}
        public PharmacieResponse(UUID id, String nom, String adresse, String ville,
                                  Double lat, Double lng, String tel, String horaires,
                                  String logo, String couleur, boolean gps, LocalDateTime upd) {
            this.id=id; this.nom=nom; this.adresse=adresse; this.ville=ville;
            this.latitude=lat; this.longitude=lng; this.telephone=tel;
            this.horaires=horaires; this.logoUrl=logo; this.couleurPrimaire=couleur;
            this.aCoordonnees=gps; this.updatedAt=upd;
        }
        public UUID getId()                       { return id; }
        public void setId(UUID v)                 { this.id = v; }
        public String getNom()                    { return nom; }
        public void setNom(String v)              { this.nom = v; }
        public String getAdresse()                { return adresse; }
        public void setAdresse(String v)          { this.adresse = v; }
        public String getVille()                  { return ville; }
        public void setVille(String v)            { this.ville = v; }
        public Double getLatitude()               { return latitude; }
        public void setLatitude(Double v)         { this.latitude = v; }
        public Double getLongitude()              { return longitude; }
        public void setLongitude(Double v)        { this.longitude = v; }
        public String getTelephone()              { return telephone; }
        public void setTelephone(String v)        { this.telephone = v; }
        public String getHoraires()               { return horaires; }
        public void setHoraires(String v)         { this.horaires = v; }
        public String getLogoUrl()                { return logoUrl; }
        public void setLogoUrl(String v)          { this.logoUrl = v; }
        public String getCouleurPrimaire()        { return couleurPrimaire; }
        public void setCouleurPrimaire(String v)  { this.couleurPrimaire = v; }
        public boolean isACoordonnees()           { return aCoordonnees; }
        public void setACoordonnees(boolean v)    { this.aCoordonnees = v; }
        public LocalDateTime getUpdatedAt()       { return updatedAt; }
        public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }
    }
}
