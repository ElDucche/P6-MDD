package com.elducche.mdd;

import com.elducche.mdd.util.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test de vérification de la configuration de test
 * 
 * Ce test vérifie que l'application Spring Boot se charge correctement
 * dans l'environnement de test.
 */
@SpringBootTest
@ActiveProfiles("test")
class MddApplicationTests extends BaseIntegrationTest {

    @Autowired
    private Environment environment;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Test
    void contextLoads() {
        // Vérifie que le contexte Spring se charge correctement
        assertNotNull(webApplicationContext);
        assertNotNull(mockMvc);
        assertNotNull(objectMapper);
        assertNotNull(passwordEncoder);
    }

    @Test
    void applicationPropertiesAreLoaded() {
        // Vérifie que les propriétés de test sont chargées
        assertNotNull(environment);
        assertNotNull(jwtSecret);
        assertTrue(environment.acceptsProfiles("test"));
    }
}
