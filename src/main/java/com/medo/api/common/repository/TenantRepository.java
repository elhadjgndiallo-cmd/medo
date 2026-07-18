package com.medo.api.common.repository;

import com.medo.api.common.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {
    Optional<Tenant> findBySousDomaine(String sousDomaine);
    Optional<Tenant> findBySchemaName(String schemaName);
    boolean existsBySousDomaine(String sousDomaine);
    boolean existsBySchemaName(String schemaName);
    List<Tenant> findAllByStatut(Tenant.StatutTenant statut);

    @Query("SELECT COUNT(t) FROM Tenant t WHERE t.statut = 'ACTIF'")
    long countActifs();

    @Query("SELECT COUNT(t) FROM Tenant t WHERE t.plan = :plan AND t.statut = 'ACTIF'")
    long countByPlan(@Param("plan") Tenant.PlanAbonnement plan);
    
    // Méthodes supplémentaires pour SuperAdminService
    @Query("SELECT t FROM Tenant t WHERE t.statut = CASE WHEN :actif = true THEN 'ACTIF' ELSE 'SUSPENDU' END")
    org.springframework.data.domain.Page<Tenant> findByActif(@Param("actif") Boolean actif, org.springframework.data.domain.Pageable pageable);
    
    @Query("SELECT COUNT(t) FROM Tenant t WHERE t.statut = 'ACTIF'")
    long countByActifTrue();
}
