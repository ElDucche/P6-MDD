package com.elducche.mdd.util;

import com.elducche.mdd.dto.*;
import com.elducche.mdd.entity.*;
import java.time.LocalDateTime;

/**
 * Builder de données de test
 * 
 * Cette classe utilitaire permet de créer facilement des objets
 * pour les tests unitaires et d'intégration.
 */
public class TestDataBuilder {

    // ===== ENTITÉS =====
    
    /**
     * Crée un utilisateur de test
     */
    public static User createUser() {
        return createUser("test@example.com", "testuser", "password123");
    }
    
    /**
     * Crée un utilisateur valide pour les tests
     */
    public static User createValidUser() {
        return createUser("test@example.com", "testuser", "password123");
    }
    
    public static User createUser(String email, String username, String password) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }
    
    public static User createUserWithId(Long id, String email, String username) {
        User user = createUser(email, username, "password123");
        user.setId(id);
        return user;
    }

    /**
     * Crée un thème de test
     */
    public static Theme createTheme() {
        return createTheme("Java", "Discussions sur Java");
    }
    
    /**
     * Crée un thème valide pour les tests
     */
    public static Theme createValidTheme() {
        return createTheme("Java", "Discussions sur Java");
    }
    
    public static Theme createTheme(String name, String description) {
        Theme theme = new Theme();
        theme.setTitle(name);
        theme.setDescription(description);
        theme.setCreatedAt(LocalDateTime.now());
        theme.setUpdatedAt(LocalDateTime.now());
        return theme;
    }
    
    public static Theme createThemeWithId(Long id, String name, String description) {
        Theme theme = createTheme(name, description);
        theme.setId(id);
        return theme;
    }

    /**
     * Crée un post de test
     */
    public static Post createPost() {
        return createPost("Titre test", "Contenu du post de test");
    }
    
    /**
     * Crée un post valide pour les tests
     */
    public static Post createValidPost() {
        return createPost("Titre test", "Contenu du post de test");
    }
    
    public static Post createPost(String title, String content) {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        return post;
    }
    
    public static Post createPostWithAuthorAndTheme(String title, String content, User author, Theme theme) {
        Post post = createPost(title, content);
        post.setAuthor(author);
        post.setTheme(theme);
        return post;
    }

    /**
     * Crée un commentaire de test
     */
    public static Comment createComment() {
        return createComment("Commentaire de test");
    }
    
    /**
     * Crée un commentaire valide pour les tests
     */
    public static Comment createValidComment() {
        return createComment("Commentaire de test");
    }
    
    public static Comment createComment(String content) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        return comment;
    }
    
    public static Comment createCommentWithAuthorAndPost(String content, User author, Post post) {
        Comment comment = createComment(content);
        comment.setAuthor(author);
        comment.setPost(post);
        return comment;
    }

    /**
     * Crée un abonnement de test avec utilisateur et thème
     */
    public static Subscription createSubscription(User user, Theme theme) {
        return new Subscription(user, theme);
    }

    /**
     * Crée un abonnement valide pour les tests (sans user/theme initialisés)
     * ATTENTION: L'ID composite doit être configuré manuellement après assignation des entités
     */
    public static Subscription createValidSubscription() {
        Subscription subscription = new Subscription();
        subscription.setSubscribedAt(LocalDateTime.now());
        return subscription;
    }
    
    /**
     * Crée un abonnement complet avec ses relations
     */
    public static Subscription createSubscriptionWithRelations(User user, Theme theme) {
        if (user == null || theme == null) {
            throw new IllegalArgumentException("User et Theme ne peuvent pas être null pour créer un Subscription");
        }
        if (user.getId() == null || theme.getId() == null) {
            throw new IllegalArgumentException("User et Theme doivent avoir des IDs pour créer un Subscription");
        }
        
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setTheme(theme);
        subscription.setId(new SubscriptionId(user.getId(), theme.getId()));
        subscription.setSubscribedAt(LocalDateTime.now());
        return subscription;
    }

    // ===== DTOs DE REQUÊTE =====
    
    /**
     * Crée une requête de login de test
     */
    public static LoginRequest createLoginRequest() {
        return createLoginRequest("test@example.com", "password123");
    }
    
    public static LoginRequest createLoginRequest(String email, String password) {
        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);
        return request;
    }

    /**
     * Crée une requête d'inscription de test
     */
    public static RegisterRequest createRegisterRequest() {
        return createRegisterRequest("test@example.com", "testuser", "password123");
    }
    
    public static RegisterRequest createRegisterRequest(String email, String username, String password) {
        RegisterRequest request = new RegisterRequest();
        request.setEmail(email);
        request.setUsername(username);
        request.setPassword(password);
        return request;
    }

    /**
     * Crée une requête de création de post
     */
    public static PostCreateRequest createPostCreateRequest() {
        return createPostCreateRequest("Titre test", "Contenu test", 1L);
    }
    
    public static PostCreateRequest createPostCreateRequest(String title, String content, Long themeId) {
        PostCreateRequest request = new PostCreateRequest();
        request.setTitle(title);
        request.setContent(content);
        request.setThemeId(themeId);
        return request;
    }

    /**
     * Crée une requête de création de commentaire
     */
    public static CommentCreateRequest createCommentCreateRequest() {
        return createCommentCreateRequest("Commentaire test");
    }
    
    public static CommentCreateRequest createCommentCreateRequest(String content) {
        CommentCreateRequest request = new CommentCreateRequest();
        request.setContent(content);
        return request;
    }

    /**
     * Crée une requête d'abonnement
     */
    public static SubscriptionRequest createSubscriptionRequest(Long themeId) {
        SubscriptionRequest request = new SubscriptionRequest();
        request.setThemeId(themeId);
        return request;
    }

    /**
     * Crée une requête de mise à jour de profil
     */
    public static UpdateUserProfileRequest createUpdateUserProfileRequest() {
        return createUpdateUserProfileRequest("newusername", "newemail@example.com");
    }
    
    public static UpdateUserProfileRequest createUpdateUserProfileRequest(String username, String email) {
        UpdateUserProfileRequest request = new UpdateUserProfileRequest();
        request.setUsername(username);
        request.setEmail(email);
        return request;
    }

    // ===== DTOs DE RÉPONSE =====
    
    /**
     * Crée une réponse de login réussie
     */
    public static LoginResponse createSuccessLoginResponse(String token) {
        return LoginResponse.success(token);
    }
    
    /**
     * Crée une réponse de login échouée
     */
    public static LoginResponse createErrorLoginResponse(String message) {
        return LoginResponse.error(message);
    }
}
