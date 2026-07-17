package com.medo.api.common.repository;

import com.medo.api.common.entity.TicketSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface TicketSupportRepository extends JpaRepository<TicketSupport, UUID> {
    Page<TicketSupport> findByStatutOrderByCreatedAtDesc(TicketSupport.StatutTicket statut, Pageable pageable);
    
    @Query("SELECT t FROM TicketSupport t ORDER BY " +
           "CASE t.priorite " +
           "WHEN 'URGENTE' THEN 1 " +
           "WHEN 'HAUTE' THEN 2 " +
           "WHEN 'NORMALE' THEN 3 " +
           "WHEN 'BASSE' THEN 4 END, " +
           "t.createdAt DESC")
    Page<TicketSupport> findAllOrderByPrioriteDescCreatedAtDesc(Pageable pageable);
    
    long countByStatut(TicketSupport.StatutTicket statut);
}
