package com.elducche.mdd.repository;

import com.elducche.mdd.entity.Post;
import com.elducche.mdd.entity.Theme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Post
 * 
 * Fournit les opérations CRUD et des méthodes optimisées
 * pour la récupération des posts avec leurs relations
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    // Trouve les posts d'un thème (pour les tests)
    List<Post> findByTheme(Theme theme);
    
    /**
     * Trouve tous les posts triés par date de création (plus récents en premier)
     * @return Liste des posts avec leurs relations
     */
    @Query("SELECT p FROM Post p JOIN FETCH p.author JOIN FETCH p.theme ORDER BY p.createdAt DESC")
    List<Post> findAllWithAuthorAndTheme();
    
    /**
     * Trouve un post par ID avec ses relations
     * @param id L'ID du post
     * @return Optional contenant le post avec ses relations
     */
    @Query("SELECT p FROM Post p JOIN FETCH p.author JOIN FETCH p.theme WHERE p.id = :id")
    Optional<Post> findByIdWithAuthorAndTheme(@Param("id") Long id);
    
    /**
     * Trouve les posts d'un thème spécifique
     * @param themeId L'ID du thème
     * @return Liste des posts du thème
     */
    @Query("SELECT p FROM Post p JOIN FETCH p.author JOIN FETCH p.theme WHERE p.theme.id = :themeId ORDER BY p.createdAt DESC")
    List<Post> findByThemeIdWithAuthorAndTheme(@Param("themeId") Long themeId);
    
    /**
     * Trouve les posts d'un auteur spécifique
     * @param authorId L'ID de l'auteur
     * @return Liste des posts de l'auteur
     */
    @Query("SELECT p FROM Post p JOIN FETCH p.author JOIN FETCH p.theme WHERE p.author.id = :authorId ORDER BY p.createdAt DESC")
    List<Post> findByAuthorIdWithAuthorAndTheme(@Param("authorId") Long authorId);
    
    /**
     * Trouve les posts des thèmes auxquels un utilisateur est abonné (feed personnalisé)
     * @param userId L'ID de l'utilisateur
     * @return Liste des posts du feed personnalisé
     */
    @Query("""
        SELECT p FROM Post p 
        JOIN FETCH p.author 
        JOIN FETCH p.theme t
        WHERE t.id IN (
            SELECT s.theme.id FROM Subscription s WHERE s.user.id = :userId
        )
        ORDER BY p.createdAt DESC
        """)
    List<Post> findPostsFromSubscribedThemes(@Param("userId") Long userId);
    
    /**
     * Trouve les posts par liste d'IDs de thèmes
     * @param themeIds Liste des IDs de thèmes
     * @return Liste des posts
     */
    @Query("SELECT p FROM Post p JOIN FETCH p.author JOIN FETCH p.theme WHERE p.theme.id IN :themeIds ORDER BY p.createdAt DESC")
    List<Post> findByThemeIdInWithAuthorAndTheme(@Param("themeIds") List<Long> themeIds);
    
    /**
     * Recherche de posts par titre (recherche partielle)
     * @param title Le titre à rechercher
     * @return Liste des posts correspondants
     */
    @Query("SELECT p FROM Post p JOIN FETCH p.author JOIN FETCH p.theme WHERE p.title LIKE %:title% ORDER BY p.createdAt DESC")
    List<Post> findByTitleContainingIgnoreCase(@Param("title") String title);
}
