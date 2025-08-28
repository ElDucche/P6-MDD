package com.elducche.mdd.controller;

import com.elducche.mdd.dto.SubscriptionRequest;
import com.elducche.mdd.entity.Subscription;
import com.elducche.mdd.entity.Theme;
import com.elducche.mdd.entity.User;
import com.elducche.mdd.repository.SubscriptionRepository;
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
 * Tests d'intégration pour SubscriptionController
 * 
 * Ces tests valident :
 * - La récupération des abonnements utilisateur
 * - La création d'abonnements
 * - La suppression d'abonnements
 * - Les autorisations et validations
 */
@DisplayName("Tests du contrôleur d'abonnements")
class SubscriptionControllerTest extends BaseIntegrationTest {

    @Autowired
    private SubscriptionRepository subscriptionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ThemeRepository themeRepository;

    private User testUser;
    private User otherUser;
    private Theme testTheme1;
    private Theme testTheme2;
    private Subscription testSubscription;
    private SubscriptionRequest validSubscriptionRequest;

    @BeforeEach
    void setUp() {
        // Créer un utilisateur de test
        testUser = new User();
        testUser.setEmail("subscriber@example.com");
        testUser.setUsername("subscriber");
        testUser.setPassword(encodePassword("Password123!"));
        testUser = userRepository.save(testUser);

        // Créer un autre utilisateur
        otherUser = new User();
        otherUser.setEmail("other@example.com");
        otherUser.setUsername("otheruser");
        otherUser.setPassword(encodePassword("Password123!"));
        otherUser = userRepository.save(otherUser);

        // Créer des thèmes de test
        testTheme1 = new Theme();
        testTheme1.setTitle("Programming");
        testTheme1.setDescription("All about programming");
        testTheme1 = themeRepository.save(testTheme1);

        testTheme2 = new Theme();
        testTheme2.setTitle("Design");
        testTheme2.setDescription("UI/UX design discussions");
        testTheme2 = themeRepository.save(testTheme2);

        // Créer un abonnement de test
        testSubscription = new Subscription(testUser, testTheme1);
        testSubscription = subscriptionRepository.save(testSubscription);

        // Préparer une requête d'abonnement valide
        validSubscriptionRequest = new SubscriptionRequest();
        validSubscriptionRequest.setThemeId(testTheme2.getId());
    }

    @Test
    @DisplayName("GET /api/subscriptions - Récupération des abonnements utilisateur")
    @WithMockUser
    void testGetUserSubscriptions_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/subscriptions")
                .with(SecurityTestUtils.authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].user.username", is("subscriber")))
                .andExpect(jsonPath("$[0].theme.title", is("Programming")));
    }

    @Test
    @DisplayName("GET /api/subscriptions - Utilisateur sans abonnements")
    @WithMockUser
    void testGetUserSubscriptions_NoSubscriptions() throws Exception {
        // When & Then - Autre utilisateur sans abonnements
        mockMvc.perform(get("/api/subscriptions")
                .with(SecurityTestUtils.authenticatedUser(otherUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("POST /api/subscriptions - Création d'un abonnement réussie")
    @WithMockUser
    void testSubscribeToTheme_Success() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/subscriptions")
                .with(SecurityTestUtils.authenticatedUser(testUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validSubscriptionRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.user.username", is("subscriber")))
                .andExpect(jsonPath("$.theme.title", is("Design")));
    }

    @Test
    @DisplayName("POST /api/subscriptions - Échec avec thème inexistant")
    @WithMockUser
    void testSubscribeToTheme_ThemeNotFound() throws Exception {
        // Given - Thème inexistant
        validSubscriptionRequest.setThemeId(99999L);

        // When & Then
        mockMvc.perform(post("/api/subscriptions")
                .with(SecurityTestUtils.authenticatedUser(testUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validSubscriptionRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Impossible de créer l'abonnement")));
    }

    @Test
    @DisplayName("POST /api/subscriptions - Échec abonnement déjà existant")
    @WithMockUser
    void testSubscribeToTheme_AlreadySubscribed() throws Exception {
        // Given - Abonnement déjà existant au thème 1
        validSubscriptionRequest.setThemeId(testTheme1.getId());

        // When & Then
        mockMvc.perform(post("/api/subscriptions")
                .with(SecurityTestUtils.authenticatedUser(testUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validSubscriptionRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Impossible de créer l'abonnement")));
    }

    @Test
    @DisplayName("DELETE /api/subscriptions/{id} - Suppression d'abonnement réussie")
    @WithMockUser
    void testUnsubscribeFromTheme_Success() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/subscriptions/{id}", testTheme1.getId())
                .with(SecurityTestUtils.authenticatedUser(testUser)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/subscriptions/{id} - Échec abonnement inexistant")
    @WithMockUser
    void testUnsubscribeFromTheme_NotFound() throws Exception {
        // When & Then - ID inexistant
        mockMvc.perform(delete("/api/subscriptions/{id}", 99999L)
                .with(SecurityTestUtils.authenticatedUser(testUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/subscriptions/{id} - Échec suppression par autre utilisateur")
    @WithMockUser
    void testUnsubscribeFromTheme_OtherUser() throws Exception {
        // When & Then - Autre utilisateur essaie de supprimer l'abonnement
        mockMvc.perform(delete("/api/subscriptions/{id}", testTheme1.getId())
                .with(SecurityTestUtils.authenticatedUser(otherUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/subscriptions - Échec avec themeId null")
    @WithMockUser
    void testSubscribeToTheme_NullThemeId() throws Exception {
        // Given - ThemeId null
        validSubscriptionRequest.setThemeId(null);

        // When & Then
        mockMvc.perform(post("/api/subscriptions")
                .with(SecurityTestUtils.authenticatedUser(testUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validSubscriptionRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Endpoints nécessitent une authentification")
    void testEndpoints_RequireAuthentication() throws Exception {
        // Test des endpoints qui nécessitent une authentification
        mockMvc.perform(get("/api/subscriptions")).andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/subscriptions")).andExpect(status().isUnauthorized());
        mockMvc.perform(delete("/api/subscriptions/1")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/subscriptions - Validation des données d'entrée")
    @WithMockUser
    void testSubscribeToTheme_ValidationErrors() throws Exception {
        // Given - Requête complètement vide
        SubscriptionRequest emptyRequest = new SubscriptionRequest();

        // When & Then
        mockMvc.perform(post("/api/subscriptions")
                .with(SecurityTestUtils.authenticatedUser(testUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(emptyRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Gestion des abonnements multiples")
    @WithMockUser
    void testMultipleSubscriptions() throws Exception {
        // Given - S'abonner au deuxième thème
        mockMvc.perform(post("/api/subscriptions")
                .with(SecurityTestUtils.authenticatedUser(testUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validSubscriptionRequest)))
                .andExpect(status().isCreated());

        // When & Then - Vérifier que l'utilisateur a maintenant 2 abonnements
        mockMvc.perform(get("/api/subscriptions")
                .with(SecurityTestUtils.authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("DELETE /api/subscriptions/{id} - Test avec IDs invalides")
    @WithMockUser
    void testUnsubscribeFromTheme_InvalidIds() throws Exception {
        // Test avec ID 0
        mockMvc.perform(delete("/api/subscriptions/{id}", 0L)
                .with(SecurityTestUtils.authenticatedUser(testUser)))
                .andExpect(status().isNotFound());

        // Test avec ID négatif
        mockMvc.perform(delete("/api/subscriptions/{id}", -1L)
                .with(SecurityTestUtils.authenticatedUser(testUser)))
                .andExpect(status().isNotFound());
    }
}
