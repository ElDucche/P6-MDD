package com.elducche.mdd.integration;

import com.elducche.mdd.dto.LoginRequest;
import com.elducche.mdd.dto.RegisterRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Tests d'intégration de bout en bout pour l'authentification
 * 
 * Ces tests valident les scénarios complets d'authentification :
 * - Inscription complète d'un utilisateur
 * - Connexion avec JWT
 * - Validation des données
 * - Gestion des erreurs
 */
@DisplayName("Tests d'intégration - Authentification")
class AuthIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    private RegisterRequest validRegisterRequest;
    private LoginRequest validLoginRequest;

    @BeforeEach
    void setUp() {
        // Nettoyage de la base de données
        userRepository.deleteAll();

        // Préparation des données de test
        validRegisterRequest = new RegisterRequest();
        validRegisterRequest.setEmail("test.integration@example.com");
        validRegisterRequest.setUsername("testintegration");
        validRegisterRequest.setPassword("Password123!");

        validLoginRequest = new LoginRequest();
        validLoginRequest.setEmail("test.integration@example.com");
        validLoginRequest.setPassword("Password123!");
    }

    @Test
    @DisplayName("Scénario complet : Inscription + Connexion + Vérification utilisateur")
    void testCompleteAuthenticationFlow() throws Exception {
        // 1. Vérification qu'aucun utilisateur n'existe
        assertEquals(0, userRepository.count());

        // 2. Inscription réussie
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validRegisterRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", containsString("Inscription réussie")));

        // 3. Vérification que l'utilisateur a été créé en base
        assertEquals(1, userRepository.count());
        User createdUser = userRepository.findByEmail("test.integration@example.com").orElse(null);
        assertNotNull(createdUser);
        assertEquals("testintegration", createdUser.getUsername());
        assertTrue(passwordEncoder.matches("Password123!", createdUser.getPassword()));

        // 4. Connexion réussie avec l'utilisateur créé
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.user.email", is("test.integration@example.com")))
                .andExpect(jsonPath("$.user.username", is("testintegration")))
                .andExpect(jsonPath("$.user.password").doesNotExist()) // Vérification que le mot de passe n'est pas exposé
                .andReturn();

        // 5. Extraction et validation du token JWT
        String responseBody = loginResult.getResponse().getContentAsString();
        assertTrue(responseBody.contains("token"));
        assertTrue(responseBody.contains("user"));
    }

    @Test
    @DisplayName("Scénario d'erreur : Inscription avec email déjà utilisé")
    void testRegistrationWithDuplicateEmail() throws Exception {
        // 1. Première inscription réussie
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validRegisterRequest)))
                .andExpect(status().isCreated());

        // 2. Tentative d'inscription avec le même email
        RegisterRequest duplicateRequest = new RegisterRequest();
        duplicateRequest.setEmail("test.integration@example.com");
        duplicateRequest.setUsername("differentusername");
        duplicateRequest.setPassword("Password123!");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(duplicateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Un compte avec cet email existe déjà")));

        // 3. Vérification qu'un seul utilisateur existe toujours
        assertEquals(1, userRepository.count());
    }

    @Test
    @DisplayName("Scénario d'erreur : Connexion avec identifiants incorrects")
    void testLoginWithInvalidCredentials() throws Exception {
        // 1. Création d'un utilisateur
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validRegisterRequest)))
                .andExpect(status().isCreated());

        // 2. Tentative de connexion avec un mauvais mot de passe
        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setEmail("test.integration@example.com");
        invalidRequest.setPassword("WrongPassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(invalidRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Identifiants invalides")));
    }

    @Test
    @DisplayName("Validation des données d'inscription")
    void testRegistrationValidation() throws Exception {
        // Test avec email invalide
        RegisterRequest invalidEmailRequest = new RegisterRequest();
        invalidEmailRequest.setEmail("invalid-email");
        invalidEmailRequest.setUsername("testuser");
        invalidEmailRequest.setPassword("Password123!");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(invalidEmailRequest)))
                .andExpect(status().isBadRequest());

        // Test avec mot de passe trop faible
        RegisterRequest weakPasswordRequest = new RegisterRequest();
        weakPasswordRequest.setEmail("test@example.com");
        weakPasswordRequest.setUsername("testuser");
        weakPasswordRequest.setPassword("weak");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(weakPasswordRequest)))
                .andExpect(status().isBadRequest());

        // Vérification qu'aucun utilisateur n'a été créé
        assertEquals(0, userRepository.count());
    }

    @Test
    @DisplayName("Gestion des caractères spéciaux dans les données")
    void testSpecialCharactersHandling() throws Exception {
        RegisterRequest specialCharsRequest = new RegisterRequest();
        specialCharsRequest.setEmail("test.spécial+tag@example.com");
        specialCharsRequest.setUsername("user_spécial-123");
        specialCharsRequest.setPassword("Password123!@#");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(specialCharsRequest)))
                .andExpect(status().isCreated());

        // Vérification que l'utilisateur a été créé avec les caractères spéciaux
        User createdUser = userRepository.findByEmail("test.spécial+tag@example.com").orElse(null);
        assertNotNull(createdUser);
        assertEquals("user_spécial-123", createdUser.getUsername());
    }
}
