package com.elducche.mdd.entity;

import com.elducche.mdd.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour l'entité Subscription
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Tests de l'entité Subscription")
class SubscriptionEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    private Subscription validSubscription;
    private User user;
    private Theme theme;

    @BeforeEach
    void setUp() {
        // Préparation des entités liées
        user = TestDataBuilder.createValidUser();
        theme = TestDataBuilder.createValidTheme();
        
        // Sauvegarde des entités parentes
        user = entityManager.persistAndFlush(user);
        theme = entityManager.persistAndFlush(theme);
        
        // Création de la subscription valide
        validSubscription = TestDataBuilder.createValidSubscription();
        validSubscription.setUser(user);
        validSubscription.setTheme(theme);
    }

    @Test
    @DisplayName("Devrait créer une subscription valide")
    void shouldCreateValidSubscription() {
        // Given - une subscription valide avec relations
        
        // When - sauvegarde en base
        Subscription savedSubscription = entityManager.persistAndFlush(validSubscription);
        
        // Then - la subscription est sauvegardée
        assertNotNull(savedSubscription);
        assertEquals(user.getId(), savedSubscription.getUser().getId());
        assertEquals(theme.getId(), savedSubscription.getTheme().getId());
        assertNotNull(savedSubscription.getSubscribedAt());
    }

    @Test
    @DisplayName("Devrait échouer sans utilisateur")
    void shouldFailWithoutUser() {
        // Given - une subscription sans utilisateur
        validSubscription.setUser(null);
        
        // When - tentative de sauvegarde
        // Then - exception de contrainte de base de données
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(validSubscription);
        });
    }

    @Test
    @DisplayName("Devrait échouer sans thème")
    void shouldFailWithoutTheme() {
        // Given - une subscription sans thème
        validSubscription.setTheme(null);
        
        // When - tentative de sauvegarde
        // Then - exception de contrainte de base de données
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(validSubscription);
        });
    }

    @Test
    @DisplayName("Devrait respecter l'unicité de la clé composite")
    void shouldRespectCompositeKeyUniqueness() {
        // Given - une première subscription sauvegardée
        entityManager.persistAndFlush(validSubscription);
        
        // When - création d'une seconde subscription avec les mêmes user/theme
        Subscription duplicateSubscription = TestDataBuilder.createValidSubscription();
        duplicateSubscription.setUser(user);
        duplicateSubscription.setTheme(theme);
        
        // Then - exception lors de la sauvegarde
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(duplicateSubscription);
        });
    }

    @Test
    @DisplayName("Devrait permettre au même utilisateur de s'abonner à plusieurs thèmes")
    void shouldAllowSameUserToSubscribeToMultipleThemes() {
        // Given - une première subscription sauvegardée
        entityManager.persistAndFlush(validSubscription);
        
        // When - création d'un second thème et subscription
        Theme secondTheme = TestDataBuilder.createValidTheme();
        secondTheme.setTitle("Deuxième thème");
        secondTheme = entityManager.persistAndFlush(secondTheme);
        
        Subscription secondSubscription = TestDataBuilder.createValidSubscription();
        secondSubscription.setUser(user);
        secondSubscription.setTheme(secondTheme);
        
        // Then - la seconde subscription peut être sauvegardée
        Subscription savedSecondSubscription = entityManager.persistAndFlush(secondSubscription);
        assertNotNull(savedSecondSubscription);
        assertEquals(user.getId(), savedSecondSubscription.getUser().getId());
        assertEquals(secondTheme.getId(), savedSecondSubscription.getTheme().getId());
    }

    @Test
    @DisplayName("Devrait permettre à plusieurs utilisateurs de s'abonner au même thème")
    void shouldAllowMultipleUsersToSubscribeToSameTheme() {
        // Given - une première subscription sauvegardée
        entityManager.persistAndFlush(validSubscription);
        
        // When - création d'un second utilisateur et subscription
        User secondUser = TestDataBuilder.createValidUser();
        secondUser.setUsername("secondUser");
        secondUser.setEmail("second@user.com");
        secondUser = entityManager.persistAndFlush(secondUser);
        
        Subscription secondSubscription = TestDataBuilder.createValidSubscription();
        secondSubscription.setUser(secondUser);
        secondSubscription.setTheme(theme);
        
        // Then - la seconde subscription peut être sauvegardée
        Subscription savedSecondSubscription = entityManager.persistAndFlush(secondSubscription);
        assertNotNull(savedSecondSubscription);
        assertEquals(secondUser.getId(), savedSecondSubscription.getUser().getId());
        assertEquals(theme.getId(), savedSecondSubscription.getTheme().getId());
    }

    @Test
    @DisplayName("Devrait maintenir l'intégrité référentielle avec l'utilisateur")
    void shouldMaintainReferentialIntegrityWithUser() {
        // Given - une subscription sauvegardée
        Subscription savedSubscription = entityManager.persistAndFlush(validSubscription);
        
        // When - récupération avec l'utilisateur
        entityManager.clear(); // Clear cache pour forcer le chargement depuis la DB
        Subscription foundSubscription = entityManager.find(Subscription.class, 
            new SubscriptionId(user.getId(), theme.getId()));
        
        // Then - l'utilisateur est correctement chargé
        assertNotNull(foundSubscription);
        assertNotNull(foundSubscription.getUser());
        assertEquals(user.getId(), foundSubscription.getUser().getId());
        assertEquals(user.getUsername(), foundSubscription.getUser().getUsername());
    }

    @Test
    @DisplayName("Devrait maintenir l'intégrité référentielle avec le thème")
    void shouldMaintainReferentialIntegrityWithTheme() {
        // Given - une subscription sauvegardée
        Subscription savedSubscription = entityManager.persistAndFlush(validSubscription);
        
        // When - récupération avec le thème
        entityManager.clear(); // Clear cache pour forcer le chargement depuis la DB
        Subscription foundSubscription = entityManager.find(Subscription.class, 
            new SubscriptionId(user.getId(), theme.getId()));
        
        // Then - le thème est correctement chargé
        assertNotNull(foundSubscription);
        assertNotNull(foundSubscription.getTheme());
        assertEquals(theme.getId(), foundSubscription.getTheme().getId());
        assertEquals(theme.getTitle(), foundSubscription.getTheme().getTitle());
    }

    @Test
    @DisplayName("Devrait gérer la suppression en cascade")
    void shouldHandleCascadeDeletion() {
        // Given - une subscription sauvegardée
        Subscription savedSubscription = entityManager.persistAndFlush(validSubscription);
        SubscriptionId subscriptionId = new SubscriptionId(user.getId(), theme.getId());
        
        // When - suppression de la subscription
        entityManager.remove(savedSubscription);
        entityManager.flush();
        
        // Then - la subscription n'existe plus
        Subscription foundSubscription = entityManager.find(Subscription.class, subscriptionId);
        assertNull(foundSubscription);
        
        // And - les entités liées existent toujours
        User foundUser = entityManager.find(User.class, user.getId());
        Theme foundTheme = entityManager.find(Theme.class, theme.getId());
        assertNotNull(foundUser);
        assertNotNull(foundTheme);
    }
}
