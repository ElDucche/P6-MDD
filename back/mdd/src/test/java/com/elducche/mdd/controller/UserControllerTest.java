package com.elducche.mdd.controller;

import com.elducche.mdd.dto.UpdateUserProfileRequest;
import com.elducche.mdd.entity.User;
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
 * Tests d'intégration pour UserController
 * 
 * Ces tests valident :
 * - L'accès au profil utilisateur
 * - La modification du profil
 * - Les autorisations et sécurité
 */
@DisplayName("Tests du contrôleur utilisateur")
class UserControllerTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private UpdateUserProfileRequest validUpdateRequest;

    @BeforeEach
    void setUp() {
        // Créer un utilisateur de test
        testUser = new User();
        testUser.setEmail("user@example.com");
        testUser.setUsername("testuser");
        testUser.setPassword(encodePassword("Password123!"));
        testUser = userRepository.save(testUser);

        // Préparer une requête de mise à jour valide
        validUpdateRequest = new UpdateUserProfileRequest();
        validUpdateRequest.setUsername("newusername");
        validUpdateRequest.setEmail("newemail@example.com");
    }

    @Test
    @DisplayName("GET /api/user/me - Récupération du profil utilisateur connecté")
    @WithMockUser
    void testGetCurrentUser_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/user/me")
                .with(SecurityTestUtils.authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testUser.getId().intValue())))
                .andExpect(jsonPath("$.email", is(testUser.getEmail())))
                .andExpect(jsonPath("$.username", is(testUser.getUsername())))
                .andExpect(jsonPath("$.password").doesNotExist()); // Le mot de passe ne doit pas être exposé
    }

    @Test
    @DisplayName("GET /api/user/me - Échec sans authentification")
    void testGetCurrentUser_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/user/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PUT /api/user/me - Mise à jour du profil réussie")
    @WithMockUser
    void testUpdateCurrentUser_Success() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/user/me")
                .with(SecurityTestUtils.authenticatedUser(testUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email", is(validUpdateRequest.getEmail())))
                .andExpect(jsonPath("$.username", is(validUpdateRequest.getUsername())));
    }

    @Test
    @DisplayName("PUT /api/user/me - Échec avec email déjà utilisé")
    @WithMockUser
    void testUpdateCurrentUser_EmailAlreadyExists() throws Exception {
        // Given - Créer un autre utilisateur avec l'email qu'on veut utiliser
        User otherUser = new User();
        otherUser.setEmail("existing@example.com");
        otherUser.setUsername("otheruser");
        otherUser.setPassword(encodePassword("Password123!"));
        userRepository.save(otherUser);

        // Essayer de mettre à jour avec cet email
        validUpdateRequest.setEmail("existing@example.com");

        // When & Then
        mockMvc.perform(put("/api/user/me")
                .with(SecurityTestUtils.authenticatedUser(testUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validUpdateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Email déjà utilisé")));
    }

    @Test
    @DisplayName("PUT /api/user/me - Échec avec username déjà utilisé")
    @WithMockUser
    void testUpdateCurrentUser_UsernameAlreadyExists() throws Exception {
        // Given - Créer un autre utilisateur avec le username qu'on veut utiliser
        User otherUser = new User();
        otherUser.setEmail("other@example.com");
        otherUser.setUsername("existinguser");
        otherUser.setPassword(encodePassword("Password123!"));
        userRepository.save(otherUser);

        // Essayer de mettre à jour avec ce username
        validUpdateRequest.setUsername("existinguser");

        // When & Then
        mockMvc.perform(put("/api/user/me")
                .with(SecurityTestUtils.authenticatedUser(testUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validUpdateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("nom d'utilisateur")));
    }

    @Test
    @DisplayName("PUT /api/user/me - Échec avec données invalides")
    @WithMockUser
    void testUpdateCurrentUser_InvalidData() throws Exception {
        // Given - Données invalides
        UpdateUserProfileRequest invalidRequest = new UpdateUserProfileRequest();
        invalidRequest.setEmail("invalid-email");
        invalidRequest.setUsername(""); // Vide

        // When & Then
        mockMvc.perform(put("/api/user/me")
                .with(SecurityTestUtils.authenticatedUser(testUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/user/me - Échec sans authentification")
    void testUpdateCurrentUser_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/user/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validUpdateRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/user/{id} - Récupération d'un utilisateur par ID")
    void testGetUserById_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/user/{id}", testUser.getId())
                .with(SecurityTestUtils.authenticatedUser(testUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testUser.getId().intValue())))
                .andExpect(jsonPath("$.email", is(testUser.getEmail())))
                .andExpect(jsonPath("$.username", is(testUser.getUsername())))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("GET /api/user/{id} - Échec avec ID inexistant")
    void testGetUserById_NotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/user/{id}", 99999L)
                .with(SecurityTestUtils.authenticatedUser(testUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/user/{id} - Échec sans authentification")
    void testGetUserById_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/user/{id}", testUser.getId()))
                .andExpect(status().isUnauthorized());
    }
}
