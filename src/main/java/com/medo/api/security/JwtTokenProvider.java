package com.medo.api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${medo.jwt.secret}")
    private String jwtSecret;

    @Value("${medo.jwt.access-token-expiration:1800000}")
    private long accessExpiration;

    @Value("${medo.jwt.refresh-token-expiration:604800000}")
    private long refreshExpiration;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(String userId, String tenantId, String typeUser, List<String> roles) {
        Date now = new Date();
        return Jwts.builder()
            .subject(userId)
            .claims(Map.of("tenantId", tenantId, "typeUser", typeUser,
                           "roles", roles, "tokenType", "ACCESS"))
            .issuedAt(now)
            .expiration(new Date(now.getTime() + accessExpiration))
            .signWith(secretKey).compact();
    }

    public String generateRefreshToken(String userId, String tenantId) {
        Date now = new Date();
        return Jwts.builder()
            .subject(userId)
            .id(UUID.randomUUID().toString())
            .claims(Map.of("tenantId", tenantId, "tokenType", "REFRESH"))
            .issuedAt(now)
            .expiration(new Date(now.getTime() + refreshExpiration))
            .signWith(secretKey).compact();
    }

    public boolean validateToken(String token) {
        try { parseClaims(token); return true; }
        catch (JwtException e) { log.warn("Token invalide : {}", e.getMessage()); }
        return false;
    }

    public String getUserIdFromToken(String t)   { return parseClaims(t).getSubject(); }
    public String getTenantIdFromToken(String t) { return (String) parseClaims(t).get("tenantId"); }
    public String getTypeUserFromToken(String t) { return (String) parseClaims(t).get("typeUser"); }
    public String getJtiFromToken(String t)      { return parseClaims(t).getId(); }
    public Date   getExpirationFromToken(String t) { return parseClaims(t).getExpiration(); }

    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String t) {
        return (List<String>) parseClaims(t).get("roles");
    }

    private Claims parseClaims(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
            .parseSignedClaims(token).getPayload();
    }
}
