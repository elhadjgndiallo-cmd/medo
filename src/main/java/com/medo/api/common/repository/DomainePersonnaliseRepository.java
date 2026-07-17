package com.medo.api.common.repository;

import com.medo.api.common.entity.DomainePersonnalise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DomainePersonnaliseRepository extends JpaRepository<DomainePersonnalise, UUID> {
    Optional<DomainePersonnalise> findByNomDomaine(String nomDomaine);
    List<DomainePersonnalise> findByTenantId(UUID tenantId);
    boolean existsByNomDomaine(String nomDomaine);
}
