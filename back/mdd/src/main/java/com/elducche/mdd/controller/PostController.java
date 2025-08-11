package com.elducche.mdd.controller;

import com.elducche.mdd.dto.PostCreateRequest;
import com.elducche.mdd.entity.Post;
import com.elducche.mdd.service.PostService;
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
 * Contrôleur pour la gestion des posts
 */
@Slf4j
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final SecurityUtil securityUtil;

    /**
     * Récupère tous les posts
     */
    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postService.getAllPosts();
        // Retourner une liste vide si aucun post n'existe
        return ResponseEntity.ok(posts != null ? posts : List.of());
    }

    /**
     * Récupère un post par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        Optional<Post> post = postService.getPostById(id);
        return post.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Récupère les posts d'un thème
     */
    @GetMapping("/theme/{themeId}")
    public ResponseEntity<List<Post>> getPostsByTheme(@PathVariable Long themeId) {
        List<Post> posts = postService.getPostsByTheme(themeId);
        return ResponseEntity.ok(posts);
    }

    /**
     * Récupère les posts des thèmes auxquels l'utilisateur est abonné
     */
    @GetMapping("/feed")
    public ResponseEntity<List<Post>> getPostsFeed() {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        List<Post> posts = postService.getPostsFromSubscribedThemes(userId);
        return ResponseEntity.ok(posts);
    }
    
    /**
     * Récupère les posts des thèmes auxquels l'utilisateur est abonné (alias)
     */
    @GetMapping("/subscribed")
    public ResponseEntity<List<Post>> getSubscribedPosts() {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        List<Post> posts = postService.getPostsFromSubscribedThemes(userId);
        // Retourner une liste vide si l'utilisateur n'a pas d'abonnements
        return ResponseEntity.ok(posts != null ? posts : List.of());
    }

    /**
     * Crée un nouveau post
     */
    @PostMapping
    public ResponseEntity<?> createPost(@Valid @RequestBody PostCreateRequest request) {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            Optional<Post> post = postService.createPost(request, userId);
            if (post.isPresent()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(post.get());
            } else {
                return ResponseEntity.badRequest().body("Erreur lors de la création du post");
            }
        } catch (Exception e) {
            log.error("Erreur lors de la création du post: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur interne du serveur");
        }
    }
}