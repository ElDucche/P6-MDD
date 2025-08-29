package com.elducche.mdd.integration;

import com.elducche.mdd.dto.LoginRequest;
import com.elducche.mdd.dto.RegisterRequest;
import com.elducche.mdd.entity.Theme;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration pour les endpoints des thèmes.
 * 
 * Teste les scénarios bout-en-bout incluant :
 * - Consultation des thèmes disponibles
 * - Validation de l'authentification
 * - Gestion des erreurs et exceptions
 * - Vérification de la cohérence des données
 */
@ActiveProfiles("test")
@DisplayName("Tests d'intégration - Gestion des Thèmes")
class ThemeIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private UserRepository userRepository;

    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        // Nettoyer les données
        themeRepository.deleteAll();
        userRepository.deleteAll();

        // Obtenir un token d'authentification
        authToken = authenticateAndGetToken();

        // Créer des thèmes de test
        createTestThemes();
    }

    @Test
    @DisplayName("Doit récupérer tous les thèmes avec succès")
    void shouldGetAllThemes() throws Exception {
        mockMvc.perform(get("/api/themes")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", anyOf(is("Technologie"), is("Science"))))
                .andExpect(jsonPath("$[1].title", anyOf(is("Technologie"), is("Science"))))
                .andExpect(jsonPath("$[0].description", notNullValue()))
                .andExpect(jsonPath("$[1].description", notNullValue()));
    }

    @Test
    @DisplayName("Doit gérer les caractères spéciaux dans les thèmes")
    void shouldHandleSpecialCharactersInThemes() throws Exception {
        // Créer un thème avec des caractères spéciaux
        Theme specialTheme = new Theme();
        specialTheme.setTitle("Français & Émojis 🚀");
        specialTheme.setDescription("Thème avec accents: é, è, à, ç et émojis 🎯 📚");
        themeRepository.save(specialTheme);

        mockMvc.perform(get("/api/themes")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].title", hasItem("Français & Émojis 🚀")))
                .andExpect(jsonPath("$[*].description", 
                    hasItem(containsString("émojis 🎯 📚"))));
    }

    @Test
    @DisplayName("Doit rejeter l'accès sans authentification")
    void shouldRejectUnauthenticatedAccess() throws Exception {
        mockMvc.perform(get("/api/themes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Doit rejeter l'accès avec token invalide")
    void shouldRejectInvalidToken() throws Exception {
        mockMvc.perform(get("/api/themes")
                .header("Authorization", "Bearer invalid-token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Doit retourner une liste vide si aucun thème n'existe")
    void shouldReturnEmptyListWhenNoThemes() throws Exception {
        // Supprimer tous les thèmes
        themeRepository.deleteAll();

        mockMvc.perform(get("/api/themes")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Doit valider la structure des données des thèmes")
    void shouldValidateThemeDataStructure() throws Exception {
        mockMvc.perform(get("/api/themes")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", notNullValue()))
                .andExpect(jsonPath("$[0].title", notNullValue()))
                .andExpect(jsonPath("$[0].description", notNullValue()))
                .andExpect(jsonPath("$[0].createdAt", notNullValue()))
                .andExpect(jsonPath("$[0].updatedAt", notNullValue()));
    }

    /**
     * Authentifie l'utilisateur et retourne le token JWT
     */
    private String authenticateAndGetToken() throws Exception {
        // Inscription
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("theme-integration@example.com");
        registerRequest.setUsername("themeintegrationuser");
        registerRequest.setPassword("?Password1");

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // Connexion
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("theme-integration@example.com");
        loginRequest.setPassword("?Password1");

        String response = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return extractTokenFromResponse(response);
    }

    /**
     * Crée des thèmes de test
     */
    private void createTestThemes() {
        Theme theme1 = new Theme();
        theme1.setTitle("Technologie");
        theme1.setDescription("Tout sur les nouvelles technologies et innovations");
        themeRepository.save(theme1);

        Theme theme2 = new Theme();
        theme2.setTitle("Science");
        theme2.setDescription("Découvertes scientifiques et recherches actuelles");
        themeRepository.save(theme2);
    }

    /**
     * Extrait le token JWT de la réponse JSON
     */
    private String extractTokenFromResponse(String response) throws Exception {
        com.fasterxml.jackson.databind.JsonNode jsonNode = objectMapper.readTree(response);
        return jsonNode.get("token").asText();
    }
}
