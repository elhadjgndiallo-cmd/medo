package com.medo.api.pharmacie.dao;

import com.medo.api.pharmacie.entity.Pharmacie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PharmacieRepository extends JpaRepository<Pharmacie, UUID> {
    Optional<Pharmacie> findFirstByOrderByCreatedAtAsc();
}
