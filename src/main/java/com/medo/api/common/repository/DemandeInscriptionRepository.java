package com.medo.api.common.dao;

import com.medo.api.common.entity.DemandeInscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DemandeInscriptionRepository extends JpaRepository<DemandeInscription, UUID> {
    Optional<DemandeInscription> findByEmailContact(String emailContact);
    boolean existsByEmailContact(String emailContact);
    boolean existsBySousDomaineSouhaite(String sousDomaine);
    List<DemandeInscription> findAllByStatutOrderByCreatedAtDesc(DemandeInscription.StatutDemande statut);

    @Query("SELECT COUNT(d) FROM DemandeInscription d WHERE d.statut = 'EN_ATTENTE'")
    long countEnAttente();
}
