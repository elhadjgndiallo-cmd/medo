package com.medo.api.rapports.dao;

import com.medo.api.common.entity.TicketSupport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketSupportRepository extends JpaRepository<TicketSupport, UUID> {

    // Count tickets by status (ignoring case) using a query method name that Spring Data understands
    long countByStatutIgnoringCase(String statut);

    // Alternative: using @Query if needed (though the method name above should work)
    // @Query("SELECT COUNT(t) FROM TicketSupport t WHERE UPPER(t.statut) = UPPER(:statut)")
    // long countByStatutIgnoringCase(@Param("statut") String statut);

    // Find tickets by status (optional: for listing)
    List<TicketSupport> findByStatutIgnoringCase(String statut);
}