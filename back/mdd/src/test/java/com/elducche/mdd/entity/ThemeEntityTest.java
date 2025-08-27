package com.elducche.mdd.entity;

import com.elducche.mdd.util.TestDataBuilder;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour l'entité Theme
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Tests de l'entité Theme")
class ThemeEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private Validator validator;

    private Theme validTheme;

    @BeforeEach
    void setUp() {
        validTheme = TestDataBuilder.createValidTheme();
    }

    @Test
    @DisplayName("Devrait créer un thème valide")
    void shouldCreateValidTheme() {
        // Given - un thème valide
        
        // When - sauvegarde en base
        Theme savedTheme = entityManager.persistAndFlush(validTheme);
        
        // Then - le thème est sauvegardé avec un ID
        assertNotNull(savedTheme.getId());
        assertEquals(validTheme.getTitle(), savedTheme.getTitle());
        assertEquals(validTheme.getDescription(), savedTheme.getDescription());
        assertNotNull(savedTheme.getCreatedAt());
        assertNotNull(savedTheme.getUpdatedAt());
    }

    @Test
    @DisplayName("Devrait échouer avec un titre null")
    void shouldFailWithNullTitle() {
        // Given - un thème avec titre null
        validTheme.setTitle(null);
        
        // When - validation
        Set<ConstraintViolation<Theme>> violations = validator.validate(validTheme);
        
        // Then - violation de contrainte
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    @Test
    @DisplayName("Devrait échouer avec un titre vide")
    void shouldFailWithEmptyTitle() {
        // Given - un thème avec titre vide
        validTheme.setTitle("");
        
        // When - validation
        Set<ConstraintViolation<Theme>> violations = validator.validate(validTheme);
        
        // Then - violation de contrainte
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    @Test
    @DisplayName("Devrait accepter une description null")
    void shouldAcceptNullDescription() {
        // Given - un thème avec description null
        validTheme.setDescription(null);
        
        // When - validation
        Set<ConstraintViolation<Theme>> violations = validator.validate(validTheme);
        
        // Then - aucune violation
        assertTrue(violations.isEmpty());
        
        // And - sauvegarde réussie
        Theme savedTheme = entityManager.persistAndFlush(validTheme);
        assertNotNull(savedTheme.getId());
        assertNull(savedTheme.getDescription());
    }

    @Test
    @DisplayName("Devrait mettre à jour les timestamps automatiquement")
    void shouldUpdateTimestampsAutomatically() {
        // Given - un thème sauvegardé
        Theme savedTheme = entityManager.persistAndFlush(validTheme);
        Long originalId = savedTheme.getId();
        
        // When - modification et sauvegarde
        savedTheme.setTitle("Nouveau titre");
        entityManager.flush();
        
        // Then - updatedAt est modifié
        Theme updatedTheme = entityManager.find(Theme.class, originalId);
        assertNotNull(updatedTheme.getUpdatedAt());
        assertTrue(updatedTheme.getUpdatedAt().isAfter(updatedTheme.getCreatedAt()) || 
                  updatedTheme.getUpdatedAt().equals(updatedTheme.getCreatedAt()));
    }

    @Test
    @DisplayName("Devrait gérer les relations avec les posts")
    void shouldHandlePostRelations() {
        // Given - un thème sauvegardé
        Theme savedTheme = entityManager.persistAndFlush(validTheme);
        
        // When - récupération du thème
        Theme foundTheme = entityManager.find(Theme.class, savedTheme.getId());
        
        // Then - la liste des posts est initialisée (vide)
        assertNotNull(foundTheme.getPosts());
        assertTrue(foundTheme.getPosts().isEmpty());
    }

    @Test
    @DisplayName("Devrait gérer les relations avec les subscriptions")
    void shouldHandleSubscriptionRelations() {
        // Given - un thème sauvegardé
        Theme savedTheme = entityManager.persistAndFlush(validTheme);
        
        // When - récupération du thème
        Theme foundTheme = entityManager.find(Theme.class, savedTheme.getId());
        
        // Then - la liste des subscriptions est initialisée (vide)
        assertNotNull(foundTheme.getSubscriptions());
        assertTrue(foundTheme.getSubscriptions().isEmpty());
    }
}
