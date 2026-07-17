package com.medo.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration Swagger/OpenAPI pour la documentation interactive de l'API
 * 
 * Accessible via :
 * - Swagger UI : http://localhost:8080/swagger-ui
 * - OpenAPI JSON : http://localhost:8080/api-docs
 * 
 * Configuration :
 * - Authentification JWT Bearer
 * - Header X-Tenant pour multi-tenancy
 * - Documentation complète des endpoints
 * 
 * @author MEDO Team
 * @version 1.0.0
 */
@Configuration
public class SwaggerConfig {

    @Value("${spring.application.name:MEDO API}")
    private String applicationName;

    @Value("${spring.application.version:1.0.0}")
    private String applicationVersion;

    @Value("${server.port:8080}")
    private String serverPort;

    /**
     * Configuration OpenAPI 3.0
     * 
     * @return Instance OpenAPI configurée
     */
    @Bean
    public OpenAPI medoApiOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(apiServers())
                .components(securityComponents())
                .addSecurityItem(new SecurityRequirement()
                        .addList("Bearer Authentication")
                        .addList("Tenant Header"));
    }

    /**
     * Informations générales de l'API
     */
    private Info apiInfo() {
        return new Info()
                .title("MEDO API - Système de Gestion de Pharmacies")
                .version(applicationVersion)
                .description(
                    "API REST Multi-Tenant pour la gestion de pharmacies avec application mobile.\n\n" +
                    "## Architecture\n" +
                    "- **Multi-Tenancy** : SCHEMA-based (un schéma PostgreSQL par pharmacie)\n" +
                    "- **Authentification** : JWT Bearer tokens (Access + Refresh)\n" +
                    "- **Sécurité** : RBAC avec permissions granulaires\n" +
                    "- **Cache** : Redis pour optimiser les performances\n\n" +
                    "## Modules Disponibles\n" +
                    "- 🔐 **Auth** : Authentification et inscriptions\n" +
                    "- 📊 **Rapports** : Statistiques et tableaux de bord\n" +
                    "- 🛒 **POS** : Point de vente et caisses\n" +
                    "- 📦 **Inventaire** : Produits, stocks, lots\n" +
                    "- 🛍️ **Achats** : Fournisseurs et bons de commande\n" +
                    "- 📱 **Mobile** : API publique pour clients\n" +
                    "- 👨‍💼 **Admin** : Gestion plateforme (Super Admin)\n\n" +
                    "## Headers Requis\n" +
                    "- **Authorization**: `Bearer {access_token}` (pour endpoints authentifiés)\n" +
                    "- **X-Tenant**: `{tenant_id}` (pour pharmacies, ex: pharma_abc)\n\n" +
                    "## Endpoints Publics (sans auth)\n" +
                    "- `/api/v1/public/*` : Recherche médicaments, pharmacies proximité\n" +
                    "- `/api/v1/auth/inscription` : Demande inscription pharmacie\n\n" +
                    "## Workflow Authentification\n" +
                    "1. POST `/api/v1/auth/login` → Récupérer access_token + refresh_token\n" +
                    "2. Utiliser access_token dans header `Authorization: Bearer {token}`\n" +
                    "3. Ajouter header `X-Tenant: {tenant_id}` pour requêtes pharmacie\n" +
                    "4. Renouveler avec POST `/api/v1/auth/refresh` si token expiré"
                )
                .contact(new Contact()
                        .name("MEDO Team")
                        .email("support@medo.app")
                        .url("https://medo.app"))
                .license(new License()
                        .name("Propriétaire - MEDO © 2024")
                        .url("https://medo.app/license"));
    }

    /**
     * Serveurs API (local, staging, production)
     */
    private List<Server> apiServers() {
        return List.of(
                new Server()
                        .url("http://localhost:" + serverPort)
                        .description("Local Development Server"),
                new Server()
                        .url("https://api-staging.medo.app")
                        .description("Staging Server"),
                new Server()
                        .url("https://api.medo.app")
                        .description("Production Server")
        );
    }

    /**
     * Configuration sécurité pour Swagger
     * 
     * Définit les schémas de sécurité :
     * - JWT Bearer Authentication
     * - X-Tenant Header pour multi-tenancy
     */
    private Components securityComponents() {
        return new Components()
                // JWT Bearer Authentication
                .addSecuritySchemes("Bearer Authentication",
                        new SecurityScheme()
                                .name("Authorization")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description(
                                    "Authentification JWT.\n\n" +
                                    "**Comment obtenir un token :**\n" +
                                    "1. POST `/api/v1/auth/login` avec email + motDePasse\n" +
                                    "2. Récupérer le `accessToken` de la réponse\n" +
                                    "3. Cliquer sur 'Authorize' et entrer : `{accessToken}`\n\n" +
                                    "**Format :** `Bearer {accessToken}`\n\n" +
                                    "**Durée de vie :** 30 minutes\n\n" +
                                    "**Renouvellement :** POST `/api/v1/auth/refresh`"
                                ))
                // X-Tenant Header
                .addSecuritySchemes("Tenant Header",
                        new SecurityScheme()
                                .name("X-Tenant")
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .description(
                                    "Identifiant du tenant (pharmacie).\n\n" +
                                    "**Obligatoire pour :** Tous les endpoints pharmacie\n\n" +
                                    "**Exemples :**\n" +
                                    "- `pharma_abc`\n" +
                                    "- `pharma_centrale`\n" +
                                    "- `pharma_xyz`\n\n" +
                                    "**Non requis pour :**\n" +
                                    "- `/api/v1/public/*` (endpoints publics)\n" +
                                    "- `/api/v1/admin/*` (Super Admin)\n" +
                                    "- `/api/v1/clients/*` (Mobile clients)"
                                ));
    }
}
