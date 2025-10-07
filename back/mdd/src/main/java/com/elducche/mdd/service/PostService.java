package com.elducche.mdd.service;

import com.elducche.mdd.dto.PostCreateRequest;
import com.elducche.mdd.entity.Post;

import java.util.List;
import java.util.Optional;

/**
 * Service de gestion des posts
 * 
 * Fournit les opérations CRUD et métier sur les posts avec
 * gestion optimisée des relations et du feed personnalisé
 */
public interface PostService {
    // Méthodes CRUD minimales pour les tests unitaires
    Optional<Post> findById(Long id);
    Post save(Post post);
    void deleteById(Long id);
    
    /**
     * Récupère tous les posts avec leurs relations (auteur + thème)
     */
    List<Post> getAllPosts();
    
    /**
     * Récupère un post par ID avec ses relations
     */
    Optional<Post> getPostById(Long id);
    
    /**
     * Récupère les posts d'un thème spécifique
     */
    List<Post> getPostsByTheme(Long themeId);
    
    /**
     * Récupère les posts d'un auteur spécifique
     */
    List<Post> getPostsByAuthor(Long authorId);
    
    /**
     * Récupère le feed personnalisé d'un utilisateur (posts des thèmes abonnés)
     */
    List<Post> getPersonalizedFeed(Long userId);
    
    /**
     * Récupère les posts des thèmes auxquels l'utilisateur est abonné
     */
    List<Post> getPostsFromSubscribedThemes(Long userId);
    
    /**
     * Recherche des posts par titre
     */
    List<Post> searchPostsByTitle(String title);
    
    /**
     * Crée un nouveau post
     */
    Optional<Post> createPost(PostCreateRequest request, Long authorId);
    
    /**
     * Met à jour un post (seul l'auteur peut modifier)
     */
    Optional<Post> updatePost(Long postId, PostCreateRequest request, Long userId);
    
    /**
     * Supprime un post (seul l'auteur peut supprimer)
     */
    boolean deletePost(Long postId, Long userId);
}
