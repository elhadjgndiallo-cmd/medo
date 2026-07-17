package com.medo.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public class AuthDtos {

    public static class LoginRequest {
        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "Format d'email invalide")
        private String email;
        @NotBlank(message = "Le mot de passe est obligatoire")
        private String motDePasse;
        public LoginRequest() {}
        public String getEmail()         { return email; }
        public void setEmail(String v)   { this.email = v; }
        public String getMotDePasse()    { return motDePasse; }
        public void setMotDePasse(String v) { this.motDePasse = v; }
    }

    public static class RefreshTokenRequest {
        @NotBlank private String refreshToken;
        public RefreshTokenRequest() {}
        public String getRefreshToken()       { return refreshToken; }
        public void setRefreshToken(String v) { this.refreshToken = v; }
    }

    public static class LogoutRequest {
        @NotBlank private String refreshToken;
        public LogoutRequest() {}
        public String getRefreshToken()       { return refreshToken; }
        public void setRefreshToken(String v) { this.refreshToken = v; }
    }

    public static class ResetPasswordRequest {
        @NotBlank @Email private String email;
        public ResetPasswordRequest() {}
        public String getEmail()       { return email; }
        public void setEmail(String v) { this.email = v; }
    }

    public static class InscriptionRequest {
        @NotBlank @Size(min=2, max=150) private String nomPharmacie;
        @NotBlank @Email                private String emailContact;
        @NotBlank @Size(min=8)          private String motDePasse;
        @NotBlank @Size(min=2, max=50)  private String sousDomaineSouhaite;
        @NotBlank                       private String adresse;
        private String telephone;
        private String planDemande;
        public InscriptionRequest() {}
        public String getNomPharmacie()              { return nomPharmacie; }
        public void setNomPharmacie(String v)        { this.nomPharmacie = v; }
        public String getEmailContact()              { return emailContact; }
        public void setEmailContact(String v)        { this.emailContact = v; }
        public String getMotDePasse()                { return motDePasse; }
        public void setMotDePasse(String v)          { this.motDePasse = v; }
        public String getSousDomaineSouhaite()       { return sousDomaineSouhaite; }
        public void setSousDomaineSouhaite(String v) { this.sousDomaineSouhaite = v; }
        public String getAdresse()                   { return adresse; }
        public void setAdresse(String v)             { this.adresse = v; }
        public String getTelephone()                 { return telephone; }
        public void setTelephone(String v)           { this.telephone = v; }
        public String getPlanDemande()               { return planDemande; }
        public void setPlanDemande(String v)         { this.planDemande = v; }
    }

    public static class MobileRegisterRequest {
        @NotBlank private String nom;
        @NotBlank private String prenom;
        @NotBlank @Email private String email;
        @NotBlank @Size(min=8) private String motDePasse;
        private String telephone;
        public MobileRegisterRequest() {}
        public String getNom()          { return nom; }
        public void setNom(String v)    { this.nom = v; }
        public String getPrenom()       { return prenom; }
        public void setPrenom(String v) { this.prenom = v; }
        public String getEmail()        { return email; }
        public void setEmail(String v)  { this.email = v; }
        public String getMotDePasse()   { return motDePasse; }
        public void setMotDePasse(String v) { this.motDePasse = v; }
        public String getTelephone()    { return telephone; }
        public void setTelephone(String v){ this.telephone = v; }
    }

    public static class MobileLoginRequest {
        @NotBlank @Email private String email;
        @NotBlank        private String motDePasse;
        public MobileLoginRequest() {}
        public String getEmail()            { return email; }
        public void setEmail(String v)      { this.email = v; }
        public String getMotDePasse()       { return motDePasse; }
        public void setMotDePasse(String v) { this.motDePasse = v; }
    }

    public static class TokenResponse {
        private String accessToken;
        private String refreshToken;
        private String tokenType = "Bearer";
        private long   expiresIn;
        private String typeUser;
        private String tenantId;
        private UserInfo user;
        public TokenResponse() {}
        public TokenResponse(String at, String rt, long exp, String typeUser, String tenantId, UserInfo user) {
            this.accessToken = at; this.refreshToken = rt;
            this.expiresIn = exp; this.typeUser = typeUser;
            this.tenantId = tenantId; this.user = user;
        }
        public String getAccessToken()         { return accessToken; }
        public void setAccessToken(String v)   { this.accessToken = v; }
        public String getRefreshToken()        { return refreshToken; }
        public void setRefreshToken(String v)  { this.refreshToken = v; }
        public String getTokenType()           { return tokenType; }
        public void setTokenType(String v)     { this.tokenType = v; }
        public long getExpiresIn()             { return expiresIn; }
        public void setExpiresIn(long v)       { this.expiresIn = v; }
        public String getTypeUser()            { return typeUser; }
        public void setTypeUser(String v)      { this.typeUser = v; }
        public String getTenantId()            { return tenantId; }
        public void setTenantId(String v)      { this.tenantId = v; }
        public UserInfo getUser()              { return user; }
        public void setUser(UserInfo v)        { this.user = v; }
    }

    public static class UserInfo {
        private UUID id;
        private String nom;
        private String prenom;
        private String email;
        private String typeUser;
        private List<String> roles;
        public UserInfo() {}
        public UserInfo(UUID id, String nom, String prenom, String email, String typeUser, List<String> roles) {
            this.id = id; this.nom = nom; this.prenom = prenom;
            this.email = email; this.typeUser = typeUser; this.roles = roles;
        }
        public UUID getId()                { return id; }
        public void setId(UUID v)          { this.id = v; }
        public String getNom()             { return nom; }
        public void setNom(String v)       { this.nom = v; }
        public String getPrenom()          { return prenom; }
        public void setPrenom(String v)    { this.prenom = v; }
        public String getEmail()           { return email; }
        public void setEmail(String v)     { this.email = v; }
        public String getTypeUser()        { return typeUser; }
        public void setTypeUser(String v)  { this.typeUser = v; }
        public List<String> getRoles()     { return roles; }
        public void setRoles(List<String> v){ this.roles = v; }
    }

    public static class InscriptionResponse {
        private UUID demandeId;
        private String message;
        private String statut;
        public InscriptionResponse() {}
        public InscriptionResponse(UUID id, String msg, String statut) {
            this.demandeId = id; this.message = msg; this.statut = statut;
        }
        public UUID getDemandeId()       { return demandeId; }
        public void setDemandeId(UUID v) { this.demandeId = v; }
        public String getMessage()       { return message; }
        public void setMessage(String v) { this.message = v; }
        public String getStatut()        { return statut; }
        public void setStatut(String v)  { this.statut = v; }
    }
}
