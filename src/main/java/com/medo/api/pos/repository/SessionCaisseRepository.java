package com.medo.api.pos.repository;

import com.medo.api.pos.entity.SessionCaisse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionCaisseRepository extends JpaRepository<SessionCaisse, UUID> {
    Optional<SessionCaisse> findByCaisseIdAndStatut(UUID caisseId, SessionCaisse.StatutSession statut);

    @Query("SELECT COUNT(s) FROM SessionCaisse s WHERE s.statut = 'OUVERTE'")
    long countOuvertes();

    List<SessionCaisse> findByUtilisateurIdOrderByDateOuvertureDesc(UUID userId);
}
