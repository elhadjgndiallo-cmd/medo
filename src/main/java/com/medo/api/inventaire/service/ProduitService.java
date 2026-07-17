package com.medo.api.inventaire.services;

import com.medo.api.exception.GlobalExceptionHandler.*;
import com.medo.api.inventaire.dao.*;
import com.medo.api.inventaire.dto.InventaireDtos.*;
import com.medo.api.inventaire.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProduitService {

    @Autowired private ProduitRepository    produitRepository;
    @Autowired private StockRepository      stockRepository;
    @Autowired private LotRepository        lotRepository;
    @Autowired private MouvementStockRepository mouvementRepository;
    @Autowired private EmplacementRepository emplacementRepository;

    @Transactional(readOnly = true)
    public Page<ProduitResponse> listerProduits(String search, Pageable pageable) {
        Page<Produit> page = (search != null && !search.isBlank())
            ? produitRepository.findByNomContainingIgnoreCaseAndActifTrue(search, pageable)
            : produitRepository.findAllByActifTrue(pageable);
        return page.map(this::toProduitResponse);
    }

    @Transactional(readOnly = true)
    public ProduitResponse getProduit(UUID id) {
        return toProduitResponse(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public ProduitResponse getParCodeBarres(String code) {
        return toProduitResponse(produitRepository.findByCodeBarres(code)
            .orElseThrow(() -> new ResourceNotFoundException("Produit", code)));
    }

    @Transactional
    public ProduitResponse creerProduit(CreerProduitRequest req) {
        if (req.getCodeBarres() != null && produitRepository.existsByCodeBarres(req.getCodeBarres()))
            throw new DuplicateResourceException("Code-barres déjà utilisé : " + req.getCodeBarres());

        Produit p = new Produit();
        p.setNom(req.getNom()); p.setDci(req.getDci());
        p.setCategorie(req.getCategorie()); p.setCodeBarres(req.getCodeBarres());
        p.setIconeType(req.getIconeType() != null ? req.getIconeType() : "pill");
        p.setPrixVente(req.getPrixVente()); p.setPrixAchat(req.getPrixAchat());
        p.setActif(true);
        return toProduitResponse(produitRepository.save(p));
    }

    @Transactional
    public ProduitResponse modifierProduit(UUID id, CreerProduitRequest req) {
        Produit p = findOrThrow(id);
        p.setNom(req.getNom()); p.setDci(req.getDci());
        p.setCategorie(req.getCategorie());
        p.setPrixVente(req.getPrixVente()); p.setPrixAchat(req.getPrixAchat());
        if (req.getIconeType() != null) p.setIconeType(req.getIconeType());
        return toProduitResponse(produitRepository.save(p));
    }

    @Transactional
    public void desactiverProduit(UUID id) {
        Produit p = findOrThrow(id);
        p.desactiver();
        produitRepository.save(p);
    }

    @Transactional(readOnly = true)
    public List<String> getCategories() {
        return produitRepository.findAllCategories();
    }

    @Transactional
    public VarianteResponse creerVariante(CreerVarianteRequest req) {
        Produit produit = findOrThrow(req.getProduitId());

        VarianteProduit v = new VarianteProduit();
        v.setProduit(produit); v.setDosage(req.getDosage());
        v.setForme(req.getForme()); v.setUnite(req.getUnite());
        v.setCodeBarres(req.getCodeBarres()); v.setPrixVente(req.getPrixVente());
        v.setActif(true);

        Stock stock = new Stock();
        stock.setVariante(v); stock.setQuantiteTotale(0);
        stock.setSeuilMin(req.getSeuilMin()); stock.setSeuilMax(req.getSeuilMax());
        v.setStock(stock);

        produit.getVariantes().add(v);
        produitRepository.save(produit);
        return toVarianteResponse(v);
    }

    @Transactional
    public LotResponse creerLot(CreerLotRequest req, UUID userId) {
        VarianteProduit variante = produitRepository.findAll().stream()
            .flatMap(p -> p.getVariantes().stream())
            .filter(v -> v.getId().equals(req.getVarianteId()))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Variante", req.getVarianteId().toString()));

        Emplacement emplacement = null;
        if (req.getEmplacementId() != null) {
            emplacement = emplacementRepository.findById(req.getEmplacementId()).orElse(null);
        }

        Lot lot = new Lot();
        lot.setVariante(variante); lot.setEmplacement(emplacement);
        lot.setNumeroLot(req.getNumeroLot());
        lot.setDateFabrication(req.getDateFabrication());
        lot.setDatePeremption(req.getDatePeremption());
        lot.setQuantite(req.getQuantite());
        lotRepository.save(lot);

        Stock stock = stockRepository.findByVarianteId(req.getVarianteId()).orElseGet(() -> {
            Stock s = new Stock(); s.setVariante(variante);
            s.setQuantiteTotale(0); s.setSeuilMin(0); return s;
        });
        stock.incrementer(req.getQuantite());
        stockRepository.save(stock);

        MouvementStock m = new MouvementStock();
        m.setLot(lot); m.setType(MouvementStock.TypeMouvement.ENTREE);
        m.setQuantite(req.getQuantite()); m.setMotif("Création lot"); m.setCreatedBy(userId);
        mouvementRepository.save(m);

        return toLotResponse(lot);
    }

    @Transactional(readOnly = true)
    public StatsInventaireResponse getStats() {
        return new StatsInventaireResponse(
            produitRepository.count(),
            produitRepository.findAllByActifTrue(Pageable.unpaged()).getTotalElements(),
            stockRepository.countRuptures(),
            stockRepository.countStocksBas(),
            lotRepository.countLotsExpirantAvant(LocalDate.now().plusDays(60))
        );
    }

    // ── Mappers ──
    private ProduitResponse toProduitResponse(Produit p) {
        List<VarianteResponse> variantes = p.getVariantes() == null ? List.of()
            : p.getVariantes().stream().map(this::toVarianteResponse).collect(Collectors.toList());
        return new ProduitResponse(p.getId(), p.getNom(), p.getDci(), p.getCategorie(),
            p.getCodeBarres(), p.getIconeType(), Boolean.TRUE.equals(p.getActif()),
            p.getPrixVente(), p.getPrixAchat(), variantes, p.getCreatedAt());
    }

    private VarianteResponse toVarianteResponse(VarianteProduit v) {
        StockResponse sr = v.getStock() == null ? null
            : new StockResponse(v.getStock().getId(), v.getStock().getQuantiteTotale(),
                v.getStock().getSeuilMin(), v.getStock().getSeuilMax(), v.getStock().getStatutBadge());
        return new VarianteResponse(v.getId(), v.getDosage(), v.getForme(), v.getUnite(),
            v.getCodeBarres(), v.getPrixEffectif(), Boolean.TRUE.equals(v.getActif()), sr);
    }

    private LotResponse toLotResponse(Lot l) {
        return new LotResponse(l.getId(), l.getNumeroLot(), l.getDateFabrication(),
            l.getDatePeremption(), l.getQuantite(), l.getStatutPeremption());
    }

    private Produit findOrThrow(UUID id) {
        return produitRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Produit", id.toString()));
    }
}
