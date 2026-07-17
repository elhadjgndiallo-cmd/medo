package com.medo.api.inventaire.services;

import com.medo.api.exception.GlobalExceptionHandler.*;
import com.medo.api.inventaire.dao.LotRepository;
import com.medo.api.inventaire.dao.MouvementStockRepository;
import com.medo.api.inventaire.dao.StockRepository;
import com.medo.api.inventaire.entity.Lot;
import com.medo.api.inventaire.entity.MouvementStock;
import com.medo.api.inventaire.entity.Stock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class StockService {

    private static final Logger log = LoggerFactory.getLogger(StockService.class);

    @Autowired private StockRepository stockRepository;
    @Autowired private LotRepository lotRepository;
    @Autowired private MouvementStockRepository mouvementRepository;

    /** Décrémentation FIFO — lots triés par date de péremption croissante */
    @Transactional
    public void decrementerStock(UUID varianteId, int qte, UUID referenceId, UUID userId) {
        Stock stock = stockRepository.findByVarianteId(varianteId)
            .orElseThrow(() -> new ResourceNotFoundException("Stock", varianteId.toString()));

        if (stock.getQuantiteTotale() < qte)
            throw new StockInsuffisantException(
                stock.getVariante().getLibelleComplet(), qte, stock.getQuantiteTotale());

        List<Lot> lots = lotRepository
            .findByVarianteIdAndQuantiteGreaterThanOrderByDatePeremptionAsc(varianteId, 0);

        int restant = qte;
        for (Lot lot : lots) {
            if (restant <= 0) break;
            int preleve = Math.min(lot.getQuantite(), restant);
            lot.setQuantite(lot.getQuantite() - preleve);
            restant -= preleve;

            MouvementStock m = new MouvementStock();
            m.setLot(lot);
            m.setType(MouvementStock.TypeMouvement.SORTIE);
            m.setQuantite(preleve);
            m.setMotif("Vente");
            m.setReferenceId(referenceId);
            m.setCreatedBy(userId);
            mouvementRepository.save(m);
        }

        stock.decrementer(qte);
        stockRepository.save(stock);

        if (stock.estEnRupture())      log.warn("RUPTURE : variante {}", varianteId);
        else if (stock.estSousSeuil()) log.warn("STOCK BAS : variante {}", varianteId);
    }

    /** Incrémentation à la réception d'une commande fournisseur */
    @Transactional
    public void incrementerStock(Lot lot, int qte, UUID referenceId, UUID userId) {
        Stock stock = stockRepository.findByVarianteId(lot.getVariante().getId())
            .orElseGet(() -> {
                Stock s = new Stock();
                s.setVariante(lot.getVariante());
                s.setQuantiteTotale(0);
                s.setSeuilMin(0);
                return s;
            });

        stock.incrementer(qte);
        stockRepository.save(stock);

        MouvementStock m = new MouvementStock();
        m.setLot(lot);
        m.setType(MouvementStock.TypeMouvement.ENTREE);
        m.setQuantite(qte);
        m.setMotif("Réception commande");
        m.setReferenceId(referenceId);
        m.setCreatedBy(userId);
        mouvementRepository.save(m);
    }

    /** Ajustement manuel (inventaire physique) */
    @Transactional
    public void ajusterStock(UUID varianteId, int nouvelleQte, String motif, UUID userId) {
        Stock stock = stockRepository.findByVarianteId(varianteId)
            .orElseThrow(() -> new ResourceNotFoundException("Stock", varianteId.toString()));
        stock.setQuantiteTotale(nouvelleQte);
        stockRepository.save(stock);
        log.info("Ajustement stock variante {} -> {}", varianteId, nouvelleQte);
    }

    public long countRuptures()  { return stockRepository.countRuptures(); }
    public long countStocksBas() { return stockRepository.countStocksBas(); }
}
