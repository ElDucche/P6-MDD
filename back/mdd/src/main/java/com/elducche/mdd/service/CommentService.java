package com.elducche.mdd.service;

import com.elducche.mdd.dto.CommentCreateRequest;
import com.elducche.mdd.entity.Comment;
import com.elducche.mdd.entity.Post;
import com.elducche.mdd.entity.User;
import com.elducche.mdd.repository.CommentRepository;
import com.elducche.mdd.repository.PostRepository;
import com.elducche.mdd.repository.UserRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

/**
 * Service de gestion des commentaires
 * 
 * Fournit les opérations CRUD sur les commentaires avec
 * gestion des relations et contrôles d'autorisation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    
    /**
     * Récupère tous les commentaires d'un post avec leurs auteurs
     */
    public List<Comment> getCommentsByPost(Long postId) {
        return commentRepository.findByPostIdWithAuthor(postId);
    }
    
    /**
     * Alias pour getCommentsByPost (compatibilité avec les contrôleurs)
     */
    public List<Comment> getCommentsByPostId(Long postId) {
        return getCommentsByPost(postId);
    }
    
    /**
     * Récupère tous les commentaires d'un utilisateur avec les posts associés
     */
    public List<Comment> getCommentsByUser(Long userId) {
        return commentRepository.findByAuthorIdWithPostAndTheme(userId);
    }
    
    /**
     * Récupère un commentaire par ID avec ses relations
     */
    public Optional<Comment> getCommentById(Long id) {
        return commentRepository.findByIdWithAuthorAndPost(id);
    }
    
    /**
     * Compte le nombre de commentaires d'un post
     */
    public long countCommentsByPost(Long postId) {
        return commentRepository.countByPostId(postId);
    }
    
    /**
     * Crée un nouveau commentaire
     */
    public Optional<Comment> createComment(CommentCreateRequest request, Long authorId) {
        try {
            // Vérification de l'existence de l'auteur
            Optional<User> authorOpt = userRepository.findById(authorId);
            if (authorOpt.isEmpty()) {
                log.warn("Tentative de création de commentaire avec auteur inexistant: {}", authorId);
                return Optional.empty();
            }
            
            // Vérification de l'existence du post
            Optional<Post> postOpt = postRepository.findById(request.getPostId());
            if (postOpt.isEmpty()) {
                log.warn("Tentative de création de commentaire avec post inexistant: {}", request.getPostId());
                return Optional.empty();
            }
            
            // Validation du contenu
            if (request.getContent() == null || request.getContent().trim().isEmpty()) {
                log.warn("Tentative de création de commentaire avec contenu vide par l'utilisateur {}", authorId);
                return Optional.empty();
            }
            
            // Création du commentaire
            Comment comment = new Comment();
            comment.setContent(request.getContent().trim());
            comment.setAuthor(authorOpt.get());
            comment.setPost(postOpt.get());
            
            Comment savedComment = commentRepository.save(comment);
            log.info("Nouveau commentaire créé par l'utilisateur {} sur le post {}", authorId, request.getPostId());
            
            // Retourner le commentaire avec ses relations
            return commentRepository.findByIdWithAuthorAndPost(savedComment.getId());
            
        } catch (Exception e) {
            log.error("Erreur lors de la création du commentaire par l'utilisateur {} sur le post {}: {}", 
                     authorId, request.getPostId(), e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Met à jour un commentaire (seul l'auteur peut modifier)
     */
    public Optional<Comment> updateComment(Long commentId, String newContent, Long userId) {
        try {
            Optional<Comment> commentOpt = commentRepository.findByIdWithAuthorAndPost(commentId);
            
            if (commentOpt.isEmpty()) {
                log.warn("Tentative de mise à jour d'un commentaire inexistant: {}", commentId);
                return Optional.empty();
            }
            
            Comment comment = commentOpt.get();
            
            // Vérification que l'utilisateur est bien l'auteur
            if (!comment.getAuthor().getId().equals(userId)) {
                log.warn("Tentative de mise à jour du commentaire {} par un utilisateur non autorisé: {}", 
                        commentId, userId);
                return Optional.empty();
            }
            
            // Validation du nouveau contenu
            if (newContent == null || newContent.trim().isEmpty()) {
                log.warn("Tentative de mise à jour du commentaire {} avec contenu vide", commentId);
                return Optional.empty();
            }
            
            // Mise à jour du contenu
            comment.setContent(newContent.trim());
            
            Comment savedComment = commentRepository.save(comment);
            log.info("Commentaire {} mis à jour par l'utilisateur {}", commentId, userId);
            
            return commentRepository.findByIdWithAuthorAndPost(savedComment.getId());
            
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour du commentaire {} par l'utilisateur {}: {}", 
                     commentId, userId, e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Supprime un commentaire (seul l'auteur peut supprimer)
     */
    public boolean deleteComment(Long commentId, Long userId) {
        try {
            Optional<Comment> commentOpt = commentRepository.findByIdWithAuthorAndPost(commentId);
            
            if (commentOpt.isEmpty()) {
                log.warn("Tentative de suppression d'un commentaire inexistant: {}", commentId);
                return false;
            }
            
            Comment comment = commentOpt.get();
            
            // Vérification que l'utilisateur est bien l'auteur
            if (!comment.getAuthor().getId().equals(userId)) {
                log.warn("Tentative de suppression du commentaire {} par un utilisateur non autorisé: {}", 
                        commentId, userId);
                return false;
            }
            
            commentRepository.delete(comment);
            log.info("Commentaire {} supprimé par l'utilisateur {}", commentId, userId);
            
            return true;
            
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du commentaire {} par l'utilisateur {}: {}", 
                     commentId, userId, e.getMessage());
            return false;
        }
    }
    
    /**
     * Supprime tous les commentaires d'un post (utilisé lors de la suppression d'un post)
     */
    public void deleteCommentsByPost(Long postId) {
        try {
            List<Comment> comments = commentRepository.findByPostId(postId);
            if (!comments.isEmpty()) {
                commentRepository.deleteAll(comments);
                log.info("{} commentaire(s) supprimé(s) du post {}", comments.size(), postId);
            }
        } catch (Exception e) {
            log.error("Erreur lors de la suppression des commentaires du post {}: {}", postId, e.getMessage());
        }
    }
    
    /**
     * Vérifie si un commentaire appartient à un utilisateur
     */
    public boolean isCommentOwner(Long commentId, Long userId) {
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        return commentOpt.isPresent() && commentOpt.get().getAuthor().getId().equals(userId);
    }
}
