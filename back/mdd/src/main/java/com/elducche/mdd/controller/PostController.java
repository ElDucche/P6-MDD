package com.elducche.mdd.controller;

import com.elducche.mdd.dto.PostCreateRequest;
import com.elducche.mdd.dto.PostDTO;
import com.elducche.mdd.entity.Post;
import com.elducche.mdd.mapper.EntityMapper;
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
    private final EntityMapper entityMapper;

    /**
     * Récupère tous les posts
     */
    @GetMapping
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        List<Post> posts = postService.getAllPosts();
        // Convertir en DTOs pour éviter les références circulaires
        List<PostDTO> postDTOs = posts != null 
            ? posts.stream().map(entityMapper::toPostDTO).toList()
            : List.of();
        return ResponseEntity.ok(postDTOs);
    }

    /**
     * Récupère un post par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long id) {
        Optional<Post> post = postService.getPostById(id);
        return post.map(p -> ResponseEntity.ok(entityMapper.toPostDTO(p)))
                  .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Récupère les posts d'un thème
     */
    @GetMapping("/theme/{themeId}")
    public ResponseEntity<List<PostDTO>> getPostsByTheme(@PathVariable Long themeId) {
        List<Post> posts = postService.getPostsByTheme(themeId);
        List<PostDTO> postDTOs = posts.stream().map(entityMapper::toPostDTO).toList();
        return ResponseEntity.ok(postDTOs);
    }

    /**
     * Récupère les posts des thèmes auxquels l'utilisateur est abonné
     */
    @GetMapping("/feed")
    public ResponseEntity<List<PostDTO>> getPostsFeed() {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        List<Post> posts = postService.getPostsFromSubscribedThemes(userId);
        List<PostDTO> postDTOs = posts.stream().map(entityMapper::toPostDTO).toList();
        return ResponseEntity.ok(postDTOs);
    }
    
    /**
     * Récupère les posts des thèmes auxquels l'utilisateur est abonné (alias)
     */
    @GetMapping("/subscribed")
    public ResponseEntity<List<PostDTO>> getSubscribedPosts() {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        List<Post> posts = postService.getPostsFromSubscribedThemes(userId);
        // Convertir en DTOs et retourner une liste vide si aucun post
        List<PostDTO> postDTOs = posts != null 
            ? posts.stream().map(entityMapper::toPostDTO).toList()
            : List.of();
        return ResponseEntity.ok(postDTOs);
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
                PostDTO postDTO = entityMapper.toPostDTO(post.get());
                return ResponseEntity.status(HttpStatus.CREATED).body(postDTO);
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