package com.medo.api.auth.dao;

import com.medo.api.auth.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, UUID> {
    Optional<Utilisateur> findByEmail(String email);
    Optional<Utilisateur> findByEmailAndActifTrue(String email);
    boolean existsByEmail(String email);
    long countByActifTrue();

    @Modifying
    @Query("UPDATE Utilisateur u SET u.dernierConnexion = :date WHERE u.id = :id")
    void updateDernierConnexion(@Param("id") UUID id, @Param("date") LocalDateTime date);

    @Query("SELECT u FROM Utilisateur u JOIN FETCH u.roles r JOIN FETCH r.permissions WHERE u.email = :email")
    Optional<Utilisateur> findByEmailWithRoles(@Param("email") String email);
}
