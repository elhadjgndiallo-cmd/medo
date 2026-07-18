package com.medo.api.inventaire.repository;

import com.medo.api.inventaire.entity.VarianteProduit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VarianteProduitRepository extends JpaRepository<VarianteProduit, UUID> {

    List<VarianteProduit> findByProduitId(UUID produitId);

    List<VarianteProduit> findByProduitIdAndActifTrue(UUID produitId);

    @Query("SELECT v FROM VarianteProduit v JOIN FETCH v.produit WHERE v.id = :id")
    Optional<VarianteProduit> findByIdWithProduit(@Param("id") UUID id);
}
