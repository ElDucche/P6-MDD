package com.elducche.mdd.integration;

import com.elducche.mdd.dto.CommentCreateRequest;
import com.elducche.mdd.dto.LoginRequest;
import com.elducche.mdd.dto.RegisterRequest;
import com.elducche.mdd.entity.Comment;
import com.elducche.mdd.entity.Post;
import com.elducche.mdd.entity.Theme;
import com.elducche.mdd.entity.User;
import com.elducche.mdd.repository.CommentRepository;
import com.elducche.mdd.repository.PostRepository;
import com.elducche.mdd.repository.ThemeRepository;
import com.elducche.mdd.repository.UserRepository;
import com.elducche.mdd.util.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration pour les endpoints des commentaires.
 * 
 * Teste les scénarios bout-en-bout incluant :
 * - Création de commentaires sur des posts
 * - Récupération des commentaires par post
 * - Authentification et autorisation
 * - Gestion des erreurs et cas limites
 * - Validation des relations entre entités
 */
@ActiveProfiles("test")
@DisplayName("Tests d'intégration - Gestion des Commentaires")
class CommentIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private UserRepository userRepository;

    private String authToken;
    private User testUser;
    private Theme testTheme;
    private Post testPost;

    @BeforeEach
    void setUp() throws Exception {
        // Nettoyer les données
        commentRepository.deleteAll();
        postRepository.deleteAll();
        themeRepository.deleteAll();
        userRepository.deleteAll();

        // Créer les données de test
        testUser = createAndSaveTestUser();
        testTheme = createAndSaveTestTheme();
        testPost = createAndSaveTestPost();

        // Obtenir un token d'authentification
        authToken = authenticateAndGetToken();

        // Créer des commentaires de test
        createTestComments();
    }

    @Test
    @DisplayName("Doit créer un commentaire avec succès")
    void shouldCreateComment() throws Exception {
        CommentCreateRequest request = new CommentCreateRequest();
        request.setContent("Commentaire d'intégration très intéressant sur ce post.");

        mockMvc.perform(post("/api/posts/{postId}/comments", testPost.getId())
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", is("Commentaire d'intégration très intéressant sur ce post.")))
                .andExpect(jsonPath("$.author.username", is("commentintegrationuser")))
                .andExpect(jsonPath("$.post.id", is(testPost.getId().intValue())))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.createdAt", notNullValue()));
    }

    @Test
    @DisplayName("Doit récupérer tous les commentaires d'un post")
    void shouldGetCommentsByPost() throws Exception {
        mockMvc.perform(get("/api/posts/{postId}/comments", testPost.getId())
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].content", anyOf(
                    is("Premier commentaire de test"), 
                    is("Deuxième commentaire de test"))))
                .andExpect(jsonPath("$[1].content", anyOf(
                    is("Premier commentaire de test"), 
                    is("Deuxième commentaire de test"))))
                .andExpect(jsonPath("$[*].post.id", everyItem(is(testPost.getId().intValue()))));
    }

    @Test
    @DisplayName("Doit rejeter la création de commentaire sans authentification")
    void shouldRejectUnauthenticatedCommentCreation() throws Exception {
        CommentCreateRequest request = new CommentCreateRequest();
        request.setContent("Commentaire non autorisé");

        mockMvc.perform(post("/api/posts/{postId}/comments", testPost.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Doit valider le contenu obligatoire du commentaire")
    void shouldValidateRequiredCommentContent() throws Exception {
        CommentCreateRequest request = new CommentCreateRequest();
        // Contenu vide/null

        mockMvc.perform(post("/api/posts/{postId}/comments", testPost.getId())
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Doit rejeter les commentaires sur des posts inexistants")
    void shouldRejectCommentsOnNonExistentPosts() throws Exception {
        CommentCreateRequest request = new CommentCreateRequest();
        request.setContent("Commentaire sur post inexistant");

        mockMvc.perform(post("/api/posts/{postId}/comments", 99999L)
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    /**
     * Crée et sauvegarde un utilisateur de test
     */
    private User createAndSaveTestUser() {
        User user = new User();
        user.setEmail("commenttest@example.com");
        user.setUsername("commenttestuser");
        user.setPassword(passwordEncoder.encode("?Password1"));
        return userRepository.save(user);
    }

    /**
     * Crée et sauvegarde un thème de test
     */
    private Theme createAndSaveTestTheme() {
        Theme theme = new Theme();
        theme.setTitle("Thème Test Comment Integration");
        theme.setDescription("Thème pour les tests d'intégration des commentaires");
        return themeRepository.save(theme);
    }

    /**
     * Crée et sauvegarde un post de test
     */
    private Post createAndSaveTestPost() {
        Post post = new Post();
        post.setTitle("Post Test pour Commentaires");
        post.setContent("Contenu du post pour tester les commentaires");
        post.setAuthor(testUser);
        post.setTheme(testTheme);
        return postRepository.save(post);
    }

    /**
     * Authentifie l'utilisateur et retourne le token JWT
     */
    private String authenticateAndGetToken() throws Exception {
        // Inscription
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("comment-integration@example.com");
        registerRequest.setUsername("commentintegrationuser");
        registerRequest.setPassword("?Password1");

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // Connexion
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("comment-integration@example.com");
        loginRequest.setPassword("?Password1");

        String response = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return extractTokenFromResponse(response);
    }

    /**
     * Crée des commentaires de test
     */
    private void createTestComments() {
        Comment comment1 = new Comment();
        comment1.setContent("Premier commentaire de test");
        comment1.setAuthor(testUser);
        comment1.setPost(testPost);
        commentRepository.save(comment1);

        Comment comment2 = new Comment();
        comment2.setContent("Deuxième commentaire de test");
        comment2.setAuthor(testUser);
        comment2.setPost(testPost);
        commentRepository.save(comment2);
    }

    /**
     * Extrait le token JWT de la réponse JSON
     */
    private String extractTokenFromResponse(String response) throws Exception {
        com.fasterxml.jackson.databind.JsonNode jsonNode = objectMapper.readTree(response);
        return jsonNode.get("token").asText();
    }
}
