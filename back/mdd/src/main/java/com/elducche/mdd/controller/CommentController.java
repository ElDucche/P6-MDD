package com.elducche.mdd.controller;

import com.elducche.mdd.dto.CommentCreateRequest;
import com.elducche.mdd.entity.Comment;
import com.elducche.mdd.service.CommentService;
import com.elducche.mdd.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * Contrôleur pour la gestion des commentaires
 */
@Slf4j
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final SecurityUtil securityUtil;

    /**
     * Récupère tous les commentaires d'un post
     * @param postId ID du post
     * @return Liste des commentaires du post
     */
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Comment>> getCommentsByPost(@PathVariable Long postId) {
        log.debug("Récupération des commentaires pour le post ID : {}", postId);
        
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    /**
     * Crée un nouveau commentaire
     * @param request Données du commentaire
     * @return Le commentaire créé ou erreur
     */
    @PostMapping
    public ResponseEntity<?> createComment(@Valid @RequestBody CommentCreateRequest request) {
        log.debug("Création d'un commentaire pour le post ID : {}", request.getPostId());
        
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            Optional<Comment> comment = commentService.createComment(request, userId);
            if (comment.isPresent()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(comment.get());
            } else {
                return ResponseEntity.badRequest().body("Impossible de créer le commentaire");
            }
        } catch (Exception e) {
            log.error("Erreur lors de la création du commentaire : ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body("Erreur lors de la création du commentaire");
        }
    }
}
