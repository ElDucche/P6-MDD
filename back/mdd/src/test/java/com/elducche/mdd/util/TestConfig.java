package com.elducche.mdd.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

/**
 * Configuration de base pour les tests
 * 
 * Cette classe fournit des beans et configurations
 * communes à tous les tests.
 */
@TestConfiguration
@ActiveProfiles("test")
public class TestConfig {

    /**
     * ObjectMapper pour sérialiser/désérialiser les objets JSON dans les tests
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    /**
     * Encodeur de mot de passe pour les tests
     * Utilise un coût faible pour accélérer les tests
     */
    @Bean
    @Primary
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(4); // Coût réduit pour les tests
    }
}
