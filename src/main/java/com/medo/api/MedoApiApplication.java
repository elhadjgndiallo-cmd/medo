package com.medo.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Classe principale de l'application MEDO API
 * 
 * Architecture Multi-Tenant SCHEMA-based avec Spring Boot
 * 
 * Features activées :
 * - Multi-tenancy avec isolation par schéma PostgreSQL
 * - Cache Redis pour améliorer les performances
 * - Aspect AOP pour RBAC et permissions
 * - JPA Auditing pour tracking des modifications
 * - Tâches asynchrones et schedulées
 * - Sécurité JWT avec Spring Security
 * 
 * Configuration Flyway désactivée (gérée manuellement dans FlywayConfig)
 * 
 * @author MEDO Team
 * @version 1.0.0
 * @since 2026-07-16
 */
@SpringBootApplication(exclude = FlywayAutoConfiguration.class)
@EnableAsync
@EnableScheduling
@EnableCaching
@EnableAspectJAutoProxy
@EnableJpaAuditing
public class MedoApiApplication {

    /**
     * Point d'entrée principal de l'application
     * 
     * Au démarrage :
     * 1. Configuration Spring Boot chargée (application.yml)
     * 2. Connexion PostgreSQL établie
     * 3. Connexion Redis établie
     * 4. Migrations Flyway exécutées (schéma public + tenants existants)
     * 5. Spring Security initialisé avec JWT
     * 6. Multi-tenant context configuré
     * 7. Serveur Tomcat démarré sur port 8080
     * 8. Swagger UI disponible sur /swagger-ui
     * 
     * @param args Arguments ligne de commande
     */
    public static void main(String[] args) {
        SpringApplication.run(MedoApiApplication.class, args);
        
        System.out.println("\n" +
            "╔══════════════════════════════════════════════════════════════╗\n" +
            "║                                                              ║\n" +
            "║              🏥 MEDO API - Successfully Started! 🏥          ║\n" +
            "║                                                              ║\n" +
            "║  Multi-Tenant Pharmacy Management System                    ║\n" +
            "║  Version: 1.0.0                                              ║\n" +
            "║                                                              ║\n" +
            "║  📊 Swagger UI:   http://localhost:8080/swagger-ui          ║\n" +
            "║  📖 API Docs:     http://localhost:8080/api-docs            ║\n" +
            "║  ❤️  Health Check: http://localhost:8080/actuator/health    ║\n" +
            "║                                                              ║\n" +
            "║  🔐 Authentication: JWT (Bearer token)                       ║\n" +
            "║  🏗️  Architecture: Multi-Tenant SCHEMA-based                ║\n" +
            "║  💾 Database: PostgreSQL + Redis                             ║\n" +
            "║                                                              ║\n" +
            "║  Ready for requests! 🚀                                      ║\n" +
            "║                                                              ║\n" +
            "╚══════════════════════════════════════════════════════════════╝\n"
        );
    }
}
