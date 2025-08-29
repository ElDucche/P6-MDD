package com.elducche.mdd.integration;

import com.elducche.mdd.dto.LoginRequest;
import com.elducche.mdd.dto.RegisterRequest;
import com.elducche.mdd.dto.UpdateUserProfileRequest;
import com.elducche.mdd.entity.User;
import com.elducche.mdd.repository.UserRepository;
import com.elducche.mdd.util.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Tests d'intégration de bout en bout pour la gestion des utilisateurs
 * 
 * Ces tests valident les scénarios complets de gestion de profil :
 * - Récupération du profil utilisateur
 * - Mise à jour du profil
 * - Gestion de l'authentification
 * - Validation des données
 */
@DisplayName("Tests d'intégration - Gestion des utilisateurs")
class UserIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    private String authToken;
    private User testUser;

    @BeforeEach
    void setUp() throws Exception {
        // Nettoyage de la base de données
        userRepository.deleteAll();

        // Création et inscription d'un utilisateur de test
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("user.integration@example.com");
        registerRequest.setUsername("userintegration");
        registerRequest.setPassword("Password123!");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(registerRequest)))
                .andExpect(status().isCreated());

        // Connexion pour obtenir le token
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("user.integration@example.com");
        loginRequest.setPassword("Password123!");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // Extraction du token
        String responseBody = loginResult.getResponse().getContentAsString();
        authToken = extractTokenFromResponse(responseBody);

        // Récupération de l'utilisateur créé
        testUser = userRepository.findByEmail("user.integration@example.com").orElse(null);
        assertNotNull(testUser);
    }

    @Test
    @DisplayName("Scénario complet : Récupération et mise à jour du profil utilisateur")
    void testCompleteUserProfileFlow() throws Exception {
        // 1. Récupération du profil utilisateur
        mockMvc.perform(get("/api/user/me")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email", is("user.integration@example.com")))
                .andExpect(jsonPath("$.username", is("userintegration")))
                .andExpect(jsonPath("$.password").doesNotExist()); // Vérification que le mot de passe n'est pas exposé

        // 2. Mise à jour du profil
        UpdateUserProfileRequest updateRequest = new UpdateUserProfileRequest();
        updateRequest.setUsername("updatedusername");
        updateRequest.setEmail("updated.email@example.com");

        mockMvc.perform(put("/api/user/me")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email", is("updated.email@example.com")))
                .andExpect(jsonPath("$.username", is("updatedusername")));

        // 3. Vérification en base de données
        User updatedUser = userRepository.findById(testUser.getId()).orElse(null);
        assertNotNull(updatedUser);
        assertEquals("updated.email@example.com", updatedUser.getEmail());
        assertEquals("updatedusername", updatedUser.getUsername());

        // 4. Nouvelle récupération pour confirmer les changements
        mockMvc.perform(get("/api/user/me")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("updated.email@example.com")))
                .andExpect(jsonPath("$.username", is("updatedusername")));
    }

    @Test
    @DisplayName("Tentative d'accès sans authentification")
    void testAccessWithoutAuthentication() throws Exception {
        // Tentative de récupération du profil sans token
        mockMvc.perform(get("/api/user/me"))
                .andExpect(status().isForbidden());

        // Tentative de mise à jour sans token
        UpdateUserProfileRequest updateRequest = new UpdateUserProfileRequest();
        updateRequest.setUsername("newusername");

        mockMvc.perform(put("/api/user/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updateRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Tentative d'accès avec token invalide")
    void testAccessWithInvalidToken() throws Exception {
        String invalidToken = "invalid.token.here";

        mockMvc.perform(get("/api/user/me")
                .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Validation des données de mise à jour")
    void testUpdateValidation() throws Exception {
        // Test avec email invalide
        UpdateUserProfileRequest invalidEmailRequest = new UpdateUserProfileRequest();
        invalidEmailRequest.setEmail("invalid-email");
        invalidEmailRequest.setUsername("validusername");

        mockMvc.perform(put("/api/user/me")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(invalidEmailRequest)))
                .andExpect(status().isBadRequest());

        // Test avec username vide
        UpdateUserProfileRequest emptyUsernameRequest = new UpdateUserProfileRequest();
        emptyUsernameRequest.setEmail("valid@example.com");
        emptyUsernameRequest.setUsername("");

        mockMvc.perform(put("/api/user/me")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(emptyUsernameRequest)))
                .andExpect(status().isBadRequest());

        // Vérification que l'utilisateur n'a pas été modifié
        User unchangedUser = userRepository.findById(testUser.getId()).orElse(null);
        assertNotNull(unchangedUser);
        assertEquals("user.integration@example.com", unchangedUser.getEmail());
        assertEquals("userintegration", unchangedUser.getUsername());
    }

    @Test
    @DisplayName("Tentative de mise à jour avec email déjà utilisé")
    void testUpdateWithDuplicateEmail() throws Exception {
        // Création d'un autre utilisateur
        User otherUser = new User();
        otherUser.setEmail("other@example.com");
        otherUser.setUsername("otheruser");
        otherUser.setPassword(encodePassword("Password123!"));
        userRepository.save(otherUser);

        // Tentative de mise à jour avec l'email de l'autre utilisateur
        UpdateUserProfileRequest duplicateEmailRequest = new UpdateUserProfileRequest();
        duplicateEmailRequest.setEmail("other@example.com");
        duplicateEmailRequest.setUsername("newusername");

        mockMvc.perform(put("/api/user/me")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(duplicateEmailRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Email déjà utilisé")));

        // Vérification que l'utilisateur n'a pas été modifié
        User unchangedUser = userRepository.findById(testUser.getId()).orElse(null);
        assertNotNull(unchangedUser);
        assertEquals("user.integration@example.com", unchangedUser.getEmail());
    }

    @Test
    @DisplayName("Gestion des caractères spéciaux dans la mise à jour")
    void testUpdateWithSpecialCharacters() throws Exception {
        UpdateUserProfileRequest specialCharsRequest = new UpdateUserProfileRequest();
        specialCharsRequest.setEmail("test.spécial+tag@example.com");
        specialCharsRequest.setUsername("user_spécial-123");

        mockMvc.perform(put("/api/user/me")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(specialCharsRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("test.spécial+tag@example.com")))
                .andExpect(jsonPath("$.username", is("user_spécial-123")));

        // Vérification en base
        User updatedUser = userRepository.findById(testUser.getId()).orElse(null);
        assertNotNull(updatedUser);
        assertEquals("test.spécial+tag@example.com", updatedUser.getEmail());
        assertEquals("user_spécial-123", updatedUser.getUsername());
    }

    /**
     * Utilitaire pour extraire le token JWT de la réponse de connexion
     */
    private String extractTokenFromResponse(String responseBody) {
        // Cette méthode peut être améliorée avec Jackson pour parser le JSON
        // Pour l'instant, une extraction simple
        int tokenStart = responseBody.indexOf("\"token\":\"") + 9;
        int tokenEnd = responseBody.indexOf("\"", tokenStart);
        return responseBody.substring(tokenStart, tokenEnd);
    }
}
