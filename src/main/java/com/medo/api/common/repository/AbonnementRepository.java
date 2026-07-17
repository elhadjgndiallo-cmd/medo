package com.medo.api.common.repository;

import com.medo.api.common.entity.Abonnement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface AbonnementRepository extends JpaRepository<Abonnement, UUID> {
    List<Abonnement> findByTenantIdOrderByCreatedAtDesc(UUID tenantId);
    List<Abonnement> findByStatut(Abonnement.StatutAbonnement statut);
    long countByStatut(Abonnement.StatutAbonnement statut);
}
