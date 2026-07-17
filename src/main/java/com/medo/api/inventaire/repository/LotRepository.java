package com.medo.api.inventaire.dao;

import com.medo.api.inventaire.entity.Lot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface LotRepository extends JpaRepository<Lot, UUID> {
    List<Lot> findByVarianteIdAndQuantiteGreaterThanOrderByDatePeremptionAsc(UUID varianteId, int qteMin);

    @Query("SELECT l FROM Lot l WHERE l.datePeremption <= :dl AND l.quantite > 0 ORDER BY l.datePeremption ASC")
    List<Lot> findLotsExpirantAvant(@Param("dl") LocalDate dateLimit);

    @Query("SELECT COUNT(l) FROM Lot l WHERE l.datePeremption <= :dl AND l.quantite > 0")
    long countLotsExpirantAvant(@Param("dl") LocalDate dateLimit);
}
