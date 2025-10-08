package com.elducche.mdd.util;

import com.elducche.mdd.config.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

/**
 * Classe de base pour les tests d'intégration
 * 
 * Cette classe fournit la configuration commune à tous les tests d'intégration :
 * - Configuration Spring Boot complète
 * - MockMvc configuré avec Spring Security
 * - ObjectMapper pour JSON
 * - Encodeur de mot de passe
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import(TestSecurityConfig.class)
public abstract class BaseIntegrationTest {

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    protected MockMvc mockMvc;

    @BeforeEach
    void setUpBaseIntegrationTest() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    /**
     * Convertit un objet en JSON
     */
    protected String asJsonString(Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }

    /**
     * Convertit un JSON en objet
     */
    protected <T> T fromJsonString(String json, Class<T> clazz) throws Exception {
        return objectMapper.readValue(json, clazz);
    }

    /**
     * Encode un mot de passe pour les tests
     */
    protected String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * Extrait le cookie authToken d'une réponse MockMvc
     */
    protected jakarta.servlet.http.Cookie getAuthCookie(org.springframework.test.web.servlet.MvcResult result) {
        jakarta.servlet.http.Cookie[] cookies = result.getResponse().getCookies();
        if (cookies != null) {
            for (jakarta.servlet.http.Cookie cookie : cookies) {
                if ("authToken".equals(cookie.getName())) {
                    return cookie;
                }
            }
        }
        return null;
    }

    /**
     * Crée et authentifie un utilisateur de test, retourne le cookie d'authentification
     */
    protected jakarta.servlet.http.Cookie authenticateTestUser(String email, String username, String password) throws Exception {
        com.elducche.mdd.dto.RegisterRequest registerRequest = new com.elducche.mdd.dto.RegisterRequest();
        registerRequest.setEmail(email);
        registerRequest.setUsername(username);
        registerRequest.setPassword(password);

        org.springframework.test.web.servlet.MvcResult result = mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(asJsonString(registerRequest)))
                .andReturn();

        return getAuthCookie(result);
    }
}
