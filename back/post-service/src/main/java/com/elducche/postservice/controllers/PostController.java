package com.elducche.postservice.controllers;

import com.elducche.postservice.models.Post;
import com.elducche.postservice.service.PostService;
import com.elducche.postservice.exceptions.PostNotFoundException;
import com.elducche.postservice.exceptions.UnauthorizedException;
import com.elducche.postservice.exceptions.PostValidationException;
import com.elducche.postservice.exceptions.ThemeNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * Récupérer tous les posts
     */
    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    /**
     * Récupérer un post par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        try {
            Optional<Post> post = postService.getPostById(id);
            return post.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupérer les posts par thème
     */
    @GetMapping("/theme/{themeId}")
    public ResponseEntity<List<Post>> getPostsByTheme(@PathVariable Long themeId) {
        try {
            System.out.println("[POST CONTROLLER] Récupération des posts pour le thème ID: " + themeId);
            List<Post> posts = postService.getPostsByTheme(themeId);
            System.out.println("[POST CONTROLLER] " + posts.size() + " posts trouvés pour le thème " + themeId);
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            System.err.println("[POST CONTROLLER] Erreur lors de la récupération des posts pour le thème " + themeId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupérer les posts par auteur
     */
    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<Post>> getPostsByAuthor(@PathVariable Long authorId) {
        try {
            List<Post> posts = postService.getPostsByAuthor(authorId);
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupérer les posts de l'utilisateur connecté
     */
    @GetMapping("/my-posts")
    public ResponseEntity<List<Post>> getMyPosts(Authentication authentication) {
        try {
            // Extraire l'ID utilisateur du token JWT (supposé être dans le "name")
            Long authorId = Long.parseLong(authentication.getName());
            List<Post> posts = postService.getPostsByAuthor(authorId);
            return ResponseEntity.ok(posts);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupérer les posts des thèmes auxquels l'utilisateur est abonné
     */
    @GetMapping("/subscribed")
    public ResponseEntity<List<Post>> getPostsFromSubscribedThemes(Authentication authentication) {
        try {
            // Extraire l'ID utilisateur du token JWT
            Long userId = Long.parseLong(authentication.getName());
            List<Post> posts = postService.getPostsFromSubscribedThemes(userId);
            return ResponseEntity.ok(posts);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Créer un nouveau post
     */
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post, Authentication authentication) {
        try {
            // Extraire l'ID utilisateur du token JWT
            Long authorId = Long.parseLong(authentication.getName());
            post.setAuthorId(authorId);
            
            Post createdPost = postService.createPost(post);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (PostValidationException | ThemeNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Mettre à jour un post existant
     */
    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody Post post, Authentication authentication) {
        try {
            // Extraire l'ID utilisateur du token JWT
            Long authorId = Long.parseLong(authentication.getName());
            
            Post updatedPost = postService.updatePost(id, post, authorId);
            return ResponseEntity.ok(updatedPost);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (PostNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (PostValidationException | ThemeNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Supprimer un post
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, Authentication authentication) {
        try {
            // Extraire l'ID utilisateur du token JWT
            Long authorId = Long.parseLong(authentication.getName());
            
            postService.deletePost(id, authorId);
            return ResponseEntity.noContent().build();
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (PostNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
