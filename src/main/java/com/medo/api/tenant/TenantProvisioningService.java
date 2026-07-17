package com.medo.api.tenant;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Service
public class TenantProvisioningService {

    private static final Logger log = LoggerFactory.getLogger(TenantProvisioningService.class);

    @Autowired
    private DataSource dataSource;

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

    public static class TenantProvisioningException extends RuntimeException {
        public TenantProvisioningException(String m, Throwable c) { super(m, c); }
    }
}
