package com.medo.api.mobile.repository;

import com.medo.api.mobile.entity.Favori;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FavoriRepository extends JpaRepository<Favori, UUID> {

    List<Favori> findByClientIdOrderByCreatedAtDesc(UUID clientId);

    boolean existsByClientIdAndPharmacieTenantId(UUID clientId, UUID pharmacieTenantId);

    Optional<Favori> findByClientIdAndPharmacieTenantId(UUID clientId, UUID pharmacieTenantId);

    @Transactional
    void deleteByClientIdAndPharmacieTenantId(UUID clientId, UUID pharmacieTenantId);

    long countByClientId(UUID clientId);
}
