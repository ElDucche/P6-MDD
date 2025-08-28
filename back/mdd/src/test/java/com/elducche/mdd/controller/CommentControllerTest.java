package com.elducche.mdd.controller;

import com.elducche.mdd.dto.CommentCreateRequest;
import com.elducche.mdd.entity.Comment;
import com.elducche.mdd.entity.Post;
import com.elducche.mdd.entity.Theme;
import com.elducche.mdd.entity.User;
import com.elducche.mdd.repository.CommentRepository;
import com.elducche.mdd.repository.PostRepository;
import com.elducche.mdd.repository.ThemeRepository;
import com.elducche.mdd.repository.UserRepository;
import com.elducche.mdd.util.BaseIntegrationTest;
import com.elducche.mdd.util.SecurityTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Tests d'intégration pour CommentController
 * 
 * Ces tests valident :
 * - La récupération des commentaires par post
 * - La création de commentaires
 * - Les autorisations et validations
 */
@DisplayName("Tests du contrôleur de commentaires")
class CommentControllerTest extends BaseIntegrationTest {

    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ThemeRepository themeRepository;

    private User testUser;
    private Theme testTheme;
    private Post testPost;
    private Comment testComment;
    private CommentCreateRequest validCommentRequest;

    @BeforeEach
    void setUp() {
        // Créer un utilisateur de test
        testUser = new User();
        testUser.setEmail("commenter@example.com");
        testUser.setUsername("commenter");
        testUser.setPassword(encodePassword("Password123!"));
        testUser = userRepository.save(testUser);

        // Créer un thème de test
        testTheme = new Theme();
        testTheme.setTitle("Discussion");
        testTheme.setDescription("General discussions");
        testTheme = themeRepository.save(testTheme);

        // Créer un post de test
        testPost = new Post();
        testPost.setTitle("Test Post for Comments");
        testPost.setContent("This post will receive comments");
        testPost.setAuthor(testUser);
        testPost.setTheme(testTheme);
        testPost = postRepository.save(testPost);

        // Créer un commentaire de test
        testComment = new Comment();
        testComment.setContent("This is a test comment");
        testComment.setAuthor(testUser);
        testComment.setPost(testPost);
        testComment = commentRepository.save(testComment);

        // Préparer une requête de création de commentaire valide
        validCommentRequest = new CommentCreateRequest();
        validCommentRequest.setContent("This is a new comment");
        validCommentRequest.setPostId(testPost.getId());
    }

    @Test
    @DisplayName("GET /api/comments/post/{postId} - Récupération des commentaires d'un post")
    @WithMockUser
    void testGetCommentsByPost_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/comments/post/{postId}", testPost.getId())
                .with(SecurityTestUtils.authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[?(@.id == " + testComment.getId() + ")].content", hasItem("This is a test comment")))
                .andExpect(jsonPath("$[?(@.id == " + testComment.getId() + ")].author.username", hasItem("commenter")));
    }

    @Test
    @DisplayName("GET /api/comments/post/{postId} - Post inexistant")
    @WithMockUser
    void testGetCommentsByPost_PostNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/comments/post/{postId}", 99999L)
                .with(SecurityTestUtils.authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0))); // Liste vide pour post inexistant
    }

    @Test
    @DisplayName("POST /api/comments - Création d'un commentaire réussie")
    @WithMockUser
    void testCreateComment_Success() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/comments")
                .with(SecurityTestUtils.authenticatedUser(testUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validCommentRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", is("This is a new comment")))
                .andExpect(jsonPath("$.author.username", is("commenter")));
    }

    @Test
    @DisplayName("POST /api/comments - Échec avec post inexistant")
    @WithMockUser
    void testCreateComment_PostNotFound() throws Exception {
        // Given - Post inexistant
        validCommentRequest.setPostId(99999L);

        // When & Then
        mockMvc.perform(post("/api/comments")
                .with(SecurityTestUtils.authenticatedUser(testUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validCommentRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Impossible de créer le commentaire")));
    }

    @Test
    @DisplayName("POST /api/comments - Échec avec contenu vide")
    @WithMockUser
    void testCreateComment_EmptyContent() throws Exception {
        // Given - Contenu vide
        validCommentRequest.setContent("");

        // When & Then
        mockMvc.perform(post("/api/comments")
                .with(SecurityTestUtils.authenticatedUser(testUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validCommentRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/comments - Échec avec postId null")
    @WithMockUser
    void testCreateComment_NullPostId() throws Exception {
        // Given - PostId null
        validCommentRequest.setPostId(null);

        // When & Then
        mockMvc.perform(post("/api/comments")
                .with(SecurityTestUtils.authenticatedUser(testUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validCommentRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/comments - Échec sans authentification")
    void testCreateComment_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validCommentRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/comments/post/{postId} - Échec sans authentification")
    void testGetCommentsByPost_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/comments/post/{postId}", testPost.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/comments - Validation des données d'entrée")
    @WithMockUser
    void testCreateComment_ValidationErrors() throws Exception {
        // Given - Données invalides
        CommentCreateRequest invalidRequest = new CommentCreateRequest();
        // Laisser tous les champs null/vides

        // When & Then
        mockMvc.perform(post("/api/comments")
                .with(SecurityTestUtils.authenticatedUser(testUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Création de commentaire avec contenu très long")
    @WithMockUser
    void testCreateComment_LongContent() throws Exception {
        // Given - Contenu très long
        String longContent = "A".repeat(1000); // 1000 caractères
        validCommentRequest.setContent(longContent);

        // When & Then
        mockMvc.perform(post("/api/comments")
                .with(SecurityTestUtils.authenticatedUser(testUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validCommentRequest)))
                .andExpect(status().isCreated()) // Ou BAD_REQUEST selon les règles de validation
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Récupération des commentaires pour un post sans commentaires")
    @WithMockUser
    void testGetCommentsByPost_NoComments() throws Exception {
        // Given - Créer un nouveau post sans commentaires
        Post emptyPost = new Post();
        emptyPost.setTitle("Post without comments");
        emptyPost.setContent("This post has no comments");
        emptyPost.setAuthor(testUser);
        emptyPost.setTheme(testTheme);
        emptyPost = postRepository.save(emptyPost);

        // When & Then
        mockMvc.perform(get("/api/comments/post/{postId}", emptyPost.getId())
                .with(SecurityTestUtils.authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
