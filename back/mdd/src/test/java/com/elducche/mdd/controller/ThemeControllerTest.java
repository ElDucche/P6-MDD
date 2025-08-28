package com.elducche.mdd.controller;

import com.elducche.mdd.entity.Theme;
import com.elducche.mdd.entity.User;
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
 * Tests d'intégration pour ThemeController
 * 
 * Ces tests valident :
 * - La récupération de tous les thèmes
 * - La récupération d'un thème par ID
 * - Les cas d'erreur (thème inexistant)
 */
@DisplayName("Tests du contrôleur de thèmes")
class ThemeControllerTest extends BaseIntegrationTest {

    @Autowired
    private ThemeRepository themeRepository;
    
    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Theme testTheme1;
    private Theme testTheme2;

    @BeforeEach
    void setUp() {
        // Créer un utilisateur de test
        testUser = new User();
        testUser.setEmail("themer@example.com");
        testUser.setUsername("themer");
        testUser.setPassword(encodePassword("Password123!"));
        testUser = userRepository.save(testUser);

        // Créer des thèmes de test
        testTheme1 = new Theme();
        testTheme1.setTitle("Technology");
        testTheme1.setDescription("All about technology and innovation");
        testTheme1 = themeRepository.save(testTheme1);

        testTheme2 = new Theme();
        testTheme2.setTitle("Science");
        testTheme2.setDescription("Scientific discoveries and research");
        testTheme2 = themeRepository.save(testTheme2);
    }

    @Test
    @DisplayName("GET /api/themes - Récupération de tous les thèmes")
    @WithMockUser
    void testGetAllThemes_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/themes")
                .with(SecurityTestUtils.authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[?(@.id == " + testTheme1.getId() + ")].title", hasItem("Technology")))
                .andExpect(jsonPath("$[?(@.id == " + testTheme1.getId() + ")].description", hasItem("All about technology and innovation")))
                .andExpect(jsonPath("$[?(@.id == " + testTheme2.getId() + ")].title", hasItem("Science")))
                .andExpect(jsonPath("$[?(@.id == " + testTheme2.getId() + ")].description", hasItem("Scientific discoveries and research")));
    }

    @Test
    @DisplayName("GET /api/themes - Échec sans authentification")
    void testGetAllThemes_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/themes"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/themes/{id} - Récupération d'un thème par ID")
    @WithMockUser
    void testGetThemeById_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/themes/{id}", testTheme1.getId())
                .with(SecurityTestUtils.authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testTheme1.getId().intValue())))
                .andExpect(jsonPath("$.title", is("Technology")))
                .andExpect(jsonPath("$.description", is("All about technology and innovation")))
                .andExpect(jsonPath("$.createdAt", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/themes/{id} - Thème inexistant")
    @WithMockUser
    void testGetThemeById_NotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/themes/{id}", 99999L)
                .with(SecurityTestUtils.authenticatedUser(testUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/themes/{id} - Échec sans authentification")
    void testGetThemeById_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/themes/{id}", testTheme1.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/themes - Vérification de la structure des données")
    @WithMockUser
    void testGetAllThemes_DataStructure() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/themes")
                .with(SecurityTestUtils.authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", notNullValue()))
                .andExpect(jsonPath("$[0].title", notNullValue()))
                .andExpect(jsonPath("$[0].description", notNullValue()))
                .andExpect(jsonPath("$[0].createdAt", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/themes - Vérification de l'ordre des thèmes")
    @WithMockUser
    void testGetAllThemes_Order() throws Exception {
        // When & Then - Vérifier que les thèmes sont retournés dans un ordre cohérent
        mockMvc.perform(get("/api/themes")
                .with(SecurityTestUtils.authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[0].id", notNullValue()))
                .andExpect(jsonPath("$[1].id", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/themes/{id} - Test avec différents types d'IDs")
    @WithMockUser
    void testGetThemeById_DifferentIdTypes() throws Exception {
        // Test avec ID valide
        mockMvc.perform(get("/api/themes/{id}", testTheme1.getId())
                .with(SecurityTestUtils.authenticatedUser(testUser)))
                .andExpect(status().isOk());

        // Test avec ID 0 (invalide)
        mockMvc.perform(get("/api/themes/{id}", 0L)
                .with(SecurityTestUtils.authenticatedUser(testUser)))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/themes/{id}", -1L)
                .with(SecurityTestUtils.authenticatedUser(testUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Endpoints nécessitent une authentification")
    void testEndpoints_RequireAuthentication() throws Exception {
        // Test des endpoints qui nécessitent une authentification
        mockMvc.perform(get("/api/themes")).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/themes/1")).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/themes - Performance avec plusieurs thèmes")
    @WithMockUser
    void testGetAllThemes_Performance() throws Exception {
        // Given - Créer quelques thèmes supplémentaires pour tester la performance
        for (int i = 3; i <= 5; i++) {
            Theme theme = new Theme();
            theme.setTitle("Theme " + i);
            theme.setDescription("Description for theme " + i);
            themeRepository.save(theme);
        }

        // When & Then - Vérifier que la réponse est rapide et contient tous les thèmes
        mockMvc.perform(get("/api/themes")
                .with(SecurityTestUtils.authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(5))));
    }
}
