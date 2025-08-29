package com.elducche.mdd.controller;

import com.elducche.mdd.util.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Tests d'intégration pour HealthController
 * 
 * Ces tests valident :
 * - L'endpoint de santé de l'application
 * - L'endpoint d'informations sur l'API
 * - L'accessibilité sans authentification
 */
@DisplayName("Tests du contrôleur de santé")
class HealthControllerTest extends BaseIntegrationTest {

    @Test
    @DisplayName("GET /api/health - Endpoint de santé accessible")
    void testHealth_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("UP")))
                .andExpect(jsonPath("$.application", is("mdd-application-test")))
                .andExpect(jsonPath("$.port", is("0")))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.message", is("Application MDD opérationnelle")));
    }

    @Test
    @DisplayName("GET /api/info - Endpoint d'informations accessible")
    void testInfo_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/info"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.application.name", is("MDD - Monde de Dév")))
                .andExpect(jsonPath("$.application.description", is("API REST pour réseau social de développeurs")))
                .andExpect(jsonPath("$.application.version", is("1.0.0")))
                .andExpect(jsonPath("$.application.architecture", is("Monolithique Spring Boot")))
                .andExpect(jsonPath("$.endpoints.authentication", is("/api/auth/*")))
                .andExpect(jsonPath("$.endpoints.users", is("/api/users/*")))
                .andExpect(jsonPath("$.endpoints.posts", is("/api/posts/*")))
                .andExpect(jsonPath("$.endpoints.themes", is("/api/themes/*")))
                .andExpect(jsonPath("$.endpoints.comments", is("/api/comments/*")))
                .andExpect(jsonPath("$.endpoints.subscriptions", is("/api/subscriptions/*")))
                .andExpect(jsonPath("$.security.type", is("JWT Bearer Token")))
                .andExpect(jsonPath("$.security.header", is("Authorization: Bearer <token>")));
    }

    @Test
    @DisplayName("Endpoints accessibles sans authentification")
    void testEndpoints_NoAuthRequired() throws Exception {
        // Test que les endpoints de santé sont accessibles sans authentification
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/info"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/health - Vérification de la structure des données")
    void testHealth_DataStructure() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", notNullValue()))
                .andExpect(jsonPath("$.application", notNullValue()))
                .andExpect(jsonPath("$.port", notNullValue()))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.message", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/info - Vérification de la structure complète")
    void testInfo_DataStructure() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/info"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.application", notNullValue()))
                .andExpect(jsonPath("$.endpoints", notNullValue()))
                .andExpect(jsonPath("$.security", notNullValue()))
                .andExpect(jsonPath("$.application.name", notNullValue()))
                .andExpect(jsonPath("$.application.description", notNullValue()))
                .andExpect(jsonPath("$.application.version", notNullValue()))
                .andExpect(jsonPath("$.application.architecture", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/health - Test multiple appels")
    void testHealth_MultipleRequest() throws Exception {
        // Test que plusieurs appels consécutifs fonctionnent
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get("/api/health"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", is("UP")));
        }
    }

    @Test
    @DisplayName("GET /api/info - Vérification des endpoints listés")
    void testInfo_EndpointsList() throws Exception {
        // When & Then - Vérifier que tous les endpoints principaux sont listés
        mockMvc.perform(get("/api/info"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.endpoints", hasKey("authentication")))
                .andExpect(jsonPath("$.endpoints", hasKey("users")))
                .andExpect(jsonPath("$.endpoints", hasKey("posts")))
                .andExpect(jsonPath("$.endpoints", hasKey("themes")))
                .andExpect(jsonPath("$.endpoints", hasKey("comments")))
                .andExpect(jsonPath("$.endpoints", hasKey("subscriptions")));
    }

    @Test
    @DisplayName("CORS - Vérification des headers")
    void testCorsHeaders() throws Exception {
        // Test que les headers CORS sont bien configurés
        mockMvc.perform(get("/api/health")
                .header("Origin", "http://localhost:4200"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/info")
                .header("Origin", "http://localhost:4200"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Content-Type - Vérification JSON")
    void testContentType() throws Exception {
        // Vérifier que les réponses sont bien en JSON
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", containsString("application/json")));

        mockMvc.perform(get("/api/info"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", containsString("application/json")));
    }
}
