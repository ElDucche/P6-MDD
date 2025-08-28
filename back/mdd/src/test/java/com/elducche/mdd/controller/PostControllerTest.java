package com.elducche.mdd.controller;

import com.elducche.mdd.dto.PostCreateRequest;
import com.elducche.mdd.entity.Post;
import com.elducche.mdd.entity.Theme;
import com.elducche.mdd.entity.User;
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
 * Tests d'intégration pour PostController
 * 
 * Ces tests valident :
 * - La récupération des posts
 * - La création de posts
 * - La modification et suppression de posts
 * - Les autorisations (seul l'auteur peut modifier/supprimer)
 */
@DisplayName("Tests du contrôleur de posts")
class PostControllerTest extends BaseIntegrationTest {

    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ThemeRepository themeRepository;

    private User testUser;
    private User otherUser;
    private Theme testTheme;
    private Post testPost;
    private PostCreateRequest validPostRequest;

    @BeforeEach
    void setUp() {
        // Créer un utilisateur de test
        testUser = new User();
        testUser.setEmail("author@example.com");
        testUser.setUsername("author");
        testUser.setPassword(encodePassword("Password123!"));
        testUser = userRepository.save(testUser);

        // Créer un autre utilisateur
        otherUser = new User();
        otherUser.setEmail("other@example.com");
        otherUser.setUsername("otheruser");
        otherUser.setPassword(encodePassword("Password123!"));
        otherUser = userRepository.save(otherUser);

        // Créer un thème de test
        testTheme = new Theme();
        testTheme.setTitle("Tech");
        testTheme.setDescription("Technology discussions");
        testTheme = themeRepository.save(testTheme);

        // Créer un post de test
        testPost = new Post();
        testPost.setTitle("Test Post");
        testPost.setContent("This is a test post content");
        testPost.setAuthor(testUser);
        testPost.setTheme(testTheme);
        testPost = postRepository.save(testPost);

        // Préparer une requête de création de post valide
        validPostRequest = new PostCreateRequest();
        validPostRequest.setTitle("New Post");
        validPostRequest.setContent("This is a new post content");
        validPostRequest.setThemeId(testTheme.getId());
    }

    @Test
    @DisplayName("GET /api/posts - Récupération de tous les posts")
    @WithMockUser
    void testGetAllPosts_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/posts")
                .with(SecurityTestUtils.authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[?(@.id == " + testPost.getId() + ")].title", hasItem("Test Post")))
                .andExpect(jsonPath("$[?(@.id == " + testPost.getId() + ")].content", hasItem("This is a test post content")))
                .andExpect(jsonPath("$[?(@.id == " + testPost.getId() + ")].author.username", hasItem("author")))
                .andExpect(jsonPath("$[?(@.id == " + testPost.getId() + ")].theme.title", hasItem("Tech")));
    }

    @Test
    @DisplayName("GET /api/posts - Échec sans authentification")
    void testGetAllPosts_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/posts/{id} - Récupération d'un post par ID")
    @WithMockUser
    void testGetPostById_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/posts/{id}", testPost.getId())
                .with(SecurityTestUtils.authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testPost.getId().intValue())))
                .andExpect(jsonPath("$.title", is("Test Post")))
                .andExpect(jsonPath("$.content", is("This is a test post content")))
                .andExpect(jsonPath("$.author.username", is("author")))
                .andExpect(jsonPath("$.theme.title", is("Tech")));
    }

    @Test
    @DisplayName("GET /api/posts/{id} - Échec avec ID inexistant")
    @WithMockUser
    void testGetPostById_NotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/posts/{id}", 99999L)
                .with(SecurityTestUtils.authenticatedUser(testUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/posts - Création d'un post réussie")
    @WithMockUser
    void testCreatePost_Success() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/posts")
                .with(SecurityTestUtils.authenticatedUser(testUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validPostRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title", is("New Post")))
                .andExpect(jsonPath("$.content", is("This is a new post content")))
                .andExpect(jsonPath("$.author.username", is("author")))
                .andExpect(jsonPath("$.theme.title", is("Tech")));
    }

    @Test
    @DisplayName("POST /api/posts - Échec avec thème inexistant")
    @WithMockUser
    void testCreatePost_ThemeNotFound() throws Exception {
        // Given - Thème inexistant
        validPostRequest.setThemeId(99999L);

        // When & Then
        mockMvc.perform(post("/api/posts")
                .with(SecurityTestUtils.authenticatedUser(testUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validPostRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Thème non trouvé")));
    }

    @Test
    @DisplayName("POST /api/posts - Échec avec données invalides")
    @WithMockUser
    void testCreatePost_InvalidData() throws Exception {
        // Given - Données invalides
        PostCreateRequest invalidRequest = new PostCreateRequest();
        invalidRequest.setTitle(""); // Vide
        invalidRequest.setContent(""); // Vide
        invalidRequest.setThemeId(null); // Null

        // When & Then
        mockMvc.perform(post("/api/posts")
                .with(SecurityTestUtils.authenticatedUser(testUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/posts/{id} - Modification d'un post par son auteur")
    @WithMockUser
    void testUpdatePost_ByAuthor_Success() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/posts/{id}", testPost.getId())
                .with(SecurityTestUtils.authenticatedUser(testUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validPostRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title", is("New Post")))
                .andExpect(jsonPath("$.content", is("This is a new post content")));
    }

    @Test
    @DisplayName("PUT /api/posts/{id} - Échec modification par un autre utilisateur")
    @WithMockUser
    void testUpdatePost_ByOtherUser_Forbidden() throws Exception {
        // When & Then - Tentative de modification par un autre utilisateur
        mockMvc.perform(put("/api/posts/{id}", testPost.getId())
                .with(SecurityTestUtils.authenticatedUser(otherUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validPostRequest)))
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("autorisation")));
    }

    @Test
    @DisplayName("DELETE /api/posts/{id} - Suppression d'un post par son auteur")
    @WithMockUser
    void testDeletePost_ByAuthor_Success() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/posts/{id}", testPost.getId())
                .with(SecurityTestUtils.authenticatedUser(testUser)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/posts/{id} - Échec suppression par un autre utilisateur")
    @WithMockUser
    void testDeletePost_ByOtherUser_Forbidden() throws Exception {
        // When & Then - Tentative de suppression par un autre utilisateur
        mockMvc.perform(delete("/api/posts/{id}", testPost.getId())
                .with(SecurityTestUtils.authenticatedUser(otherUser)))
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("autorisation")));
    }

    @Test
    @DisplayName("GET /api/posts/feed - Récupération du feed personnalisé")
    @WithMockUser
    void testGetUserFeed_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/posts/feed")
                .with(SecurityTestUtils.authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", isA(java.util.List.class)));
    }

    @Test
    @DisplayName("Endpoints nécessitent une authentification")
    void testEndpoints_RequireAuthentication() throws Exception {
        // Test des endpoints qui nécessitent une authentification
        mockMvc.perform(get("/api/posts")).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/posts/1")).andExpect(status().isForbidden());
        mockMvc.perform(post("/api/posts")).andExpect(status().isForbidden());
        mockMvc.perform(put("/api/posts/1")).andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/posts/1")).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/posts/feed")).andExpect(status().isForbidden());
    }
}
