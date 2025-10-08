package com.elducche.mdd.service;

import com.elducche.mdd.dto.CommentCreateRequest;
import com.elducche.mdd.entity.Comment;

import java.util.List;
import java.util.Optional;

/**
 * Service de gestion des commentaires
 * 
 * Fournit les opérations CRUD sur les commentaires avec
 * gestion des relations et contrôles d'autorisation
 */
public interface CommentService {
    // Méthodes CRUD minimales pour les tests unitaires
    Optional<Comment> findById(Long id);
    Comment save(Comment comment);
    void deleteById(Long id);
    
    /**
     * Récupère tous les commentaires d'un post avec leurs auteurs
     */
    List<Comment> getCommentsByPost(Long postId);
    
    /**
     * Alias pour getCommentsByPost (compatibilité avec les contrôleurs)
     */
    List<Comment> getCommentsByPostId(Long postId);
    
    /**
     * Récupère tous les commentaires d'un utilisateur avec les posts associés
     */
    List<Comment> getCommentsByUser(Long userId);
    
    /**
     * Récupère un commentaire par ID avec ses relations
     */
    Optional<Comment> getCommentById(Long id);
    
    /**
     * Compte le nombre de commentaires d'un post
     */
    long countCommentsByPost(Long postId);
    
    /**
     * Crée un nouveau commentaire
     */
    Optional<Comment> createComment(CommentCreateRequest request, Long authorId);
    
    /**
     * Met à jour un commentaire (seul l'auteur peut modifier)
     */
    Optional<Comment> updateComment(Long commentId, String newContent, Long userId);
    
    /**
     * Supprime un commentaire (seul l'auteur peut supprimer)
     */
    boolean deleteComment(Long commentId, Long userId);
    
    /**
     * Supprime tous les commentaires d'un post (utilisé lors de la suppression d'un post)
     */
    void deleteCommentsByPost(Long postId);
    
    /**
     * Vérifie si un commentaire appartient à un utilisateur
     */
    boolean isCommentOwner(Long commentId, Long userId);
}
