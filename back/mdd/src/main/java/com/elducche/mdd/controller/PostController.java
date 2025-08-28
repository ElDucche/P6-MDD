package com.elducche.mdd.controller;

import com.elducche.mdd.dto.PostCreateRequest;
import com.elducche.mdd.dto.PostDTO;
import com.elducche.mdd.entity.Post;
import com.elducche.mdd.mapper.EntityMapper;
import com.elducche.mdd.service.PostService;
import com.elducche.mdd.util.AuthUtil;
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
    private final AuthUtil authUtil;
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
    @GetMapping("/subscribed")
    public ResponseEntity<List<PostDTO>> getSubscribedPosts() {
        return authUtil.executeWithAuth(userId -> {
            List<Post> posts = postService.getPostsFromSubscribedThemes(userId);
            List<PostDTO> postDTOs = posts.stream().map(entityMapper::toPostDTO).toList();
            return ResponseEntity.ok(postDTOs);
        });
    }

    /**
     * Crée un nouveau post
     */
    @PostMapping
    public ResponseEntity<?> createPost(@Valid @RequestBody PostCreateRequest request) {
        return authUtil.executeWithAuthHandleErrors(userId -> {
            try {
                Optional<Post> post = postService.createPost(request, userId);
                if (post.isPresent()) {
                    PostDTO postDTO = entityMapper.toPostDTO(post.get());
                    return ResponseEntity.status(HttpStatus.CREATED).body(postDTO);
                } else {
                    return ResponseEntity.badRequest().body("Erreur lors de la création du post");
                }
            } catch (IllegalArgumentException e) {
                if (e.getMessage().contains("Thème non trouvé")) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Thème non trouvé");
                }
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        });
    }

    /**
     * Met à jour un post existant
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Long id, @Valid @RequestBody PostCreateRequest request) {
        return authUtil.executeWithAuthHandleErrors(userId -> {
            Optional<Post> post = postService.updatePost(id, request, userId);
            if (post.isPresent()) {
                PostDTO postDTO = entityMapper.toPostDTO(post.get());
                return ResponseEntity.ok(postDTO);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Vous n'avez pas l'autorisation de modifier ce post");
            }
        });
    }

    /**
     * Supprime un post
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        return authUtil.executeWithAuthHandleErrors(userId -> {
            boolean deleted = postService.deletePost(id, userId);
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Vous n'avez pas l'autorisation de supprimer ce post");
            }
        });
    }

    /**
     * Récupère le feed personnalisé de l'utilisateur
     */
    @GetMapping("/feed")
    public ResponseEntity<List<PostDTO>> getUserFeed() {
        return authUtil.executeWithAuth(userId -> {
            List<Post> posts = postService.getPersonalizedFeed(userId);
            List<PostDTO> postDTOs = posts.stream().map(entityMapper::toPostDTO).toList();
            return ResponseEntity.ok(postDTOs);
        });
    }
}