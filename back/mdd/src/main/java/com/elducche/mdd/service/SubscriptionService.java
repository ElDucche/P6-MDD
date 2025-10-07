package com.elducche.mdd.service;

import com.elducche.mdd.dto.SubscriptionResponseDTO;
import com.elducche.mdd.entity.Subscription;
import com.elducche.mdd.entity.SubscriptionId;
import com.elducche.mdd.entity.Theme;

import java.util.List;
import java.util.Optional;

/**
 * Service de gestion des abonnements
 * 
 * Fournit les opérations de gestion des abonnements aux thèmes
 * avec contrôles de cohérence et gestion des relations
 */
public interface SubscriptionService {
    // Méthodes CRUD minimales pour les tests unitaires
    Optional<Subscription> findById(SubscriptionId id);
    Subscription save(Subscription subscription);
    void deleteById(SubscriptionId id);
    
    /**
     * Récupère tous les abonnements d'un utilisateur avec les thèmes
     */
    List<Subscription> getUserSubscriptions(Long userId);
    
    /**
     * Récupère tous les abonnements d'un thème avec les utilisateurs
     */
    List<Subscription> getThemeSubscriptions(Long themeId);
    
    /**
     * Vérifie si un utilisateur est abonné à un thème
     */
    boolean isUserSubscribedToTheme(Long userId, Long themeId);
    
    /**
     * Compte le nombre d'abonnements d'un utilisateur
     */
    long countUserSubscriptions(Long userId);
    
    /**
     * Compte le nombre d'abonnés d'un thème
     */
    long countThemeSubscribers(Long themeId);
    
    /**
     * Abonne un utilisateur à un thème
     */
    Optional<Subscription> subscribeUserToTheme(Long userId, Long themeId);
    
    /**
     * Alias pour subscribeUserToTheme (compatibilité avec les contrôleurs)
     */
    Subscription subscribeToTheme(Long userId, Long themeId);
    
    /**
     * Désabonne un utilisateur d'un thème
     */
    boolean unsubscribeUserFromTheme(Long userId, Long themeId);
    
    /**
     * Alias pour unsubscribeUserFromTheme (compatibilité avec les contrôleurs)
     */
    boolean unsubscribeFromTheme(Long userId, Long subscriptionId);
    
    /**
     * Bascule l'état d'abonnement d'un utilisateur à un thème
     */
    boolean toggleSubscription(Long userId, Long themeId);
    
    /**
     * Supprime tous les abonnements d'un utilisateur
     */
    void deleteAllUserSubscriptions(Long userId);
    
    /**
     * Supprime tous les abonnements d'un thème
     */
    void deleteAllThemeSubscriptions(Long themeId);
    
    /**
     * Récupère les thèmes auxquels un utilisateur n'est PAS abonné
     */
    List<Theme> getAvailableThemesForUser(Long userId);
    
    /**
     * Récupère les thèmes auxquels un utilisateur est abonné
     */
    List<Theme> getSubscribedThemesForUser(Long userId);
    
    /**
     * Récupère tous les abonnements d'un utilisateur en DTO
     */
    List<SubscriptionResponseDTO> getUserSubscriptionsDTO(Long userId);
    
    /**
     * Abonne un utilisateur à un thème et retourne le DTO
     */
    SubscriptionResponseDTO subscribeToThemeDTO(Long userId, Long themeId);
}
