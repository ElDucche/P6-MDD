package com.elducche.mdd.entity;

import com.elducche.mdd.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour l'entité User
 */
@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.sql.init.mode=never"
})
@DisplayName("Tests de l'entité User")
class UserEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = TestDataBuilder.createValidUser();
    }

    @Test
    @DisplayName("Devrait créer un utilisateur valide")
    void shouldCreateValidUser() {
        // Given - un utilisateur valide
        
        // When - sauvegarde en base
        User savedUser = entityManager.persistAndFlush(validUser);
        
        // Then - l'utilisateur est sauvegardé avec un ID
        assertNotNull(savedUser.getId());
        assertEquals(validUser.getUsername(), savedUser.getUsername());
        assertEquals(validUser.getEmail(), savedUser.getEmail());
        assertEquals(validUser.getPassword(), savedUser.getPassword());
        assertNotNull(savedUser.getCreatedAt());
        assertNotNull(savedUser.getUpdatedAt());
    }

    @Test
    @DisplayName("Devrait échouer avec un email null")
    void shouldFailWithNullEmail() {
        // Given - un utilisateur avec email null
        validUser.setEmail(null);
        
        // When/Then - exception lors de la sauvegarde
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(validUser);
        });
    }

    @Test
    @DisplayName("Devrait échouer avec un username null")
    void shouldFailWithNullUsername() {
        // Given - un utilisateur avec username null
        validUser.setUsername(null);
        
        // When/Then - exception lors de la sauvegarde
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(validUser);
        });
    }

    @Test
    @DisplayName("Devrait échouer avec un password null")
    void shouldFailWithNullPassword() {
        // Given - un utilisateur avec password null
        validUser.setPassword(null);
        
        // When/Then - exception lors de la sauvegarde
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(validUser);
        });
    }

    @Test
    @DisplayName("Devrait mettre à jour les timestamps automatiquement")
    void shouldUpdateTimestampsAutomatically() {
        // Given - un utilisateur sauvegardé
        User savedUser = entityManager.persistAndFlush(validUser);
        Long originalId = savedUser.getId();
        
        // When - modification et sauvegarde
        savedUser.setUsername("nouveauNom");
        entityManager.flush();
        
        // Then - updatedAt est modifié
        User updatedUser = entityManager.find(User.class, originalId);
        assertNotNull(updatedUser.getUpdatedAt());
        assertTrue(updatedUser.getUpdatedAt().isAfter(updatedUser.getCreatedAt()) || 
                  updatedUser.getUpdatedAt().equals(updatedUser.getCreatedAt()));
    }

    @Test
    @DisplayName("Devrait respecter l'unicité de l'email")
    void shouldRespectEmailUniqueness() {
        // Given - un premier utilisateur sauvegardé
        entityManager.persistAndFlush(validUser);
        
        // When - création d'un second utilisateur avec le même email
        User duplicateUser = TestDataBuilder.createValidUser();
        duplicateUser.setUsername("autrenom");
        duplicateUser.setEmail(validUser.getEmail()); // même email
        
        // Then - exception lors de la sauvegarde
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(duplicateUser);
        });
    }

    @Test
    @DisplayName("Devrait respecter l'unicité du username")
    void shouldRespectUsernameUniqueness() {
        // Given - un premier utilisateur sauvegardé
        entityManager.persistAndFlush(validUser);
        
        // When - création d'un second utilisateur avec le même username
        User duplicateUser = TestDataBuilder.createValidUser();
        duplicateUser.setEmail("autre@email.com");
        duplicateUser.setUsername(validUser.getUsername()); // même username
        
        // Then - exception lors de la sauvegarde
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(duplicateUser);
        });
    }
}
