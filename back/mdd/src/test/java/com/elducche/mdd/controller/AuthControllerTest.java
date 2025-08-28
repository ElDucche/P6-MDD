package com.elducche.mdd.controller;

import com.elducche.mdd.dto.LoginRequest;
import com.elducche.mdd.dto.RegisterRequest;
import com.elducche.mdd.entity.User;
import com.elducche.mdd.repository.UserRepository;
import com.elducche.mdd.util.BaseIntegrationTest;
import com.elducche.mdd.util.TestDataUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Tests d'intégration pour AuthController
 * 
 * Ces tests valident :
 * - Les endpoints d'authentification (register, login)
 * - La validation des données d'entrée
 * - Les réponses HTTP et JSON
 * - L'intégration avec Spring Security
 */
@DisplayName("Tests du contrôleur d'authentification")
class AuthControllerTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    private RegisterRequest validRegisterRequest;
    private LoginRequest validLoginRequest;
    private User existingUser;

    @BeforeEach
    void setUp() {
        // Créer un utilisateur existant avec TestDataUtil pour éviter les conflits
        existingUser = TestDataUtil.createUniqueUser();
        existingUser = userRepository.save(existingUser);

        // Préparer des données de test valides avec un utilisateur unique
        User tempUser = TestDataUtil.createUniqueUser();
        validRegisterRequest = new RegisterRequest();
        validRegisterRequest.setEmail(tempUser.getEmail());
        validRegisterRequest.setUsername(tempUser.getUsername());
        validRegisterRequest.setPassword("Password123!");

        validLoginRequest = new LoginRequest();
        validLoginRequest.setEmail(existingUser.getEmail());
        validLoginRequest.setPassword("Password123!");
    }

    @Test
    @DisplayName("POST /api/auth/register - Inscription réussie avec données valides")
    void testRegister_Success() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validRegisterRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.message", containsString("réussie")));
    }

    @Test
    @DisplayName("POST /api/auth/register - Échec avec email déjà utilisé")
    void testRegister_EmailAlreadyExists() throws Exception {
        // Given - Un utilisateur avec cet email existe déjà
        RegisterRequest duplicateEmailRequest = new RegisterRequest();
        duplicateEmailRequest.setEmail(existingUser.getEmail()); // Utiliser l'email de l'utilisateur existant
        duplicateEmailRequest.setUsername(TestDataUtil.createUniqueUser().getUsername()); // Username unique
        duplicateEmailRequest.setPassword("Password123!");

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(duplicateEmailRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("email")));
    }

    @Test
    @DisplayName("POST /api/auth/register - Échec avec username déjà utilisé")
    void testRegister_UsernameAlreadyExists() throws Exception {
        // Given - Un utilisateur avec ce username existe déjà
        RegisterRequest duplicateUsernameRequest = new RegisterRequest();
        duplicateUsernameRequest.setEmail(TestDataUtil.createUniqueUser().getEmail()); // Email unique
        duplicateUsernameRequest.setUsername(existingUser.getUsername()); // Utiliser le username de l'utilisateur existant
        duplicateUsernameRequest.setPassword("Password123!");

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(duplicateUsernameRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("nom d'utilisateur")));
    }

    @Test
    @DisplayName("POST /api/auth/register - Échec avec données invalides")
    void testRegister_InvalidData() throws Exception {
        // Given - Données invalides
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setEmail("invalid-email");
        invalidRequest.setUsername(""); // Vide
        invalidRequest.setPassword("123"); // Trop court

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/login - Connexion réussie avec identifiants valides")
    void testLogin_Success() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.message", containsString("réussie")));
    }

    @Test
    @DisplayName("POST /api/auth/login - Échec avec email inexistant")
    void testLogin_UserNotFound() throws Exception {
        // Given - Email inexistant (créer un email unique qui n'existe pas en base)
        User nonExistentUser = TestDataUtil.createUniqueUser();
        LoginRequest nonExistentUserRequest = new LoginRequest();
        nonExistentUserRequest.setEmail(nonExistentUser.getEmail()); // Email unique non sauvé
        nonExistentUserRequest.setPassword("Password123!");

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(nonExistentUserRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Identifiants invalides")));
    }

    @Test
    @DisplayName("POST /api/auth/login - Échec avec mot de passe incorrect")
    void testLogin_WrongPassword() throws Exception {
        // Given - Bon email, mauvais mot de passe
        LoginRequest wrongPasswordRequest = new LoginRequest();
        wrongPasswordRequest.setEmail(existingUser.getEmail()); // Email de l'utilisateur existant
        wrongPasswordRequest.setPassword("WrongPassword123!");

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(wrongPasswordRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Identifiants invalides")));
    }

    @Test
    @DisplayName("POST /api/auth/login - Échec avec données invalides")
    void testLogin_InvalidData() throws Exception {
        // Given - Données invalides
        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setEmail("invalid-email");
        invalidRequest.setPassword(""); // Vide

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/* - Échec avec Content-Type incorrect")
    void testAuth_WrongContentType() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.TEXT_PLAIN)
                .content("not json"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("POST /api/auth/* - Échec avec JSON malformé")
    void testAuth_MalformedJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json"))
                .andExpect(status().isBadRequest());
    }
}
