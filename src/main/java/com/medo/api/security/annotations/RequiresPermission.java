package com.medo.api.security.annotations;

import java.lang.annotation.*;

/**
 * Annotation pour vérifier les permissions au niveau des méthodes
 * 
 * Utilisation:
 * @RequiresPermission(module = "INVENTAIRE", action = "ECRIRE")
 * public void creerProduit(...) { ... }
 * 
 * ou pour plusieurs permissions:
 * @RequiresPermission(module = "POS", action = "LIRE", requireAll = false)
 * @RequiresPermission(module = "POS", action = "ECRIRE", requireAll = false)
 * public void effectuerVente(...) { ... }
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresPermission {
    
    /**
     * Module de la permission (ex: "INVENTAIRE", "POS", "ACHATS")
     */
    String module();
    
    /**
     * Action de la permission (ex: "LIRE", "ECRIRE", "SUPPRIMER")
     */
    String action();
    
    /**
     * Si true, toutes les permissions annotées doivent être présentes
     * Si false, au moins une permission suffit
     */
    boolean requireAll() default true;
    
    /**
     * Message d'erreur personnalisé si la permission est refusée
     */
    String errorMessage() default "Accès refusé : permission insuffisante";
}
