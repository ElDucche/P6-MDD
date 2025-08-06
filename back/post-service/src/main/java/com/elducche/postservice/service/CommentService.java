package com.elducche.postservice.service;

import com.elducche.postservice.models.Comment;
import com.elducche.postservice.repositories.CommentRepository;
import com.elducche.postservice.utils.CommentMapper;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    /**
     * Récupérer tous les commentaires d'un post avec les noms d'utilisateur des auteurs
     */
    public List<Comment> getCommentsByPostId(Long postId) {
        List<Object[]> results = commentRepository.findByPostIdWithAuthorUsername(postId);
        return CommentMapper.mapToCommentsWithAuthor(results);
    }

    /**
     * Créer un nouveau commentaire
     */
    public Comment createComment(Comment comment, Long authorId) {
        // Valider les données
        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Le contenu du commentaire est requis");
        }
        
        if (comment.getPostId() == null) {
            throw new IllegalArgumentException("L'ID du post est requis");
        }
        
        // Définir les champs obligatoires
        comment.setAuthorId(authorId);
        LocalDateTime now = LocalDateTime.now();
        comment.setCreatedAt(now);
        comment.setUpdatedAt(now);
        
        // Sauvegarder le commentaire
        Comment savedComment = commentRepository.save(comment);
        
        // Récupérer le commentaire avec le username de l'auteur
        Optional<Object[]> resultWithAuthor = commentRepository.findByIdWithAuthorUsername(savedComment.getId());
        if (resultWithAuthor.isPresent()) {
            return CommentMapper.mapToCommentWithAuthor(resultWithAuthor.get());
        }
        
        // Fallback si le username n'est pas trouvé
        return savedComment;
    }

    /**
     * Supprimer un commentaire (seulement si l'utilisateur est l'auteur)
     */
    public void deleteComment(Long commentId, Long authorId) {
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        
        if (commentOpt.isEmpty()) {
            throw new IllegalArgumentException("Commentaire non trouvé");
        }
        
        Comment comment = commentOpt.get();
        
        if (!comment.getAuthorId().equals(authorId)) {
            throw new SecurityException("Vous n'êtes pas autorisé à supprimer ce commentaire");
        }
        
        commentRepository.deleteById(commentId);
    }

    // Méthode de compatibilité - à supprimer plus tard
    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    // Méthode de compatibilité - à supprimer plus tard
    public List<Comment> getCommentsByPost(Long postId) {
        return commentRepository.findByPostId(postId);
    }
}
