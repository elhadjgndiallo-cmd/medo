package com.medo.api.rapports.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RapportsDtos {

    public static class DashboardStatsResponse {
        private BigDecimal chiffreAffaires;
        private long nombreVentes;
        private BigDecimal panierMoyen;
        private long totalProduits;
        private long produitsActifs;
        private long ruptures;
        private long stocksBas;

        public DashboardStatsResponse() {}
        
        public DashboardStatsResponse(BigDecimal ca, long nbVentes, BigDecimal panier,
                                     long totalProd, long actifs, long rupt, long bas) {
            this.chiffreAffaires = ca;
            this.nombreVentes = nbVentes;
            this.panierMoyen = panier;
            this.totalProduits = totalProd;
            this.produitsActifs = actifs;
            this.ruptures = rupt;
            this.stocksBas = bas;
        }

        public BigDecimal getChiffreAffaires() { return chiffreAffaires; }
        public void setChiffreAffaires(BigDecimal v) { this.chiffreAffaires = v; }
        public long getNombreVentes() { return nombreVentes; }
        public void setNombreVentes(long v) { this.nombreVentes = v; }
        public BigDecimal getPanierMoyen() { return panierMoyen; }
        public void setPanierMoyen(BigDecimal v) { this.panierMoyen = v; }
        public long getTotalProduits() { return totalProduits; }
        public void setTotalProduits(long v) { this.totalProduits = v; }
        public long getProduitsActifs() { return produitsActifs; }
        public void setProduitsActifs(long v) { this.produitsActifs = v; }
        public long getRuptures() { return ruptures; }
        public void setRuptures(long v) { this.ruptures = v; }
        public long getStocksBas() { return stocksBas; }
        public void setStocksBas(long v) { this.stocksBas = v; }
    }

    public static class VenteParJourResponse {
        private LocalDate date;
        private long nombreVentes;
        private BigDecimal montantTotal;

        public VenteParJourResponse() {}
        
        public VenteParJourResponse(LocalDate date, long nb, BigDecimal montant) {
            this.date = date;
            this.nombreVentes = nb;
            this.montantTotal = montant;
        }

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate v) { this.date = v; }
        public long getNombreVentes() { return nombreVentes; }
        public void setNombreVentes(long v) { this.nombreVentes = v; }
        public BigDecimal getMontantTotal() { return montantTotal; }
        public void setMontantTotal(BigDecimal v) { this.montantTotal = v; }
    }

    public static class TopProduitResponse {
        private String nomProduit;
        private long quantiteVendue;
        private BigDecimal montantTotal;

        public TopProduitResponse() {}
        
        public TopProduitResponse(String nom, long qte, BigDecimal montant) {
            this.nomProduit = nom;
            this.quantiteVendue = qte;
            this.montantTotal = montant;
        }

        public String getNomProduit() { return nomProduit; }
        public void setNomProduit(String v) { this.nomProduit = v; }
        public long getQuantiteVendue() { return quantiteVendue; }
        public void setQuantiteVendue(long v) { this.quantiteVendue = v; }
        public BigDecimal getMontantTotal() { return montantTotal; }
        public void setMontantTotal(BigDecimal v) { this.montantTotal = v; }
    }

    public static class RapportInventaireResponse {
        private long totalProduits;
        private long produitsActifs;
        private long ruptures;
        private long stocksBas;
        private long lotsExpirant;
        private BigDecimal valeurStockTotal;

        public RapportInventaireResponse() {}
        
        public RapportInventaireResponse(long total, long actifs, long rupt, long bas,
                                        long exp, BigDecimal valeur) {
            this.totalProduits = total;
            this.produitsActifs = actifs;
            this.ruptures = rupt;
            this.stocksBas = bas;
            this.lotsExpirant = exp;
            this.valeurStockTotal = valeur;
        }

        public long getTotalProduits() { return totalProduits; }
        public void setTotalProduits(long v) { this.totalProduits = v; }
        public long getProduitsActifs() { return produitsActifs; }
        public void setProduitsActifs(long v) { this.produitsActifs = v; }
        public long getRuptures() { return ruptures; }
        public void setRuptures(long v) { this.ruptures = v; }
        public long getStocksBas() { return stocksBas; }
        public void setStocksBas(long v) { this.stocksBas = v; }
        public long getLotsExpirant() { return lotsExpirant; }
        public void setLotsExpirant(long v) { this.lotsExpirant = v; }
        public BigDecimal getValeurStockTotal() { return valeurStockTotal; }
        public void setValeurStockTotal(BigDecimal v) { this.valeurStockTotal = v; }
    }

    public static class RapportVentesResponse {
        private BigDecimal chiffreAffaires;
        private long nombreVentes;
        private long nombreClients;
        private BigDecimal panierMoyen;
        private BigDecimal ticketMax;
        private BigDecimal ticketMin;
        private LocalDate periodeDebut;
        private LocalDate periodeFin;

        public RapportVentesResponse() {}
        
        public RapportVentesResponse(BigDecimal ca, long nbV, long nbC, BigDecimal panier,
                                    BigDecimal max, BigDecimal min, LocalDate debut, LocalDate fin) {
            this.chiffreAffaires = ca;
            this.nombreVentes = nbV;
            this.nombreClients = nbC;
            this.panierMoyen = panier;
            this.ticketMax = max;
            this.ticketMin = min;
            this.periodeDebut = debut;
            this.periodeFin = fin;
        }

        public BigDecimal getChiffreAffaires() { return chiffreAffaires; }
        public void setChiffreAffaires(BigDecimal v) { this.chiffreAffaires = v; }
        public long getNombreVentes() { return nombreVentes; }
        public void setNombreVentes(long v) { this.nombreVentes = v; }
        public long getNombreClients() { return nombreClients; }
        public void setNombreClients(long v) { this.nombreClients = v; }
        public BigDecimal getPanierMoyen() { return panierMoyen; }
        public void setPanierMoyen(BigDecimal v) { this.panierMoyen = v; }
        public BigDecimal getTicketMax() { return ticketMax; }
        public void setTicketMax(BigDecimal v) { this.ticketMax = v; }
        public BigDecimal getTicketMin() { return ticketMin; }
        public void setTicketMin(BigDecimal v) { this.ticketMin = v; }
        public LocalDate getPeriodeDebut() { return periodeDebut; }
        public void setPeriodeDebut(LocalDate v) { this.periodeDebut = v; }
        public LocalDate getPeriodeFin() { return periodeFin; }
        public void setPeriodeFin(LocalDate v) { this.periodeFin = v; }
    }
}
