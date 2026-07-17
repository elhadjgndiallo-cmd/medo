package com.medo.api.inventaire.dao;

import com.medo.api.inventaire.entity.MouvementStock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface MouvementStockRepository extends JpaRepository<MouvementStock, UUID> {
    Page<MouvementStock> findByLotVarianteIdOrderByCreatedAtDesc(UUID varianteId, Pageable pageable);
}
