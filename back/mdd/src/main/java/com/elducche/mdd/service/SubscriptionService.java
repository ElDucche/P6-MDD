package com.elducche.mdd.service;

import com.elducche.mdd.entity.Subscription;
import com.elducche.mdd.entity.SubscriptionId;
import com.elducche.mdd.entity.Theme;
import com.elducche.mdd.entity.User;
import com.elducche.mdd.repository.SubscriptionRepository;
import com.elducche.mdd.repository.ThemeRepository;
import com.elducche.mdd.repository.UserRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

/**
 * Service de gestion des abonnements
 * 
 * Fournit les opérations de gestion des abonnements aux thèmes
 * avec contrôles de cohérence et gestion des relations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {
    // Méthodes CRUD minimales pour les tests unitaires
    public Optional<Subscription> findById(SubscriptionId id) {
        return subscriptionRepository.findById(id);
    }

    public Subscription save(Subscription subscription) {
        return subscriptionRepository.save(subscription);
    }

    public void deleteById(SubscriptionId id) {
        subscriptionRepository.deleteById(id);
    }
    
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final ThemeRepository themeRepository;
    
    /**
     * Récupère tous les abonnements d'un utilisateur avec les thèmes
     */
    public List<Subscription> getUserSubscriptions(Long userId) {
        return subscriptionRepository.findByUserIdWithTheme(userId);
    }
    
    /**
     * Récupère tous les abonnements d'un thème avec les utilisateurs
     */
    public List<Subscription> getThemeSubscriptions(Long themeId) {
        return subscriptionRepository.findByThemeIdWithUser(themeId);
    }
    
    /**
     * Vérifie si un utilisateur est abonné à un thème
     */
    public boolean isUserSubscribedToTheme(Long userId, Long themeId) {
        SubscriptionId id = new SubscriptionId(userId, themeId);
        return subscriptionRepository.existsById(id);
    }
    
    /**
     * Compte le nombre d'abonnements d'un utilisateur
     */
    public long countUserSubscriptions(Long userId) {
        return subscriptionRepository.countByUserId(userId);
    }
    
    /**
     * Compte le nombre d'abonnés d'un thème
     */
    public long countThemeSubscribers(Long themeId) {
        return subscriptionRepository.countByThemeId(themeId);
    }
    
    /**
     * Abonne un utilisateur à un thème
     */
    public Optional<Subscription> subscribeUserToTheme(Long userId, Long themeId) {
        try {
            // Vérification de l'existence de l'utilisateur
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                log.warn("Tentative d'abonnement avec utilisateur inexistant: {}", userId);
                return Optional.empty();
            }
            
            // Vérification de l'existence du thème
            Optional<Theme> themeOpt = themeRepository.findById(themeId);
            if (themeOpt.isEmpty()) {
                log.warn("Tentative d'abonnement à un thème inexistant: {}", themeId);
                return Optional.empty();
            }
            
            // Vérification que l'abonnement n'existe pas déjà
            SubscriptionId subscriptionId = new SubscriptionId(userId, themeId);
            if (subscriptionRepository.existsById(subscriptionId)) {
                log.info("L'utilisateur {} est déjà abonné au thème {}", userId, themeId);
                throw new IllegalStateException("L'utilisateur est déjà abonné à ce thème");
            }
            
            // Création de l'abonnement
            Subscription subscription = new Subscription();
            subscription.setId(subscriptionId);
            subscription.setUser(userOpt.get());
            subscription.setTheme(themeOpt.get());
            
            Subscription savedSubscription = subscriptionRepository.save(subscription);
            log.info("Utilisateur {} abonné au thème {}", userId, themeId);
            
            return Optional.of(savedSubscription);
            
        } catch (IllegalStateException e) {
            // Re-throw les exceptions métier pour que le contrôleur puisse les gérer
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de l'abonnement de l'utilisateur {} au thème {}: {}", 
                     userId, themeId, e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Alias pour subscribeUserToTheme (compatibilité avec les contrôleurs)
     */
    public Subscription subscribeToTheme(Long userId, Long themeId) {
        Optional<Subscription> result = subscribeUserToTheme(userId, themeId);
        return result.orElse(null);
    }
    
    /**
     * Désabonne un utilisateur d'un thème
     */
    public boolean unsubscribeUserFromTheme(Long userId, Long themeId) {
        try {
            SubscriptionId subscriptionId = new SubscriptionId(userId, themeId);
            
            if (!subscriptionRepository.existsById(subscriptionId)) {
                log.info("Tentative de désabonnement inexistant: utilisateur {} du thème {}", userId, themeId);
                return false;
            }
            
            subscriptionRepository.deleteById(subscriptionId);
            log.info("Utilisateur {} désabonné du thème {}", userId, themeId);
            
            return true;
            
        } catch (Exception e) {
            log.error("Erreur lors du désabonnement de l'utilisateur {} du thème {}: {}", 
                     userId, themeId, e.getMessage());
            return false;
        }
    }
    
    /**
     * Alias pour unsubscribeUserFromTheme (compatibilité avec les contrôleurs)
     * Note: le contrôleur attend (userId, subscriptionId) mais on utilise (userId, themeId)
     */
    public boolean unsubscribeFromTheme(Long userId, Long subscriptionId) {
        // Dans notre cas, on assume que subscriptionId peut être l'ID du thème
        // ou l'ID de l'abonnement. Il faudrait idéalement adapter selon le contexte.
        return unsubscribeUserFromTheme(userId, subscriptionId);
    }
    
    /**
     * Bascule l'état d'abonnement d'un utilisateur à un thème
     * (abonne si pas abonné, désabonne si abonné)
     */
    public boolean toggleSubscription(Long userId, Long themeId) {
        if (isUserSubscribedToTheme(userId, themeId)) {
            return unsubscribeUserFromTheme(userId, themeId);
        } else {
            return subscribeUserToTheme(userId, themeId).isPresent();
        }
    }
    
    /**
     * Supprime tous les abonnements d'un utilisateur
     */
    public void deleteAllUserSubscriptions(Long userId) {
        try {
            List<Subscription> subscriptions = subscriptionRepository.findByUserId(userId);
            if (!subscriptions.isEmpty()) {
                subscriptionRepository.deleteAll(subscriptions);
                log.info("{} abonnement(s) supprimé(s) pour l'utilisateur {}", subscriptions.size(), userId);
            }
        } catch (Exception e) {
            log.error("Erreur lors de la suppression des abonnements de l'utilisateur {}: {}", 
                     userId, e.getMessage());
        }
    }
    
    /**
     * Supprime tous les abonnements d'un thème
     */
    public void deleteAllThemeSubscriptions(Long themeId) {
        try {
            List<Subscription> subscriptions = subscriptionRepository.findByThemeId(themeId);
            if (!subscriptions.isEmpty()) {
                subscriptionRepository.deleteAll(subscriptions);
                log.info("{} abonnement(s) supprimé(s) pour le thème {}", subscriptions.size(), themeId);
            }
        } catch (Exception e) {
            log.error("Erreur lors de la suppression des abonnements du thème {}: {}", 
                     themeId, e.getMessage());
        }
    }
    
    /**
     * Récupère les thèmes auxquels un utilisateur n'est PAS abonné
     */
    public List<Theme> getAvailableThemesForUser(Long userId) {
        return themeRepository.findNonSubscribedThemes(userId);
    }
    
    /**
     * Récupère les thèmes auxquels un utilisateur est abonné
     */
    public List<Theme> getSubscribedThemesForUser(Long userId) {
        return themeRepository.findSubscribedThemes(userId);
    }
}
