package com.elducche.mdd.util;

import com.elducche.mdd.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Utilitaire pour centraliser la logique d'authentification dans les contrôleurs
 * 
 * Simplifie la gestion des vérifications d'authentification et des réponses d'erreur
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthUtil {

    private final SecurityUtil securityUtil;

    /**
     * Vérifie l'authentification et exécute une action si l'utilisateur est connecté
     * 
     * @param action Action à exécuter avec l'userId de l'utilisateur connecté
     * @param <T> Type de retour de l'action
     * @return ResponseEntity avec le résultat de l'action ou erreur 401
     */
    public <T> ResponseEntity<T> executeWithAuth(Function<Long, ResponseEntity<T>> action) {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            log.warn("Tentative d'accès à une ressource protégée sans authentification");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            return action.apply(userId);
        } catch (IllegalArgumentException e) {
            log.warn("Erreur de validation: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erreur lors de l'exécution de l'action authentifiée: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Vérifie l'authentification et exécute une action si l'utilisateur est connecté
     * Version avec gestion des exceptions personnalisées
     * 
     * @param action Action à exécuter avec l'userId de l'utilisateur connecté
     * @param <T> Type de retour de l'action
     * @return ResponseEntity avec le résultat de l'action ou erreur approprié
     */
    public <T> ResponseEntity<?> executeWithAuthHandleErrors(Function<Long, ResponseEntity<T>> action) {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            log.warn("Tentative d'accès à une ressource protégée sans authentification");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            return action.apply(userId);
        } catch (IllegalArgumentException e) {
            log.warn("Erreur de validation: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Erreur lors de l'exécution de l'action authentifiée: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body("Erreur interne du serveur");
        }
    }

    /**
     * Récupère l'userId de l'utilisateur connecté ou retourne null
     * 
     * @return L'ID de l'utilisateur connecté ou null
     */
    public Long getCurrentUserId() {
        return securityUtil.getCurrentUserId();
    }

    /**
     * Vérifie si l'utilisateur actuel est authentifié
     * 
     * @return true si l'utilisateur est authentifié, false sinon
     */
    public boolean isAuthenticated() {
        return securityUtil.getCurrentUserId() != null;
    }
}
