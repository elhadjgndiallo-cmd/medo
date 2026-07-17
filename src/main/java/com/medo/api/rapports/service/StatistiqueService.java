package com.medo.api.rapports.service;

import com.medo.api.inventaire.repository.ProduitRepository;
import com.medo.api.inventaire.repository.StockRepository;
import com.medo.api.pos.repository.VenteRepository;
import com.medo.api.rapports.dto.RapportsDtos.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatistiqueService {

    private static final Logger log = LoggerFactory.getLogger(StatistiqueService.class);

    @Autowired private VenteRepository venteRepository;
    @Autowired private ProduitRepository produitRepository;
    @Autowired private StockRepository stockRepository;

    @Transactional(readOnly = true)
    public DashboardStatsResponse getStatsDashboard(LocalDate dateDebut, LocalDate dateFin) {
        log.debug("Calcul stats dashboard du {} au {}", dateDebut, dateFin);

        // Ventes
        BigDecimal chiffreAffaires = venteRepository.sumMontantTotalBetweenDates(dateDebut, dateFin);
        long nombreVentes = venteRepository.countByDateVenteBetween(dateDebut, dateFin);
        BigDecimal panierMoyen = nombreVentes > 0 
            ? chiffreAffaires.divide(BigDecimal.valueOf(nombreVentes), 2, BigDecimal.ROUND_HALF_UP)
            : BigDecimal.ZERO;

        // Inventaire
        long totalProduits = produitRepository.count();
        long produitsActifs = produitRepository.countByActifTrue();
        long ruptures = stockRepository.countByQuantiteTotale(0);
        long stocksBas = stockRepository.countByQuantiteTotaleLessThanSeuilMin();

        return new DashboardStatsResponse(
            chiffreAffaires, nombreVentes, panierMoyen,
            totalProduits, produitsActifs, ruptures, stocksBas
        );
    }

    @Transactional(readOnly = true)
    public List<VenteParJourResponse> getVentesParJour(LocalDate dateDebut, LocalDate dateFin) {
        log.debug("Récupération ventes par jour du {} au {}", dateDebut, dateFin);
        
        List<Object[]> results = venteRepository.sumVentesParJour(dateDebut, dateFin);
        List<VenteParJourResponse> ventesParJour = new ArrayList<>();
        
        for (Object[] row : results) {
            LocalDate date = (LocalDate) row[0];
            Long nombreVentes = (Long) row[1];
            BigDecimal montant = (BigDecimal) row[2];
            ventesParJour.add(new VenteParJourResponse(date, nombreVentes, montant));
        }
        
        return ventesParJour;
    }

    @Transactional(readOnly = true)
    public List<TopProduitResponse> getTopProduits(LocalDate dateDebut, LocalDate dateFin, int limit) {
        log.debug("Récupération top {} produits", limit);
        
        List<Object[]> results = venteRepository.findTopProduitsVendus(dateDebut, dateFin, limit);
        List<TopProduitResponse> topProduits = new ArrayList<>();
        
        for (Object[] row : results) {
            String nomProduit = (String) row[0];
            Long quantite = (Long) row[1];
            BigDecimal montant = (BigDecimal) row[2];
            topProduits.add(new TopProduitResponse(nomProduit, quantite, montant));
        }
        
        return topProduits;
    }

    @Transactional(readOnly = true)
    public RapportInventaireResponse getRapportInventaire() {
        log.debug("Génération rapport inventaire");
        
        long totalProduits = produitRepository.count();
        long produitsActifs = produitRepository.countByActifTrue();
        long ruptures = stockRepository.countByQuantiteTotale(0);
        long stocksBas = stockRepository.countByQuantiteTotaleLessThanSeuilMin();
        long lotsExpirant = stockRepository.countLotsExpirantAvant(LocalDate.now().plusDays(60));
        
        BigDecimal valeurStockTotal = stockRepository.sumValeurTotaleStock();
        
        return new RapportInventaireResponse(
            totalProduits, produitsActifs, ruptures, stocksBas, 
            lotsExpirant, valeurStockTotal
        );
    }

    @Transactional(readOnly = true)
    public RapportVentesResponse getRapportVentes(LocalDate dateDebut, LocalDate dateFin) {
        log.debug("Génération rapport ventes du {} au {}", dateDebut, dateFin);
        
        BigDecimal chiffreAffaires = venteRepository.sumMontantTotalBetweenDates(dateDebut, dateFin);
        long nombreVentes = venteRepository.countByDateVenteBetween(dateDebut, dateFin);
        long nombreClients = venteRepository.countDistinctClientsBetweenDates(dateDebut, dateFin);
        
        BigDecimal panierMoyen = nombreVentes > 0
            ? chiffreAffaires.divide(BigDecimal.valueOf(nombreVentes), 2, BigDecimal.ROUND_HALF_UP)
            : BigDecimal.ZERO;
        
        BigDecimal ticketMax = venteRepository.findMaxMontantBetweenDates(dateDebut, dateFin);
        BigDecimal ticketMin = venteRepository.findMinMontantBetweenDates(dateDebut, dateFin);
        
        return new RapportVentesResponse(
            chiffreAffaires, nombreVentes, nombreClients,
            panierMoyen, ticketMax, ticketMin, dateDebut, dateFin
        );
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getStatistiquesAvancees(LocalDate dateDebut, LocalDate dateFin) {
        log.debug("Calcul statistiques avancées");
        
        Map<String, Object> stats = new HashMap<>();
        
        // Évolution CA
        BigDecimal caActuel = venteRepository.sumMontantTotalBetweenDates(dateDebut, dateFin);
        LocalDate periodePrec = dateDebut.minusDays(dateFin.toEpochDay() - dateDebut.toEpochDay());
        BigDecimal caPrecedent = venteRepository.sumMontantTotalBetweenDates(periodePrec, dateDebut.minusDays(1));
        
        BigDecimal evolutionCA = caPrecedent.compareTo(BigDecimal.ZERO) > 0
            ? caActuel.subtract(caPrecedent).divide(caPrecedent, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100))
            : BigDecimal.ZERO;
        
        stats.put("chiffreAffairesActuel", caActuel);
        stats.put("chiffreAffairesPrecedent", caPrecedent);
        stats.put("evolutionCA", evolutionCA);
        
        // Taux de rotation des stocks
        stats.put("tauxRotationStock", calculerTauxRotation(dateDebut, dateFin));
        
        return stats;
    }

    private BigDecimal calculerTauxRotation(LocalDate dateDebut, LocalDate dateFin) {
        BigDecimal valeurVendue = venteRepository.sumMontantTotalBetweenDates(dateDebut, dateFin);
        BigDecimal valeurStockMoyen = stockRepository.sumValeurTotaleStock();
        
        if (valeurStockMoyen.compareTo(BigDecimal.ZERO) > 0) {
            long nbJours = dateFin.toEpochDay() - dateDebut.toEpochDay();
            return valeurVendue.divide(valeurStockMoyen, 2, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(365.0 / nbJours));
        }
        return BigDecimal.ZERO;
    }
}
