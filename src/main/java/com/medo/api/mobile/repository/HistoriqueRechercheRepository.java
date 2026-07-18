package com.medo.api.mobile.repository;

import com.medo.api.mobile.entity.HistoriqueRecherche;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Repository
public interface HistoriqueRechercheRepository extends JpaRepository<HistoriqueRecherche, UUID> {
    Page<HistoriqueRecherche> findByClientIdOrderByCreatedAtDesc(UUID clientId, Pageable pageable);
    long countByClientId(UUID clientId);

    @Query("SELECT COUNT(DISTINCT h.termeProduit) FROM HistoriqueRecherche h WHERE h.client.id = :id AND h.nombreResultats > 0")
    long countPharmaciesTouchees(@Param("id") UUID clientId);

    @Transactional
    void deleteByClientId(UUID clientId);
}
