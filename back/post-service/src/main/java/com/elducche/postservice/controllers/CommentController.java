package com.elducche.postservice.controllers;

import com.elducche.postservice.models.Comment;
import com.elducche.postservice.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Créer un nouveau commentaire
     */
    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody Comment comment, Authentication authentication) {
        try {
            // Récupérer l'ID de l'utilisateur depuis le JWT
            Long authorId = Long.valueOf(authentication.getName());
            Comment createdComment = commentService.createComment(comment, authorId);
            return ResponseEntity.ok(createdComment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Récupérer tous les commentaires d'un post avec les usernames des auteurs
     */
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Comment>> getCommentsByPost(@PathVariable Long postId) {
        try {
            List<Comment> comments = commentService.getCommentsByPostId(postId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Supprimer un commentaire (seulement si l'utilisateur est l'auteur)
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId, Authentication authentication) {
        try {
            Long authorId = Long.valueOf(authentication.getName());
            commentService.deleteComment(commentId, authorId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
