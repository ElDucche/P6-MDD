package com.elducche.mdd.repository;

import com.elducche.mdd.entity.Subscription;
import com.elducche.mdd.entity.SubscriptionId;
import com.elducche.mdd.entity.User;
import com.elducche.mdd.entity.Theme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Subscription
 * 
 * Gère les abonnements des utilisateurs aux thèmes avec
 * des méthodes optimisées pour les requêtes fréquentes
 */
@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, SubscriptionId> {
    // Trouve les abonnements d'un utilisateur (pour les tests)
    List<Subscription> findByUser(User user);

    // Trouve les abonnements d'un thème (pour les tests)
    List<Subscription> findByTheme(Theme theme);
    
    /**
     * Trouve tous les abonnements d'un utilisateur avec les thèmes
     * @param userId L'ID de l'utilisateur
     * @return Liste des abonnements avec les thèmes
     */
    @Query("SELECT s FROM Subscription s JOIN FETCH s.theme WHERE s.id.userId = :userId")
    List<Subscription> findByUserIdWithTheme(@Param("userId") Long userId);
    
    /**
     * Trouve tous les abonnés d'un thème
     * @param themeId L'ID du thème
     * @return Liste des abonnements avec les utilisateurs
     */
    @Query("SELECT s FROM Subscription s JOIN FETCH s.user WHERE s.id.themeId = :themeId")
    List<Subscription> findByThemeIdWithUser(@Param("themeId") Long themeId);
    
    /**
     * Trouve les IDs des thèmes auxquels un utilisateur est abonné
     * @param userId L'ID de l'utilisateur
     * @return Liste des IDs de thèmes
     */
    @Query("SELECT s.id.themeId FROM Subscription s WHERE s.id.userId = :userId")
    List<Long> findThemeIdsByUserId(@Param("userId") Long userId);
    
    /**
     * Vérifie si un utilisateur est abonné à un thème
     * @param userId L'ID de l'utilisateur
     * @param themeId L'ID du thème
     * @return true si l'utilisateur est abonné
     */
    @Query("SELECT COUNT(s) > 0 FROM Subscription s WHERE s.id.userId = :userId AND s.id.themeId = :themeId")
    boolean existsByUserIdAndThemeId(@Param("userId") Long userId, @Param("themeId") Long themeId);
    
    /**
     * Trouve un abonnement spécifique
     * @param userId L'ID de l'utilisateur
     * @param themeId L'ID du thème
     * @return Optional contenant l'abonnement
     */
    @Query("SELECT s FROM Subscription s WHERE s.id.userId = :userId AND s.id.themeId = :themeId")
    Optional<Subscription> findByUserIdAndThemeId(@Param("userId") Long userId, @Param("themeId") Long themeId);
    
    /**
     * Supprime un abonnement spécifique
     * @param userId L'ID de l'utilisateur
     * @param themeId L'ID du thème
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Subscription s WHERE s.id.userId = :userId AND s.id.themeId = :themeId")
    void deleteByUserIdAndThemeId(@Param("userId") Long userId, @Param("themeId") Long themeId);
    
    /**
     * Compte le nombre d'abonnés d'un thème
     * @param themeId L'ID du thème
     * @return Nombre d'abonnés
     */
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.id.themeId = :themeId")
    long countByThemeId(@Param("themeId") Long themeId);
    
    /**
     * Récupère tous les abonnements d'un utilisateur (simple)
     */
    @Query("SELECT s FROM Subscription s WHERE s.id.userId = :userId")
    List<Subscription> findByUserId(@Param("userId") Long userId);
    
    /**
     * Récupère tous les abonnements d'un thème (simple)
     */
    @Query("SELECT s FROM Subscription s WHERE s.id.themeId = :themeId")
    List<Subscription> findByThemeId(@Param("themeId") Long themeId);
    
    /**
     * Compte le nombre d'abonnements d'un utilisateur
     * @param userId L'ID de l'utilisateur
     * @return Nombre d'abonnements
     */
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.id.userId = :userId")
    long countByUserId(@Param("userId") Long userId);
}
