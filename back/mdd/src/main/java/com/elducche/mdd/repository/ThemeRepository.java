package com.elducche.mdd.repository;

import com.elducche.mdd.entity.Theme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Theme
 * 
 * Fournit les opérations CRUD et des méthodes de recherche
 * pour la gestion des thèmes
 */
@Repository
public interface ThemeRepository extends JpaRepository<Theme, Long> {
    
    /**
     * Trouve les thèmes par titre (recherche partielle)
     * @param title Le titre à rechercher
     * @return Liste des thèmes correspondants
     */
    List<Theme> findByTitleContainingIgnoreCase(String title);
    
    /**
     * Vérifie si un thème existe par son titre exact
     * @param title Le titre du thème
     * @return true si le thème existe
     */
    boolean existsByTitle(String title);
    
    /**
     * Trouve les thèmes auxquels un utilisateur est abonné
     * @param userId L'ID de l'utilisateur
     * @return Liste des thèmes
     */
    @Query("SELECT t FROM Theme t JOIN t.subscriptions s WHERE s.user.id = :userId")
    List<Theme> findThemesByUserId(@Param("userId") Long userId);
    
    /**
     * Trouve les thèmes les plus populaires (avec le plus d'abonnés)
     * @param limit Nombre maximum de thèmes à retourner
     * @return Liste des thèmes triés par popularité
     */
    @Query("SELECT t FROM Theme t LEFT JOIN t.subscriptions s GROUP BY t ORDER BY COUNT(s) DESC")
    List<Theme> findMostPopularThemes(@Param("limit") int limit);
    
    /**
     * Trouve tous les thèmes triés par date de création (plus récents en premier)
     * @return Liste des thèmes triés
     */
    List<Theme> findAllByOrderByCreatedAtDesc();
    
    /**
     * Trouve un thème par titre exact
     */
    Optional<Theme> findByTitle(String title);
    
    /**
     * Trouve les thèmes auxquels un utilisateur est abonné
     */
    @Query("SELECT t FROM Theme t JOIN Subscription s ON t.id = s.theme.id WHERE s.user.id = :userId")
    List<Theme> findSubscribedThemes(@Param("userId") Long userId);
    
    /**
     * Trouve les thèmes auxquels un utilisateur n'est PAS abonné
     */
    @Query("SELECT t FROM Theme t WHERE t.id NOT IN " +
           "(SELECT s.theme.id FROM Subscription s WHERE s.user.id = :userId)")
    List<Theme> findNonSubscribedThemes(@Param("userId") Long userId);
    
    /**
     * Compte le nombre de posts dans un thème
     */
    @Query("SELECT COUNT(p) FROM Post p WHERE p.theme.id = :themeId")
    long countPostsByThemeId(@Param("themeId") Long themeId);
    
    /**
     * Compte le nombre d'abonnés d'un thème
     */
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.theme.id = :themeId")
    long countSubscribersByThemeId(@Param("themeId") Long themeId);
}
