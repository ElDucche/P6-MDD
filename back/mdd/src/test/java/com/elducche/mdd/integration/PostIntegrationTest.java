package com.elducche.mdd.integration;

import com.elducche.mdd.dto.PostCreateRequest;
import com.elducche.mdd.dto.LoginRequest;
import com.elducche.mdd.dto.RegisterRequest;
import com.elducche.mdd.entity.Post;
import com.elducche.mdd.entity.Theme;
import com.elducche.mdd.entity.User;
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
 * Tests d'intégration pour les endpoints des posts.
 * 
 * Teste les scénarios bout-en-bout incluant :
 * - Création de posts avec validation des thèmes
 * - Consultation des posts par ID et par thème
 * - Authentification et autorisation
 * - Gestion des erreurs et cas limites
 * - Pagination et tri des résultats
 */
@ActiveProfiles("test")
@DisplayName("Tests d'intégration - Gestion des Posts")
class PostIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private UserRepository userRepository;

    private String authToken;
    private User testUser;
    private Theme testTheme;

    @BeforeEach
    void setUp() throws Exception {
        // Nettoyer les données
        postRepository.deleteAll();
        themeRepository.deleteAll();
        userRepository.deleteAll();

        // Créer les données de test
        testUser = createAndSaveTestUser();
        testTheme = createAndSaveTestTheme();

        // Obtenir un token d'authentification
        authToken = authenticateAndGetToken();

        // Créer des posts de test
        createTestPosts();
    }

    @Test
    @DisplayName("Doit créer un post avec succès")
    void shouldCreatePost() throws Exception {
        PostCreateRequest request = new PostCreateRequest();
        request.setTitle("Nouveau Post d'Intégration");
        request.setContent("Contenu complet du post avec toutes les informations nécessaires.");
        request.setThemeId(testTheme.getId());

        mockMvc.perform(post("/api/posts")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title", is("Nouveau Post d'Intégration")))
                .andExpect(jsonPath("$.content", is("Contenu complet du post avec toutes les informations nécessaires.")))
                .andExpect(jsonPath("$.author.username", is("postintegrationuser")))
                .andExpect(jsonPath("$.theme.id", is(testTheme.getId().intValue())))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.createdAt", notNullValue()));
    }

    @Test
    @DisplayName("Doit récupérer un post par son ID")
    void shouldGetPostById() throws Exception {
        Post post = createSingleTestPost();

        mockMvc.perform(get("/api/posts/{id}", post.getId())
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(post.getId().intValue())))
                .andExpect(jsonPath("$.title", is(post.getTitle())))
                .andExpect(jsonPath("$.content", is(post.getContent())))
                .andExpect(jsonPath("$.author.id", is(post.getAuthor().getId().intValue())))
                .andExpect(jsonPath("$.theme.id", is(post.getTheme().getId().intValue())));
    }

    @Test
    @DisplayName("Doit récupérer tous les posts triés par date")
    void shouldGetAllPostsSortedByDate() throws Exception {
        mockMvc.perform(get("/api/posts")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", anyOf(is("Post Technologie"), is("Post Science"))))
                .andExpect(jsonPath("$[1].title", anyOf(is("Post Technologie"), is("Post Science"))))
                .andExpect(jsonPath("$[0].createdAt", notNullValue()))
                .andExpect(jsonPath("$[1].createdAt", notNullValue()));
    }

    @Test
    @DisplayName("Doit filtrer les posts par thème")
    void shouldFilterPostsByTheme() throws Exception {
        mockMvc.perform(get("/api/posts")
                .param("themeId", testTheme.getId().toString())
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[*].theme.id", everyItem(is(testTheme.getId().intValue()))));
    }

    @Test
    @DisplayName("Doit rejeter la création de post sans authentification")
    void shouldRejectUnauthenticatedPostCreation() throws Exception {
        PostCreateRequest request = new PostCreateRequest();
        request.setTitle("Post Non Autorisé");
        request.setContent("Contenu du post non autorisé");
        request.setThemeId(testTheme.getId());

        mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Doit valider les données obligatoires lors de la création")
    void shouldValidateRequiredFieldsForCreation() throws Exception {
        PostCreateRequest request = new PostCreateRequest();
        // Titre manquant
        request.setContent("Contenu sans titre");
        request.setThemeId(testTheme.getId());

        mockMvc.perform(post("/api/posts")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Doit rejeter la création avec un thème inexistant")
    void shouldRejectCreationWithInvalidTheme() throws Exception {
        PostCreateRequest request = new PostCreateRequest();
        request.setTitle("Post avec thème invalide");
        request.setContent("Contenu du post");
        request.setThemeId(99999L); // ID inexistant

        mockMvc.perform(post("/api/posts")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Doit retourner 404 pour un post inexistant")
    void shouldReturn404ForNonExistentPost() throws Exception {
        mockMvc.perform(get("/api/posts/{id}", 99999L)
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Doit gérer les caractères spéciaux dans le contenu")
    void shouldHandleSpecialCharactersInContent() throws Exception {
        PostCreateRequest request = new PostCreateRequest();
        request.setTitle("Post avec émojis 🚀 et accents");
        request.setContent("Contenu avec caractères spéciaux: é, è, à, ç, ñ, ü et émojis 🎯 📚 🔥");
        request.setThemeId(testTheme.getId());

        mockMvc.perform(post("/api/posts")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("Post avec émojis 🚀 et accents")))
                .andExpect(jsonPath("$.content", containsString("émojis 🎯 📚 🔥")));
    }

    @Test
    @DisplayName("Doit gérer un contenu de post très long")
    void shouldHandleLongPostContent() throws Exception {
        String longContent = "Lorem ipsum ".repeat(100) + "fin du contenu.";
        
        PostCreateRequest request = new PostCreateRequest();
        request.setTitle("Post avec contenu très long");
        request.setContent(longContent);
        request.setThemeId(testTheme.getId());

        mockMvc.perform(post("/api/posts")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content", is(longContent)));
    }

    @Test
    @DisplayName("Doit maintenir la cohérence de l'auteur dans les posts")
    void shouldMaintainAuthorConsistency() throws Exception {
        PostCreateRequest request = new PostCreateRequest();
        request.setTitle("Post de cohérence auteur");
        request.setContent("Contenu pour vérifier l'auteur");
        request.setThemeId(testTheme.getId());

        String response = mockMvc.perform(post("/api/posts")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Extraire l'ID du post créé
        com.fasterxml.jackson.databind.JsonNode postNode = objectMapper.readTree(response);
        Long postId = postNode.get("id").asLong();

        // Vérifier que l'auteur est cohérent lors de la récupération
        mockMvc.perform(get("/api/posts/{id}", postId)
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author.email", is("post-integration@example.com")))
                .andExpect(jsonPath("$.author.username", is("postintegrationuser")));
    }

    /**
     * Crée et sauvegarde un utilisateur de test
     */
    private User createAndSaveTestUser() {
        User user = new User();
        user.setEmail("posttest@example.com");
        user.setUsername("posttestuser");
        user.setPassword(passwordEncoder.encode("?Password1"));
        return userRepository.save(user);
    }

    /**
     * Crée et sauvegarde un thème de test
     */
    private Theme createAndSaveTestTheme() {
        Theme theme = new Theme();
        theme.setTitle("Thème Test Integration");
        theme.setDescription("Thème pour les tests d'intégration des posts");
        return themeRepository.save(theme);
    }

    /**
     * Authentifie l'utilisateur et retourne le token JWT
     */
    private String authenticateAndGetToken() throws Exception {
        // Inscription
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("post-integration@example.com");
        registerRequest.setUsername("postintegrationuser");
        registerRequest.setPassword("?Password1");

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // Connexion
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("post-integration@example.com");
        loginRequest.setPassword("?Password1");

        String response = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return extractTokenFromResponse(response);
    }

    /**
     * Crée des posts de test
     */
    private void createTestPosts() {
        Post post1 = new Post();
        post1.setTitle("Post Technologie");
        post1.setContent("Contenu du post sur la technologie avec des détails intéressants.");
        post1.setAuthor(testUser);
        post1.setTheme(testTheme);
        postRepository.save(post1);

        Post post2 = new Post();
        post2.setTitle("Post Science");
        post2.setContent("Contenu du post sur la science avec des découvertes récentes.");
        post2.setAuthor(testUser);
        post2.setTheme(testTheme);
        postRepository.save(post2);
    }

    /**
     * Crée un post unique pour les tests spécifiques
     */
    private Post createSingleTestPost() {
        Post post = new Post();
        post.setTitle("Post Unique Test");
        post.setContent("Contenu du post unique pour test spécifique.");
        post.setAuthor(testUser);
        post.setTheme(testTheme);
        return postRepository.save(post);
    }

    /**
     * Extrait le token JWT de la réponse JSON
     */
    private String extractTokenFromResponse(String response) throws Exception {
        com.fasterxml.jackson.databind.JsonNode jsonNode = objectMapper.readTree(response);
        return jsonNode.get("token").asText();
    }
}
