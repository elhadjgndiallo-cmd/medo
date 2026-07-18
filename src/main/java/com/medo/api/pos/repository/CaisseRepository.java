package com.medo.api.pos.repository;

import com.medo.api.pos.entity.Caisse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface CaisseRepository extends JpaRepository<Caisse, UUID> {
    List<Caisse> findAllByActifTrue();
    boolean existsByNom(String nom);
}
