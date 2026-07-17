package com.medo.api.achats.dao;

import com.medo.api.achats.entity.Fournisseur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface FournisseurRepository extends JpaRepository<Fournisseur, UUID> {
    List<Fournisseur> findAllByActifTrue();
    Page<Fournisseur> findByNomContainingIgnoreCaseAndActifTrue(String nom, Pageable pageable);
    boolean existsByNom(String nom);
}
