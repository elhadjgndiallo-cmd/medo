package com.medo.api.security;

import com.medo.api.exception.GlobalExceptionHandler.PermissionDeniedException;
import com.medo.api.security.annotations.RequiresPermission;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Aspect AOP pour intercepter et vérifier les permissions
 * via l'annotation @RequiresPermission
 */
@Aspect
@Component
public class PermissionAspect {

    private static final Logger log = LoggerFactory.getLogger(PermissionAspect.class);

    @Autowired
    private RbacPermissionEvaluator permissionEvaluator;

    @Before("@annotation(com.medo.api.security.annotations.RequiresPermission)")
    public void checkPermission(JoinPoint joinPoint) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new PermissionDeniedException("Non authentifié", "");
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // Récupérer toutes les annotations @RequiresPermission
        RequiresPermission[] permissions = method.getAnnotationsByType(RequiresPermission.class);
        
        if (permissions.length == 0) {
            return;
        }

        String userId = extractUserId(authentication);
        
        boolean requireAll = permissions[0].requireAll();
        
        if (requireAll) {
            // Toutes les permissions doivent être présentes
            for (RequiresPermission permission : permissions) {
                String permissionCode = permission.module() + "." + permission.action();
                
                if (!permissionEvaluator.checkPermission(userId, permissionCode)) {
                    log.warn("Permission refusée pour {} : {} (requireAll=true)", 
                            userId, permissionCode);
                    throw new PermissionDeniedException(
                        permission.module(), 
                        permission.action()
                    );
                }
            }
        } else {
            // Au moins une permission doit être présente
            boolean hasAnyPermission = false;
            
            for (RequiresPermission permission : permissions) {
                String permissionCode = permission.module() + "." + permission.action();
                
                if (permissionEvaluator.checkPermission(userId, permissionCode)) {
                    hasAnyPermission = true;
                    break;
                }
            }
            
            if (!hasAnyPermission) {
                log.warn("Permission refusée pour {} : aucune des permissions requises (requireAll=false)", 
                        userId);
                throw new PermissionDeniedException(
                    permissions[0].module(), 
                    permissions[0].action()
                );
            }
        }
        
        log.debug("Vérification de permission OK pour {} sur {}", 
                 userId, method.getName());
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
}
