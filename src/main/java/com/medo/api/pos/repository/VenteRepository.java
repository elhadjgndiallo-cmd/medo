package com.medo.api.pos.repository;

import com.medo.api.pos.entity.Vente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface VenteRepository extends JpaRepository<Vente, UUID> {
    Page<Vente> findBySessionIdOrderByDateVenteDesc(UUID sessionId, Pageable pageable);

    @Query("SELECT COALESCE(SUM(v.montantTotal),0) FROM Vente v WHERE v.statut='VALIDEE' AND v.dateVente BETWEEN :debut AND :fin")
    BigDecimal sumMontantPeriode(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);

    @Query("SELECT COUNT(v) FROM Vente v WHERE v.statut='VALIDEE' AND v.dateVente BETWEEN :debut AND :fin")
    long countVentesPeriode(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);

    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(v.numeroTicket, 2) AS integer)), 0) FROM Vente v")
    int findLastTicketNumber();
}
