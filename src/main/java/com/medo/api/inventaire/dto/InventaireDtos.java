package com.medo.api.inventaire.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class InventaireDtos {

    // ── Requêtes ──

    public static class CreerProduitRequest {
        @NotBlank @Size(max=200) private String nom;
        private String dci;
        private String categorie;
        private String codeBarres;
        private String iconeType;
        @NotNull @PositiveOrZero private BigDecimal prixVente;
        @NotNull @PositiveOrZero private BigDecimal prixAchat;

        public CreerProduitRequest() {}
        public String getNom()               { return nom; }
        public void setNom(String v)         { this.nom = v; }
        public String getDci()               { return dci; }
        public void setDci(String v)         { this.dci = v; }
        public String getCategorie()         { return categorie; }
        public void setCategorie(String v)   { this.categorie = v; }
        public String getCodeBarres()        { return codeBarres; }
        public void setCodeBarres(String v)  { this.codeBarres = v; }
        public String getIconeType()         { return iconeType; }
        public void setIconeType(String v)   { this.iconeType = v; }
        public BigDecimal getPrixVente()     { return prixVente; }
        public void setPrixVente(BigDecimal v){ this.prixVente = v; }
        public BigDecimal getPrixAchat()     { return prixAchat; }
        public void setPrixAchat(BigDecimal v){ this.prixAchat = v; }
    }

    public static class CreerVarianteRequest {
        @NotNull private UUID produitId;
        private String dosage;
        private String forme;
        private String unite;
        private String codeBarres;
        private BigDecimal prixVente;
        @Min(0) private int seuilMin;
        private Integer seuilMax;

        public CreerVarianteRequest() {}
        public UUID getProduitId()           { return produitId; }
        public void setProduitId(UUID v)     { this.produitId = v; }
        public String getDosage()            { return dosage; }
        public void setDosage(String v)      { this.dosage = v; }
        public String getForme()             { return forme; }
        public void setForme(String v)       { this.forme = v; }
        public String getUnite()             { return unite; }
        public void setUnite(String v)       { this.unite = v; }
        public String getCodeBarres()        { return codeBarres; }
        public void setCodeBarres(String v)  { this.codeBarres = v; }
        public BigDecimal getPrixVente()     { return prixVente; }
        public void setPrixVente(BigDecimal v){ this.prixVente = v; }
        public int getSeuilMin()             { return seuilMin; }
        public void setSeuilMin(int v)       { this.seuilMin = v; }
        public Integer getSeuilMax()         { return seuilMax; }
        public void setSeuilMax(Integer v)   { this.seuilMax = v; }
    }

    public static class CreerLotRequest {
        @NotNull private UUID varianteId;
        private UUID emplacementId;
        @NotBlank private String numeroLot;
        private LocalDate dateFabrication;
        @NotNull @Future private LocalDate datePeremption;
        @NotNull @Min(1) private int quantite;

        public CreerLotRequest() {}
        public UUID getVarianteId()              { return varianteId; }
        public void setVarianteId(UUID v)        { this.varianteId = v; }
        public UUID getEmplacementId()           { return emplacementId; }
        public void setEmplacementId(UUID v)     { this.emplacementId = v; }
        public String getNumeroLot()             { return numeroLot; }
        public void setNumeroLot(String v)       { this.numeroLot = v; }
        public LocalDate getDateFabrication()    { return dateFabrication; }
        public void setDateFabrication(LocalDate v){ this.dateFabrication = v; }
        public LocalDate getDatePeremption()     { return datePeremption; }
        public void setDatePeremption(LocalDate v){ this.datePeremption = v; }
        public int getQuantite()                 { return quantite; }
        public void setQuantite(int v)           { this.quantite = v; }
    }

    // ── Réponses ──

    public static class ProduitResponse {
        private UUID id;
        private String nom;
        private String dci;
        private String categorie;
        private String codeBarres;
        private String iconeType;
        private boolean actif;
        private BigDecimal prixVente;
        private BigDecimal prixAchat;
        private List<VarianteResponse> variantes;
        private LocalDateTime createdAt;

        public ProduitResponse() {}
        public ProduitResponse(UUID id, String nom, String dci, String categorie, String codeBarres,
                               String iconeType, boolean actif, BigDecimal pv, BigDecimal pa,
                               List<VarianteResponse> variantes, LocalDateTime createdAt) {
            this.id = id; this.nom = nom; this.dci = dci; this.categorie = categorie;
            this.codeBarres = codeBarres; this.iconeType = iconeType; this.actif = actif;
            this.prixVente = pv; this.prixAchat = pa; this.variantes = variantes; this.createdAt = createdAt;
        }
        public UUID getId()                          { return id; }
        public void setId(UUID v)                    { this.id = v; }
        public String getNom()                       { return nom; }
        public void setNom(String v)                 { this.nom = v; }
        public String getDci()                       { return dci; }
        public void setDci(String v)                 { this.dci = v; }
        public String getCategorie()                 { return categorie; }
        public void setCategorie(String v)           { this.categorie = v; }
        public String getCodeBarres()                { return codeBarres; }
        public void setCodeBarres(String v)          { this.codeBarres = v; }
        public String getIconeType()                 { return iconeType; }
        public void setIconeType(String v)           { this.iconeType = v; }
        public boolean isActif()                     { return actif; }
        public void setActif(boolean v)              { this.actif = v; }
        public BigDecimal getPrixVente()             { return prixVente; }
        public void setPrixVente(BigDecimal v)       { this.prixVente = v; }
        public BigDecimal getPrixAchat()             { return prixAchat; }
        public void setPrixAchat(BigDecimal v)       { this.prixAchat = v; }
        public List<VarianteResponse> getVariantes() { return variantes; }
        public void setVariantes(List<VarianteResponse> v){ this.variantes = v; }
        public LocalDateTime getCreatedAt()          { return createdAt; }
        public void setCreatedAt(LocalDateTime v)    { this.createdAt = v; }
    }

    public static class VarianteResponse {
        private UUID id;
        private String dosage;
        private String forme;
        private String unite;
        private String codeBarres;
        private BigDecimal prixVente;
        private boolean actif;
        private StockResponse stock;

        public VarianteResponse() {}
        public VarianteResponse(UUID id, String dosage, String forme, String unite, String codeBarres,
                                BigDecimal pv, boolean actif, StockResponse stock) {
            this.id = id; this.dosage = dosage; this.forme = forme; this.unite = unite;
            this.codeBarres = codeBarres; this.prixVente = pv; this.actif = actif; this.stock = stock;
        }
        public UUID getId()                { return id; }
        public void setId(UUID v)          { this.id = v; }
        public String getDosage()          { return dosage; }
        public void setDosage(String v)    { this.dosage = v; }
        public String getForme()           { return forme; }
        public void setForme(String v)     { this.forme = v; }
        public String getUnite()           { return unite; }
        public void setUnite(String v)     { this.unite = v; }
        public String getCodeBarres()      { return codeBarres; }
        public void setCodeBarres(String v){ this.codeBarres = v; }
        public BigDecimal getPrixVente()   { return prixVente; }
        public void setPrixVente(BigDecimal v){ this.prixVente = v; }
        public boolean isActif()           { return actif; }
        public void setActif(boolean v)    { this.actif = v; }
        public StockResponse getStock()    { return stock; }
        public void setStock(StockResponse v){ this.stock = v; }
    }

    public static class StockResponse {
        private UUID id;
        private int quantiteTotale;
        private int seuilMin;
        private Integer seuilMax;
        private String statut;

        public StockResponse() {}
        public StockResponse(UUID id, int qt, int sm, Integer smax, String statut) {
            this.id = id; this.quantiteTotale = qt; this.seuilMin = sm; this.seuilMax = smax; this.statut = statut;
        }
        public UUID getId()               { return id; }
        public void setId(UUID v)         { this.id = v; }
        public int getQuantiteTotale()    { return quantiteTotale; }
        public void setQuantiteTotale(int v){ this.quantiteTotale = v; }
        public int getSeuilMin()          { return seuilMin; }
        public void setSeuilMin(int v)    { this.seuilMin = v; }
        public Integer getSeuilMax()      { return seuilMax; }
        public void setSeuilMax(Integer v){ this.seuilMax = v; }
        public String getStatut()         { return statut; }
        public void setStatut(String v)   { this.statut = v; }
    }

    public static class LotResponse {
        private UUID id;
        private String numeroLot;
        private LocalDate dateFabrication;
        private LocalDate datePeremption;
        private int quantite;
        private String statutPeremption;

        public LotResponse() {}
        public LotResponse(UUID id, String num, LocalDate df, LocalDate dp, int qte, String sp) {
            this.id = id; this.numeroLot = num; this.dateFabrication = df;
            this.datePeremption = dp; this.quantite = qte; this.statutPeremption = sp;
        }
        public UUID getId()                    { return id; }
        public void setId(UUID v)              { this.id = v; }
        public String getNumeroLot()           { return numeroLot; }
        public void setNumeroLot(String v)     { this.numeroLot = v; }
        public LocalDate getDateFabrication()  { return dateFabrication; }
        public void setDateFabrication(LocalDate v){ this.dateFabrication = v; }
        public LocalDate getDatePeremption()   { return datePeremption; }
        public void setDatePeremption(LocalDate v){ this.datePeremption = v; }
        public int getQuantite()               { return quantite; }
        public void setQuantite(int v)         { this.quantite = v; }
        public String getStatutPeremption()    { return statutPeremption; }
        public void setStatutPeremption(String v){ this.statutPeremption = v; }
    }

    public static class StatsInventaireResponse {
        private long totalProduits;
        private long produitsActifs;
        private long ruptures;
        private long stocksBas;
        private long lotsExpirant60j;

        public StatsInventaireResponse() {}
        public StatsInventaireResponse(long total, long actifs, long rupt, long bas, long exp) {
            this.totalProduits = total; this.produitsActifs = actifs;
            this.ruptures = rupt; this.stocksBas = bas; this.lotsExpirant60j = exp;
        }
        public long getTotalProduits()     { return totalProduits; }
        public void setTotalProduits(long v){ this.totalProduits = v; }
        public long getProduitsActifs()    { return produitsActifs; }
        public void setProduitsActifs(long v){ this.produitsActifs = v; }
        public long getRuptures()          { return ruptures; }
        public void setRuptures(long v)    { this.ruptures = v; }
        public long getStocksBas()         { return stocksBas; }
        public void setStocksBas(long v)   { this.stocksBas = v; }
        public long getLotsExpirant60j()   { return lotsExpirant60j; }
        public void setLotsExpirant60j(long v){ this.lotsExpirant60j = v; }
    }
}
