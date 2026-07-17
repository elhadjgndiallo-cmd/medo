package com.medo.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration CORS (Cross-Origin Resource Sharing)
 * 
 * Permet aux applications frontend (React, Angular, Vue) et mobile
 * d'accéder à l'API depuis des origines différentes.
 * 
 * Configuration :
 * - Origins autorisées : localhost + domaines production
 * - Méthodes HTTP : GET, POST, PUT, DELETE, PATCH, OPTIONS
 * - Headers autorisés : Authorization, X-Tenant, Content-Type
 * - Credentials : Activé pour cookies/sessions
 * 
 * @author MEDO Team
 * @version 1.0.0
 */
@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origins:http://localhost:3000,http://localhost:4200,http://localhost:5173}")
    private String[] allowedOrigins;

    @Value("${cors.max-age:3600}")
    private Long maxAge;

    /**
     * Configuration CORS pour l'application
     * 
     * @return Source de configuration CORS
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Origins autorisées
        // Par défaut : localhost (React 3000, Angular 4200, Vite 5173)
        // Production : Ajouter domaines dans application.yml
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));

        // Méthodes HTTP autorisées
        configuration.setAllowedMethods(Arrays.asList(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "PATCH",
                "OPTIONS"
        ));

        // Headers autorisés
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",      // JWT Bearer token
                "X-Tenant",          // Identifiant tenant multi-tenant
                "Content-Type",      // Type de contenu (application/json)
                "Accept",            // Type de réponse accepté
                "X-Requested-With",  // Identification requêtes AJAX
                "Cache-Control"      // Contrôle du cache
        ));

        // Headers exposés au client
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "X-Tenant",
                "X-Total-Count",     // Pagination : nombre total d'éléments
                "X-Page-Number",     // Pagination : numéro de page
                "X-Page-Size"        // Pagination : taille de page
        ));

        // Autoriser credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);

        // Durée de cache de la config CORS (1 heure)
        configuration.setMaxAge(maxAge);

        // Appliquer la configuration à tous les endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);

        return source;
    }

    /**
     * Configuration CORS permissive pour développement local
     * À utiliser uniquement en dev, PAS en production !
     * 
     * @return Configuration CORS permissive
     */
    public CorsConfigurationSource corsConfigurationSourceDev() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*"); // Toutes les origins
        configuration.addAllowedMethod("*");        // Toutes les méthodes
        configuration.addAllowedHeader("*");        // Tous les headers
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
