package com.elducche.mdd.controller;

import com.elducche.mdd.dto.SubscriptionRequest;
import com.elducche.mdd.entity.Subscription;
import com.elducche.mdd.service.SubscriptionService;
import com.elducche.mdd.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Contrôleur pour la gestion des abonnements aux thèmes
 */
@Slf4j
@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final AuthUtil authUtil;

    /**
     * Récupère tous les abonnements de l'utilisateur connecté
     * @return Liste des abonnements de l'utilisateur
     */
    @GetMapping
    public ResponseEntity<List<Subscription>> getUserSubscriptions() {
        log.debug("Récupération des abonnements de l'utilisateur connecté");
        
        return authUtil.executeWithAuth(userId -> {
            List<Subscription> subscriptions = subscriptionService.getUserSubscriptions(userId);
            return ResponseEntity.ok(subscriptions);
        });
    }

    /**
     * Crée un abonnement à un thème
     * @param request Données de l'abonnement
     * @return L'abonnement créé ou erreur
     */
    @PostMapping
    public ResponseEntity<?> subscribeToTheme(@Valid @RequestBody SubscriptionRequest request) {
        log.debug("Création d'un abonnement au thème ID : {}", request.getThemeId());
        
        return authUtil.executeWithAuthHandleErrors(userId -> {
            try {
                Subscription subscription = subscriptionService.subscribeToTheme(userId, request.getThemeId());
                if (subscription != null) {
                    return ResponseEntity.status(HttpStatus.CREATED).body(subscription);
                } else {
                    return ResponseEntity.badRequest().body("Impossible de créer l'abonnement");
                }
            } catch (IllegalStateException e) {
                return ResponseEntity.badRequest().body("Impossible de créer l'abonnement");
            }
        });
    }

    /**
     * Supprime un abonnement
     * @param id ID de l'abonnement
     * @return Confirmation de suppression
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> unsubscribeFromTheme(@PathVariable Long id) {
        log.debug("Suppression de l'abonnement ID : {}", id);
        
        return authUtil.executeWithAuthHandleErrors(userId -> {
            boolean deleted = subscriptionService.unsubscribeFromTheme(userId, id);
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        });
    }
}
