package com.medo.api.achats.repository;

import com.medo.api.achats.entity.BonCommande;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface BonCommandeRepository extends JpaRepository<BonCommande, UUID> {
    Page<BonCommande> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<BonCommande> findByStatutOrderByCreatedAtDesc(BonCommande.StatutCommande statut, Pageable pageable);
    Page<BonCommande> findByFournisseurIdOrderByCreatedAtDesc(UUID fournisseurId, Pageable pageable);
    boolean existsByReference(String reference);

    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(b.reference, 9) AS integer)), 0) FROM BonCommande b")
    int findLastBcNumber();
}
