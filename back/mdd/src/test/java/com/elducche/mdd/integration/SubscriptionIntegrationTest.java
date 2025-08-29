package com.elducche.mdd.integration;

import com.elducche.mdd.dto.LoginRequest;
import com.elducche.mdd.dto.RegisterRequest;
import com.elducche.mdd.dto.SubscriptionRequest;
import com.elducche.mdd.entity.Subscription;
import com.elducche.mdd.entity.Theme;
import com.elducche.mdd.entity.User;
import com.elducche.mdd.repository.PostRepository;
import com.elducche.mdd.repository.SubscriptionRepository;
import com.elducche.mdd.repository.ThemeRepository;
import com.elducche.mdd.repository.UserRepository;
import com.elducche.mdd.util.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration pour les endpoints des abonnements.
 * 
 * Teste les scénarios bout-en-bout incluant :
 * - Création et suppression d'abonnements aux thèmes
 * - Consultation des abonnements utilisateur
 * - Authentification et autorisation
 * - Gestion des erreurs et cas limites
 * - Validation des règles métier d'abonnement
 */
@ActiveProfiles("test")
@DisplayName("Tests d'intégration - Gestion des Abonnements")
class SubscriptionIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    private String authToken;
    private User testUser;
    private Theme testTheme;
    private Theme secondTestTheme;

    @BeforeEach
    void setUp() throws Exception {
        // Nettoyer les données
        subscriptionRepository.deleteAll();
        postRepository.deleteAll();
        themeRepository.deleteAll();
        userRepository.deleteAll();

        // Créer un utilisateur de test
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setPassword("hashedpassword");
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
        testUser = userRepository.save(testUser);

        // Créer des thèmes de test
        testTheme = new Theme();
        testTheme.setTitle("Java");
        testTheme.setDescription("Tout sur Java");
        testTheme.setCreatedAt(LocalDateTime.now());
        testTheme.setUpdatedAt(LocalDateTime.now());
        testTheme = themeRepository.save(testTheme);

        secondTestTheme = new Theme();
        secondTestTheme.setTitle("Spring");
        secondTestTheme.setDescription("Framework Spring");
        secondTestTheme.setCreatedAt(LocalDateTime.now());
        secondTestTheme.setUpdatedAt(LocalDateTime.now());
        secondTestTheme = themeRepository.save(secondTestTheme);

        // S'authentifier
        authToken = authenticateAndGetToken();
    }

    @Test
    @DisplayName("Doit créer un abonnement à un thème")
    void shouldCreateSubscription() throws Exception {
        SubscriptionRequest request = new SubscriptionRequest();
        request.setThemeId(testTheme.getId());

        mockMvc.perform(post("/api/subscriptions")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.themeId").value(testTheme.getId()))
                .andExpect(jsonPath("$.userId").exists()); // L'ID user dépend de l'ordre d'insertion
    }

    @Test
    @DisplayName("Doit obtenir les abonnements de l'utilisateur")
    void shouldGetUserSubscriptions() throws Exception {
        // Créer d'abord un abonnement via l'API
        SubscriptionRequest request = new SubscriptionRequest();
        request.setThemeId(testTheme.getId());

        mockMvc.perform(post("/api/subscriptions")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Puis vérifier qu'on peut le récupérer
        mockMvc.perform(get("/api/subscriptions")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].themeId").value(testTheme.getId()));
    }

    @Test
    @DisplayName("Doit supprimer un abonnement")
    void shouldDeleteSubscription() throws Exception {
        // Créer d'abord un abonnement via l'API
        SubscriptionRequest request = new SubscriptionRequest();
        request.setThemeId(testTheme.getId());

        mockMvc.perform(post("/api/subscriptions")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Puis le supprimer
        mockMvc.perform(delete("/api/subscriptions/" + testTheme.getId())
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Doit rejeter l'abonnement à un thème inexistant")
    void shouldRejectSubscriptionToNonExistentTheme() throws Exception {
        SubscriptionRequest request = new SubscriptionRequest();
        request.setThemeId(999L);

        mockMvc.perform(post("/api/subscriptions")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Doit empêcher les abonnements en double")
    void shouldPreventDuplicateSubscriptions() throws Exception {
        // Créer le premier abonnement
        SubscriptionRequest request = new SubscriptionRequest();
        request.setThemeId(testTheme.getId());

        mockMvc.perform(post("/api/subscriptions")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Essayer de créer le même abonnement
        mockMvc.perform(post("/api/subscriptions")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Doit retourner une liste vide pour un utilisateur sans abonnements")
    void shouldReturnEmptyListForUserWithoutSubscriptions() throws Exception {
        mockMvc.perform(get("/api/subscriptions")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Doit gérer correctement plusieurs abonnements d'utilisateur")
    void shouldHandleMultipleUserSubscriptionsCorrectly() throws Exception {
        // Créer deux abonnements via l'API
        SubscriptionRequest request1 = new SubscriptionRequest();
        request1.setThemeId(testTheme.getId());

        mockMvc.perform(post("/api/subscriptions")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        SubscriptionRequest request2 = new SubscriptionRequest();
        request2.setThemeId(secondTestTheme.getId());

        mockMvc.perform(post("/api/subscriptions")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/subscriptions")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("Doit maintenir la cohérence des abonnements lors d'opérations multiples")
    void shouldMaintainSubscriptionConsistencyDuringMultipleOperations() throws Exception {
        // Créer un abonnement
        SubscriptionRequest request = new SubscriptionRequest();
        request.setThemeId(testTheme.getId());

        mockMvc.perform(post("/api/subscriptions")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Vérifier qu'il existe
        mockMvc.perform(get("/api/subscriptions")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        // Le supprimer
        mockMvc.perform(delete("/api/subscriptions/" + testTheme.getId())
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNoContent());

        // Vérifier qu'il n'existe plus
        mockMvc.perform(get("/api/subscriptions")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Doit rejeter la création d'abonnement non authentifiée")
    void shouldRejectUnauthenticatedSubscriptionCreation() throws Exception {
        SubscriptionRequest request = new SubscriptionRequest();
        request.setThemeId(testTheme.getId());

        mockMvc.perform(post("/api/subscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Doit empêcher la suppression d'abonnement d'un autre utilisateur")
    void shouldPreventDeletionOfOtherUserSubscription() throws Exception {
        // Créer un autre utilisateur et un abonnement
        User secondUser = new User();
        secondUser.setEmail("seconduser@example.com");
        secondUser.setUsername("seconduser");
        secondUser.setPassword("hashedpassword");
        secondUser.setCreatedAt(LocalDateTime.now());
        secondUser.setUpdatedAt(LocalDateTime.now());
        secondUser = userRepository.save(secondUser);

        Subscription secondUserSubscription = new Subscription(secondUser, testTheme);
        subscriptionRepository.save(secondUserSubscription);

        // Essayer de supprimer l'abonnement de l'autre utilisateur
        mockMvc.perform(delete("/api/subscriptions/" + testTheme.getId())
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Doit rejeter la suppression d'abonnement inexistant")
    void shouldRejectDeletionOfNonExistentSubscription() throws Exception {
        // Nettoyer tous les abonnements
        subscriptionRepository.deleteAll();

        mockMvc.perform(delete("/api/subscriptions/" + testTheme.getId())
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNotFound());
    }

    /**
     * Authentifie un utilisateur et retourne le token JWT.
     */
    private String authenticateAndGetToken() throws Exception {
        // Inscription
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("subscription-integration@example.com");
        registerRequest.setUsername("subscriptionintegrationuser");
        registerRequest.setPassword("?Password1");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // Connexion
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("subscription-integration@example.com");
        loginRequest.setPassword("?Password1");

        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("token").asText();
    }
}
