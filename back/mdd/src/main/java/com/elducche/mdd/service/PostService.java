package com.elducche.mdd.service;

import com.elducche.mdd.dto.PostCreateRequest;
import com.elducche.mdd.entity.Post;
import com.elducche.mdd.entity.Theme;
import com.elducche.mdd.entity.User;
import com.elducche.mdd.repository.PostRepository;
import com.elducche.mdd.repository.ThemeRepository;
import com.elducche.mdd.repository.UserRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

/**
 * Service de gestion des posts
 * 
 * Fournit les opérations CRUD et métier sur les posts avec
 * gestion optimisée des relations et du feed personnalisé
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    
    private final PostRepository postRepository;
    private final ThemeRepository themeRepository;
    private final UserRepository userRepository;
    
    /**
     * Récupère tous les posts avec leurs relations (auteur + thème)
     */
    public List<Post> getAllPosts() {
        return postRepository.findAllWithAuthorAndTheme();
    }
    
    /**
     * Récupère un post par ID avec ses relations
     */
    public Optional<Post> getPostById(Long id) {
        return postRepository.findByIdWithAuthorAndTheme(id);
    }
    
    /**
     * Récupère les posts d'un thème spécifique
     */
    public List<Post> getPostsByTheme(Long themeId) {
        return postRepository.findByThemeIdWithAuthorAndTheme(themeId);
    }
    
    /**
     * Récupère les posts d'un auteur spécifique
     */
    public List<Post> getPostsByAuthor(Long authorId) {
        return postRepository.findByAuthorIdWithAuthorAndTheme(authorId);
    }
    
    /**
     * Récupère le feed personnalisé d'un utilisateur (posts des thèmes abonnés)
     */
    public List<Post> getPersonalizedFeed(Long userId) {
        return postRepository.findPostsFromSubscribedThemes(userId);
    }
    
    /**
     * Recherche des posts par titre
     */
    public List<Post> searchPostsByTitle(String title) {
        return postRepository.findByTitleContainingIgnoreCase(title);
    }
    
    /**
     * Crée un nouveau post
     */
    public Optional<Post> createPost(PostCreateRequest request, Long authorId) {
        try {
            // Vérification de l'existence de l'auteur
            Optional<User> authorOpt = userRepository.findById(authorId);
            if (authorOpt.isEmpty()) {
                log.warn("Tentative de création de post avec auteur inexistant: {}", authorId);
                return Optional.empty();
            }
            
            // Vérification de l'existence du thème
            Optional<Theme> themeOpt = themeRepository.findById(request.getThemeId());
            if (themeOpt.isEmpty()) {
                log.warn("Tentative de création de post avec thème inexistant: {}", request.getThemeId());
                return Optional.empty();
            }
            
            // Création du post
            Post post = new Post();
            post.setTitle(request.getTitle());
            post.setContent(request.getContent());
            post.setAuthor(authorOpt.get());
            post.setTheme(themeOpt.get());
            
            Post savedPost = postRepository.save(post);
            log.info("Nouveau post créé par l'utilisateur {} dans le thème {}", authorId, request.getThemeId());
            
            // Retourner le post avec ses relations
            return postRepository.findByIdWithAuthorAndTheme(savedPost.getId());
            
        } catch (Exception e) {
            log.error("Erreur lors de la création du post par l'utilisateur {}: {}", authorId, e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Met à jour un post (seul l'auteur peut modifier)
     */
    public Optional<Post> updatePost(Long postId, PostCreateRequest request, Long userId) {
        try {
            Optional<Post> postOpt = postRepository.findByIdWithAuthorAndTheme(postId);
            
            if (postOpt.isEmpty()) {
                log.warn("Tentative de mise à jour d'un post inexistant: {}", postId);
                return Optional.empty();
            }
            
            Post post = postOpt.get();
            
            // Vérification que l'utilisateur est bien l'auteur
            if (!post.getAuthor().getId().equals(userId)) {
                log.warn("Tentative de mise à jour du post {} par un utilisateur non autorisé: {}", postId, userId);
                return Optional.empty();
            }
            
            // Mise à jour des champs
            if (request.getTitle() != null) {
                post.setTitle(request.getTitle());
            }
            if (request.getContent() != null) {
                post.setContent(request.getContent());
            }
            
            // Changement de thème si spécifié
            if (request.getThemeId() != null && !request.getThemeId().equals(post.getTheme().getId())) {
                Optional<Theme> newThemeOpt = themeRepository.findById(request.getThemeId());
                if (newThemeOpt.isPresent()) {
                    post.setTheme(newThemeOpt.get());
                } else {
                    log.warn("Tentative de mise à jour avec thème inexistant: {}", request.getThemeId());
                    return Optional.empty();
                }
            }
            
            Post savedPost = postRepository.save(post);
            log.info("Post {} mis à jour par l'utilisateur {}", postId, userId);
            
            return postRepository.findByIdWithAuthorAndTheme(savedPost.getId());
            
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour du post {} par l'utilisateur {}: {}", postId, userId, e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Supprime un post (seul l'auteur peut supprimer)
     */
    public boolean deletePost(Long postId, Long userId) {
        try {
            Optional<Post> postOpt = postRepository.findByIdWithAuthorAndTheme(postId);
            
            if (postOpt.isEmpty()) {
                log.warn("Tentative de suppression d'un post inexistant: {}", postId);
                return false;
            }
            
            Post post = postOpt.get();
            
            // Vérification que l'utilisateur est bien l'auteur
            if (!post.getAuthor().getId().equals(userId)) {
                log.warn("Tentative de suppression du post {} par un utilisateur non autorisé: {}", postId, userId);
                return false;
            }
            
            postRepository.delete(post);
            log.info("Post {} supprimé par l'utilisateur {}", postId, userId);
            
            return true;
            
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du post {} par l'utilisateur {}: {}", postId, userId, e.getMessage());
            return false;
        }
    }
}
