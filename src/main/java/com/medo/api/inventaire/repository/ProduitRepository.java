package com.medo.api.inventaire.repository;

import com.medo.api.inventaire.entity.Produit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, UUID> {
    Page<Produit> findAllByActifTrue(Pageable pageable);
    Page<Produit> findByNomContainingIgnoreCaseAndActifTrue(String nom, Pageable pageable);
    Optional<Produit> findByCodeBarres(String codeBarres);
    boolean existsByCodeBarres(String codeBarres);

    @Query("SELECT DISTINCT p.categorie FROM Produit p WHERE p.actif = true AND p.categorie IS NOT NULL ORDER BY p.categorie")
    List<String> findAllCategories();

    @Query("SELECT p FROM Produit p JOIN p.variantes v JOIN v.stock s WHERE p.actif = true AND s.quantiteTotale <= s.seuilMin ORDER BY s.quantiteTotale ASC")
    List<Produit> findProduitsStockBas();
}
