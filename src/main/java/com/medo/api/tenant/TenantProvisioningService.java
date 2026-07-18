package com.medo.api.tenant;

import com.medo.api.auth.entity.Role;
import com.medo.api.auth.entity.Utilisateur;
import com.medo.api.auth.repository.RoleRepository;
import com.medo.api.auth.repository.UtilisateurRepository;
import com.medo.api.common.entity.Tenant;
import com.medo.api.common.repository.TenantRepository;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

@Service
public class TenantProvisioningService {

    private static final Logger log = LoggerFactory.getLogger(TenantProvisioningService.class);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Value("${medo.flyway.tenant-location:classpath:db/migration/tenant}")
    private String tenantMigrationLocation;

    public void provisionTenant(String schemaName) {
        log.info("Provisionnement : {}", schemaName);
        try {
            createSchema(schemaName);
            runMigrations(schemaName);
            log.info("Tenant {} OK", schemaName);
        } catch (Exception e) {
            rollback(schemaName);
            throw new TenantProvisioningException("Erreur provisionnement : " + schemaName, e);
        }
    }

    public void deprovisionTenant(String schemaName) {
        try (Connection c = dataSource.getConnection();
             Statement st = c.createStatement()) {
            st.execute("DROP SCHEMA IF EXISTS " + schemaName + " CASCADE");
        } catch (SQLException e) {
            throw new TenantProvisioningException("Erreur suppression schéma : " + schemaName, e);
        }
    }

    public boolean schemaExists(String schemaName) {
        String sql = "SELECT COUNT(*) FROM information_schema.schemata WHERE schema_name = ?";
        try (Connection c = dataSource.getConnection();
             var ps = c.prepareStatement(sql)) {
            ps.setString(1, schemaName);
            var rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) { return false; }
    }

    private void createSchema(String s) throws SQLException {
        try (Connection c = dataSource.getConnection();
             Statement st = c.createStatement()) {
            st.execute("CREATE SCHEMA IF NOT EXISTS " + s);
        }
    }

    private void runMigrations(String s) {
        Flyway.configure()
            .dataSource(dataSource).schemas(s)
            .locations(tenantMigrationLocation)
            .baselineOnMigrate(true).validateOnMigrate(true)
            .load().migrate();
    }

    private void rollback(String s) {
        try (Connection c = dataSource.getConnection();
             Statement st = c.createStatement()) {
            st.execute("DROP SCHEMA IF EXISTS " + s + " CASCADE");
        } catch (SQLException ex) { log.error("Rollback échoué : {}", s); }
    }

    /**
     * Créer un nouveau tenant avec provisionnement de schéma
     */
    @Transactional
    public Tenant provisionNewTenant(String nomPharmacie, String sousDomaine, Tenant.PlanAbonnement plan) {
        log.info("Création d'un nouveau tenant : {} (sous-domaine: {})", nomPharmacie, sousDomaine);
        
        // Créer l'entité Tenant
        Tenant tenant = new Tenant();
        tenant.setNom(nomPharmacie);
        tenant.setSousDomaine(sousDomaine);
        tenant.setSchemaName(Tenant.buildSchemaName(sousDomaine));
        tenant.setPlan(plan != null ? plan : Tenant.PlanAbonnement.GRATUIT);
        tenant.setStatut(Tenant.StatutTenant.ACTIF);
        tenant.setEmailContact(sousDomaine + "@temp.com"); // Sera mis à jour après
        
        // Sauvegarder le tenant dans le schéma public
        tenant = tenantRepository.save(tenant);
        
        // Provisionner le schéma pour ce tenant
        provisionTenant(tenant.getSchemaName());
        
        log.info("Tenant {} créé avec succès (ID: {})", tenant.getSousDomaine(), tenant.getId());
        return tenant;
    }

    /**
     * Créer l'utilisateur administrateur pour un tenant
     */
    @Transactional
    public Utilisateur createAdminUser(Tenant tenant, String email, String motDePasseHash, String nomPharmacie) {
        log.info("Création de l'utilisateur admin pour le tenant : {}", tenant.getSousDomaine());
        
        // Mettre à jour l'email du tenant
        tenant.setEmailContact(email);
        tenantRepository.save(tenant);
        
        // Basculer vers le schéma du tenant
        TenantContext.setCurrentTenant(tenant.getSchemaName());
        
        try {
            // Créer l'utilisateur admin
            Utilisateur admin = new Utilisateur();
            admin.setNom(nomPharmacie);
            admin.setPrenom("Admin");
            admin.setEmail(email);
            admin.setMotDePasse(motDePasseHash);
            admin.setTypeUtilisateur(Utilisateur.TypeUser.ADMIN_PHARMACIE);
            admin.setActif(true);
            
            // Chercher ou créer le rôle ADMIN
            Role roleAdmin = roleRepository.findByNom("ADMIN")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setNom("ADMIN");
                    newRole.setDescription("Administrateur de la pharmacie");
                    return roleRepository.save(newRole);
                });
            
            Set<Role> roles = new HashSet<>();
            roles.add(roleAdmin);
            admin.setRoles(roles);
            
            admin = utilisateurRepository.save(admin);
            
            log.info("Utilisateur admin créé : {} pour le tenant {}", email, tenant.getSousDomaine());
            return admin;
            
        } finally {
            // Toujours nettoyer le contexte
            TenantContext.clear();
        }
    }

    public static class TenantProvisioningException extends RuntimeException {
        public TenantProvisioningException(String m, Throwable c) { super(m, c); }
    }
}
