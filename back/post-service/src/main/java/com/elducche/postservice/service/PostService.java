package com.elducche.postservice.service;

import com.elducche.postservice.models.Post;
import com.elducche.postservice.repositories.PostRepository;
import com.elducche.postservice.repositories.ThemeRepository;
import com.elducche.postservice.exceptions.PostNotFoundException;
import com.elducche.postservice.exceptions.UnauthorizedException;
import com.elducche.postservice.exceptions.PostValidationException;
import com.elducche.postservice.exceptions.ThemeNotFoundException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final ThemeRepository themeRepository;

    public PostService(PostRepository postRepository, ThemeRepository themeRepository) {
        this.postRepository = postRepository;
        this.themeRepository = themeRepository;
    }

    /**
     * Récupérer tous les posts
     */
    public List<Post> getAllPosts() {
        return (List<Post>) postRepository.findAll();
    }

    /**
     * Récupérer un post par son ID
     */
    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    /**
     * Récupérer les posts par thème
     */
    public List<Post> getPostsByTheme(Long themeId) {
        return postRepository.findByThemeId(themeId);
    }

    /**
     * Récupérer les posts par auteur
     */
    public List<Post> getPostsByAuthor(Long authorId) {
        return postRepository.findByAuthorId(authorId);
    }

    /**
     * Créer un nouveau post
     */
    public Post createPost(Post post) {
        validatePostCreation(post);
        
        // Définir les timestamps
        LocalDateTime now = LocalDateTime.now();
        post.setCreatedAt(now);
        post.setUpdatedAt(now);
        
        return postRepository.save(post);
    }

    /**
     * Mettre à jour un post existant
     */
    public Post updatePost(Long id, Post updatedPost, Long authorId) {
        Optional<Post> existingPostOpt = postRepository.findById(id);
        
        if (existingPostOpt.isEmpty()) {
            throw new PostNotFoundException(id);
        }
        
        Post existingPost = existingPostOpt.get();
        
        // Vérifier que l'utilisateur est l'auteur du post
        if (!existingPost.getAuthorId().equals(authorId)) {
            throw new UnauthorizedException("User is not authorized to update this post");
        }
        
        // Valider le thème si modifié
        if (updatedPost.getThemeId() != null && !updatedPost.getThemeId().equals(existingPost.getThemeId())) {
            validateThemeExists(updatedPost.getThemeId());
        }
        
        // Mettre à jour les champs
        if (updatedPost.getTitle() != null) {
            existingPost.setTitle(updatedPost.getTitle());
        }
        if (updatedPost.getContent() != null) {
            existingPost.setContent(updatedPost.getContent());
        }
        if (updatedPost.getThemeId() != null) {
            existingPost.setThemeId(updatedPost.getThemeId());
        }
        
        existingPost.setUpdatedAt(LocalDateTime.now());
        
        return postRepository.save(existingPost);
    }

    /**
     * Supprimer un post
     */
    public void deletePost(Long id, Long authorId) {
        Optional<Post> existingPostOpt = postRepository.findById(id);
        
        if (existingPostOpt.isEmpty()) {
            throw new PostNotFoundException(id);
        }
        
        Post existingPost = existingPostOpt.get();
        
        // Vérifier que l'utilisateur est l'auteur du post
        if (!existingPost.getAuthorId().equals(authorId)) {
            throw new UnauthorizedException("User is not authorized to delete this post");
        }
        
        postRepository.deleteById(id);
    }

    /**
     * Valider la création d'un post
     */
    private void validatePostCreation(Post post) {
        if (post.getTitle() == null || post.getTitle().trim().isEmpty()) {
            throw new PostValidationException("Post title is required");
        }
        
        if (post.getContent() == null || post.getContent().trim().isEmpty()) {
            throw new PostValidationException("Post content is required");
        }
        
        if (post.getAuthorId() == null) {
            throw new PostValidationException("Post author is required");
        }
        
        if (post.getThemeId() == null) {
            throw new PostValidationException("Post theme is required");
        }
        
        validateThemeExists(post.getThemeId());
    }

    /**
     * Valider que le thème existe
     */
    private void validateThemeExists(Long themeId) {
        if (!themeRepository.existsById(themeId)) {
            throw new ThemeNotFoundException(themeId);
        }
    }
}
