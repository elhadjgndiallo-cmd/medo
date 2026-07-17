package com.medo.api.inventaire.dao;

import com.medo.api.inventaire.entity.Emplacement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmplacementRepository extends JpaRepository<Emplacement, UUID> {
    Optional<Emplacement> findByCode(String code);
    boolean existsByCode(String code);
}
