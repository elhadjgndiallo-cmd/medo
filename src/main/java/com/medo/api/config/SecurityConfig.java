package com.medo.api.config;

import com.medo.api.security.JwtAuthenticationFilter;
import com.medo.api.tenant.TenantFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Configuration Spring Security
 * 
 * Sécurité de l'API MEDO :
 * - Authentification JWT (stateless)
 * - RBAC avec permissions granulaires
 * - Multi-tenant via TenantFilter
 * - CORS configuré
 * - Endpoints publics autorisés
 * 
 * Ordre des filtres :
 * 1. TenantFilter (extraction X-Tenant header)
 * 2. JwtAuthenticationFilter (validation JWT)
 * 3. Spring Security filters
 * 
 * @author MEDO Team
 * @version 1.0.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private TenantFilter tenantFilter;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    /**
     * URLs publiques accessibles sans authentification
     */
    private static final String[] PUBLIC_URLS = {
        // Authentification
        "/api/v1/auth/login",
        "/api/v1/auth/mobile/login",
        "/api/v1/auth/refresh",
        "/api/v1/auth/inscription",
        
        // API Mobile publique (recherche médicaments)
        "/api/v1/public/**",
        
        // Documentation
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/v3/api-docs/**",
        "/api-docs/**",
        
        // Monitoring
        "/actuator/health",
        "/actuator/info",
        
        // Erreurs
        "/error"
    };

    /**
     * Encodeur de mots de passe BCrypt
     * Force : 12 rounds (équilibre sécurité/performance)
     * 
     * @return BCryptPasswordEncoder configuré
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Configuration de la chaîne de filtres de sécurité
     * 
     * @param http HttpSecurity à configurer
     * @return SecurityFilterChain configurée
     * @throws Exception En cas d'erreur de configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Désactiver CSRF (API stateless avec JWT)
            .csrf(AbstractHttpConfigurer::disable)
            
            // Désactiver form login (pas de formulaire HTML)
            .formLogin(AbstractHttpConfigurer::disable)
            
            // Désactiver HTTP Basic Auth
            .httpBasic(AbstractHttpConfigurer::disable)
            
            // Session stateless (JWT uniquement, pas de session serveur)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Configuration CORS (injection depuis CorsConfig)
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            
            // Autorisation des requêtes
            .authorizeHttpRequests(auth -> auth
                // URLs publiques (sans token)
                .requestMatchers(PUBLIC_URLS).permitAll()
                
                // OPTIONS requests (CORS preflight)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // Super Admin uniquement
                .requestMatchers("/api/v1/admin/**")
                    .hasAuthority("TYPE_SUPER_ADMIN")
                
                // Admin Pharmacie ou Super Admin
                .requestMatchers("/api/v1/pharmacie/config/**")
                    .hasAnyAuthority("TYPE_ADMIN_PHARMACIE", "TYPE_SUPER_ADMIN")
                
                // Pharmacie : Gérants et Admins
                .requestMatchers("/api/v1/pos/**", "/api/v1/inventaire/**", 
                                "/api/v1/achats/**", "/api/v1/rapports/**")
                    .hasAnyAuthority("TYPE_ADMIN_PHARMACIE", "TYPE_GERANT", 
                                   "TYPE_VENDEUR", "TYPE_SUPER_ADMIN")
                
                // Mobile clients authentifiés
                .requestMatchers("/api/v1/clients/**")
                    .hasAuthority("TYPE_CLIENT_MOBILE")
                
                // Toutes les autres requêtes nécessitent authentification
                .anyRequest().authenticated()
            )
            
            // Ajout des filtres custom
            // 1. TenantFilter : Extrait header X-Tenant et configure le context
            .addFilterBefore(tenantFilter, UsernamePasswordAuthenticationFilter.class)
            
            // 2. JwtAuthenticationFilter : Valide JWT et configure authentication
            .addFilterAfter(jwtAuthenticationFilter, TenantFilter.class);
        
        return http.build();
    }
}
