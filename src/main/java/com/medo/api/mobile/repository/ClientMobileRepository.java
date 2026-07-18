package com.medo.api.mobile.repository;

import com.medo.api.mobile.entity.ClientMobile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientMobileRepository extends JpaRepository<ClientMobile, UUID> {
    Optional<ClientMobile> findByEmail(String email);
    Optional<ClientMobile> findByEmailAndActifTrue(String email);
    boolean existsByEmail(String email);
}
