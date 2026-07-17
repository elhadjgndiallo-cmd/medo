package com.medo.api.security;

import com.medo.api.auth.entity.Permission;
import com.medo.api.auth.entity.Role;
import com.medo.api.auth.repository.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RbacPermissionEvaluator implements PermissionEvaluator {

    private static final Logger log = LoggerFactory.getLogger(RbacPermissionEvaluator.class);

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String userId = extractUserId(authentication);
        if (userId == null) {
            return false;
        }

        return checkPermission(userId, String.valueOf(permission));
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, 
                                String targetType, Object permission) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String userId = extractUserId(authentication);
        if (userId == null) {
            return false;
        }

        return checkPermission(userId, String.valueOf(permission));
    }

    /**
     * Vérifie si l'utilisateur a une permission spécifique
     * Format attendu: "MODULE.ACTION" (ex: "INVENTAIRE.ECRIRE", "POS.LIRE")
     */
    public boolean checkPermission(String userId, String permissionCode) {
        try {
            UUID userUuid = UUID.fromString(userId);
            
            var utilisateur = utilisateurRepository.findByIdWithRolesAndPermissions(userUuid);
            if (utilisateur.isEmpty()) {
                log.warn("Utilisateur {} non trouvé pour vérification permission", userId);
                return false;
            }

            Set<String> userPermissions = utilisateur.get().getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(this::formatPermission)
                .collect(Collectors.toSet());

            boolean hasPermission = userPermissions.contains(permissionCode);
            
            log.debug("Vérification permission {} pour utilisateur {} : {}", 
                     permissionCode, userId, hasPermission);
            
            return hasPermission;

        } catch (Exception e) {
            log.error("Erreur lors de la vérification de permission : {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Vérifie si l'utilisateur a l'une des permissions spécifiées
     */
    public boolean hasAnyPermission(String userId, String... permissionCodes) {
        for (String permissionCode : permissionCodes) {
            if (checkPermission(userId, permissionCode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vérifie si l'utilisateur a toutes les permissions spécifiées
     */
    public boolean hasAllPermissions(String userId, String... permissionCodes) {
        for (String permissionCode : permissionCodes) {
            if (!checkPermission(userId, permissionCode)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Vérifie si l'utilisateur a un rôle spécifique
     */
    public boolean hasRole(String userId, String roleName) {
        try {
            UUID userUuid = UUID.fromString(userId);
            
            var utilisateur = utilisateurRepository.findByIdWithRolesAndPermissions(userUuid);
            if (utilisateur.isEmpty()) {
                return false;
            }

            return utilisateur.get().getRoles().stream()
                .anyMatch(role -> role.getNom().equals(roleName));

        } catch (Exception e) {
            log.error("Erreur lors de la vérification de rôle : {}", e.getMessage());
            return false;
        }
    }

    private String extractUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof MedoUserPrincipal) {
            return ((MedoUserPrincipal) principal).getUserId();
        }
        
        if (principal instanceof String) {
            return (String) principal;
        }
        
        return null;
    }

    private String formatPermission(Permission permission) {
        return permission.getModule() + "." + permission.getAction();
    }
}
