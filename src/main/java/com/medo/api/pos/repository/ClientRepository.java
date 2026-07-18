package com.medo.api.pos.repository;

import com.medo.api.pos.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {
    Page<Client> findByNomContainingIgnoreCase(String nom, Pageable pageable);
}
