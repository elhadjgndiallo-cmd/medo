package com.medo.api.common.service;

import com.medo.api.common.entity.Abonnement;
import com.medo.api.common.entity.DemandeInscription;
import com.medo.api.common.entity.Tenant;
import com.medo.api.common.entity.TicketSupport;
import com.medo.api.common.repository.AbonnementRepository;
import com.medo.api.common.repository.DemandeInscriptionRepository;
import com.medo.api.common.repository.TenantRepository;
import com.medo.api.common.repository.TicketSupportRepository;
import com.medo.api.exception.GlobalExceptionHandler.*;
import com.medo.api.tenant.TenantProvisioningService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class SuperAdminService {

    private static final Logger log = LoggerFactory.getLogger(SuperAdminService.class);

    @Autowired private DemandeInscriptionRepository demandeRepository;
    @Autowired private TenantRepository tenantRepository;
    @Autowired private AbonnementRepository abonnementRepository;
    @Autowired private TicketSupportRepository ticketRepository;
    @Autowired private TenantProvisioningService tenantProvisioningService;

    // ── Gestion des Demandes d'Inscription ──

    @Transactional(readOnly = true)
    public Page<DemandeInscription> listerDemandes(DemandeInscription.StatutDemande statut, Pageable pageable) {
        if (statut != null) {
            return demandeRepository.findAllByStatutOrderByCreatedAtDesc(statut, pageable);
        }
        return demandeRepository.findAll(pageable);
    }

    @Transactional
    public Tenant accepterDemande(UUID demandeId, UUID superAdminId) {
        DemandeInscription demande = demandeRepository.findById(demandeId)
            .orElseThrow(() -> new ResourceNotFoundException("Demande", demandeId.toString()));

        if (!demande.isEnAttente()) {
            throw new BusinessRuleException("Demande déjà traitée");
        }

        log.info("Acceptation demande inscription : {}", demande.getEmailContact());

        // Créer le tenant
        Tenant tenant = tenantProvisioningService.provisionNewTenant(
            demande.getNomPharmacie(),
            demande.getSousDomaineSouhaite(),
            demande.getPlanDemande()
        );

        // Créer l'utilisateur admin
        tenantProvisioningService.createAdminUser(
            tenant,
            demande.getEmailContact(),
            demande.getMotDePasseHash(),
            demande.getNomPharmacie()
        );

        // Mettre à jour la demande
        demande.accepter(tenant.getId(), superAdminId);
        demandeRepository.save(demande);

        log.info("Tenant {} créé avec succès pour {}", tenant.getSlug(), demande.getEmailContact());
        return tenant;
    }

    @Transactional
    public void rejeterDemande(UUID demandeId, String motif, UUID superAdminId) {
        DemandeInscription demande = demandeRepository.findById(demandeId)
            .orElseThrow(() -> new ResourceNotFoundException("Demande", demandeId.toString()));

        if (!demande.isEnAttente()) {
            throw new BusinessRuleException("Demande déjà traitée");
        }

        demande.rejeter(motif, superAdminId);
        demandeRepository.save(demande);

        log.info("Demande {} rejetée : {}", demandeId, motif);
    }

    // ── Gestion des Tenants ──

    @Transactional(readOnly = true)
    public Page<Tenant> listerTenants(Boolean actif, Pageable pageable) {
        if (actif != null) {
            return tenantRepository.findByActif(actif, pageable);
        }
        return tenantRepository.findAll(pageable);
    }

    @Transactional
    public void suspendreTenant(UUID tenantId, String raison) {
        Tenant tenant = tenantRepository.findById(tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Tenant", tenantId.toString()));

        tenant.setActif(false);
        tenant.setUpdatedAt(LocalDateTime.now());
        tenantRepository.save(tenant);

        log.warn("Tenant {} suspendu : {}", tenant.getSlug(), raison);
    }

    @Transactional
    public void reactiverTenant(UUID tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Tenant", tenantId.toString()));

        tenant.setActif(true);
        tenant.setUpdatedAt(LocalDateTime.now());
        tenantRepository.save(tenant);

        log.info("Tenant {} réactivé", tenant.getSlug());
    }

    // ── Gestion des Abonnements ──

    @Transactional(readOnly = true)
    public List<Abonnement> getAbonnementsTenant(UUID tenantId) {
        return abonnementRepository.findByTenantIdOrderByCreatedAtDesc(tenantId);
    }

    @Transactional
    public Abonnement creerAbonnement(UUID tenantId, Tenant.PlanAbonnement plan, 
                                     Abonnement.PeriodeFacturation periode) {
        Tenant tenant = tenantRepository.findById(tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Tenant", tenantId.toString()));

        Abonnement abonnement = new Abonnement();
        abonnement.setTenant(tenant);
        abonnement.setPlan(plan);
        abonnement.setPeriodeFacturation(periode);
        abonnement.setDateDebut(java.time.LocalDate.now());
        abonnement.setDateFin(java.time.LocalDate.now().plusMonths(1)); // TODO: Calculer selon période
        abonnement.setStatut(Abonnement.StatutAbonnement.ACTIF);

        return abonnementRepository.save(abonnement);
    }

    // ── Gestion des Tickets Support ──

    @Transactional(readOnly = true)
    public Page<TicketSupport> listerTickets(TicketSupport.StatutTicket statut, Pageable pageable) {
        if (statut != null) {
            return ticketRepository.findByStatutOrderByCreatedAtDesc(statut, pageable);
        }
        return ticketRepository.findAllOrderByPrioriteDescCreatedAtDesc(pageable);
    }

    @Transactional
    public void assignerTicket(UUID ticketId, UUID adminId) {
        TicketSupport ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket", ticketId.toString()));

        ticket.setAssigneA(adminId);
        ticket.setStatut(TicketSupport.StatutTicket.EN_COURS);
        ticket.setUpdatedAt(LocalDateTime.now());
        ticketRepository.save(ticket);
    }

    @Transactional
    public void resoudreTicket(UUID ticketId, String commentaire, UUID adminId) {
        TicketSupport ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket", ticketId.toString()));

        ticket.resoudre(commentaire, adminId);
        ticketRepository.save(ticket);

        log.info("Ticket {} résolu par {}", ticket.getNumeroTicket(), adminId);
    }

    // ── Statistiques ──

    @Transactional(readOnly = true)
    public Map<String, Object> getStatistiquesGlobales() {
        Map<String, Object> stats = new java.util.HashMap<>();
        
        stats.put("totalTenants", tenantRepository.count());
        stats.put("tenantsActifs", tenantRepository.countByActifTrue());
        stats.put("demandesEnAttente", demandeRepository.countEnAttente());
        stats.put("ticketsOuverts", ticketRepository.countByStatut(TicketSupport.StatutTicket.OUVERT));
        
        return stats;
    }
}
