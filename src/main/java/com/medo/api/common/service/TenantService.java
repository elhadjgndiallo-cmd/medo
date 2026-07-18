package com.medo.api.common.services;

import com.medo.api.auth.repository.UtilisateurRepository;
import com.medo.api.auth.entity.Utilisateur;
import com.medo.api.common.repository.DemandeInscriptionRepository;
import com.medo.api.common.repository.TenantRepository;
import com.medo.api.common.dto.SuperAdminDtos.*;
import com.medo.api.common.entity.DemandeInscription;
import com.medo.api.common.entity.Tenant;
import com.medo.api.exception.GlobalExceptionHandler.*;
import com.medo.api.tenant.TenantContext;
import com.medo.api.tenant.TenantProvisioningService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TenantService {

    private static final Logger log = LoggerFactory.getLogger(TenantService.class);

    @Autowired private TenantRepository tenantRepository;
    @Autowired private DemandeInscriptionRepository demandeRepository;
    @Autowired private UtilisateurRepository utilisateurRepository;
    @Autowired private TenantProvisioningService provisioningService;

    public PlateformeStatsResponse getStats() {
        return new PlateformeStatsResponse(
            tenantRepository.countActifs(),
            tenantRepository.countByPlan(Tenant.PlanAbonnement.GRATUIT),
            tenantRepository.countByPlan(Tenant.PlanAbonnement.PRO),
            demandeRepository.countEnAttente()
        );
    }

    public List<DemandeResponse> getDemandesEnAttente() {
        return demandeRepository
            .findAllByStatutOrderByCreatedAtDesc(
                DemandeInscription.StatutDemande.EN_ATTENTE, 
                org.springframework.data.domain.PageRequest.of(0, 1000)
            )
            .getContent()
            .stream().map(this::toDemandeResponse).collect(Collectors.toList());
    }

    public List<DemandeResponse> getToutesDemandes() {
        return demandeRepository.findAll().stream()
            .map(this::toDemandeResponse).collect(Collectors.toList());
    }

    @Transactional
    public TenantResponse accepterDemande(UUID demandeId, UUID superAdminId) {
        DemandeInscription demande = demandeRepository.findById(demandeId)
            .orElseThrow(() -> new ResourceNotFoundException("Demande", demandeId.toString()));

        if (!demande.isEnAttente())
            throw new BusinessRuleException("Demande déjà traitée (statut: " + demande.getStatut() + ")");

        String sousDomaine = demande.getSousDomaineSouhaite();
        if (tenantRepository.existsBySousDomaine(sousDomaine))
            throw new DuplicateResourceException("Sous-domaine déjà utilisé : " + sousDomaine);

        String schemaName = Tenant.buildSchemaName(sousDomaine);

        Tenant tenant = new Tenant();
        tenant.setNom(demande.getNomPharmacie());
        tenant.setSousDomaine(sousDomaine);
        tenant.setSchemaName(schemaName);
        tenant.setEmailContact(demande.getEmailContact());
        tenant.setPlan(demande.getPlanDemande());
        tenant.setStatut(Tenant.StatutTenant.ACTIF);
        tenant = tenantRepository.save(tenant);

        provisioningService.provisionTenant(schemaName);
        creerAdminPharmacie(demande, schemaName);

        demande.accepter(tenant.getId(), superAdminId);
        demandeRepository.save(demande);
        log.info("Pharmacie validée : {} -> {}", demande.getNomPharmacie(), schemaName);
        return toTenantResponse(tenant);
    }

    @Transactional
    public void rejeterDemande(UUID demandeId, String motif, UUID superAdminId) {
        DemandeInscription demande = demandeRepository.findById(demandeId)
            .orElseThrow(() -> new ResourceNotFoundException("Demande", demandeId.toString()));
        if (!demande.isEnAttente())
            throw new BusinessRuleException("Demande déjà traitée");
        demande.rejeter(motif, superAdminId);
        demandeRepository.save(demande);
    }

    public List<TenantResponse> getAllTenants() {
        return tenantRepository.findAll().stream()
            .map(this::toTenantResponse).collect(Collectors.toList());
    }

    public TenantResponse getTenant(UUID id) {
        return toTenantResponse(tenantRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tenant", id.toString())));
    }

    @Transactional
    public TenantResponse suspendre(UUID id) {
        Tenant t = tenantRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tenant", id.toString()));
        t.suspendre();
        return toTenantResponse(tenantRepository.save(t));
    }

    @Transactional
    public TenantResponse reactiver(UUID id) {
        Tenant t = tenantRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tenant", id.toString()));
        t.reactiver();
        return toTenantResponse(tenantRepository.save(t));
    }

    private void creerAdminPharmacie(DemandeInscription demande, String schemaName) {
        TenantContext.setCurrentTenant(schemaName);
        try {
            Utilisateur admin = new Utilisateur();
            admin.setNom(demande.getNomPharmacie());
            admin.setPrenom("Admin");
            admin.setEmail(demande.getEmailContact());
            admin.setMotDePasse(demande.getMotDePasseHash());
            admin.setTypeUtilisateur(Utilisateur.TypeUser.ADMIN_PHARMACIE);
            admin.setActif(true);
            utilisateurRepository.save(admin);
        } finally {
            TenantContext.clear();
        }
    }

    private DemandeResponse toDemandeResponse(DemandeInscription d) {
        return new DemandeResponse(d.getId(), d.getNomPharmacie(), d.getEmailContact(),
            d.getSousDomaineSouhaite(), d.getPlanDemande().name(), d.getStatut().name(), d.getCreatedAt());
    }

    private TenantResponse toTenantResponse(Tenant t) {
        return new TenantResponse(t.getId(), t.getNom(), t.getSousDomaine(),
            t.getSchemaName(), t.getStatut().name(), t.getPlan().name(),
            t.getEmailContact(), t.getCreatedAt());
    }
}
