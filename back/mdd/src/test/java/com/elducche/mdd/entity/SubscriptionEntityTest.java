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
 * Tests unitaires pour l'entité Subscription
 */
@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.sql.init.mode=never"
})
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
        user = TestDataBuilder.createUser("user@test.com", "testuser", "password");
        theme = TestDataBuilder.createTheme("Java", "Programmation Java");
        
        // Sauvegarde des entités parentes
        user = entityManager.persistAndFlush(user);
        theme = entityManager.persistAndFlush(theme);
        
        // Création de l'abonnement valide avec ID composite
        validSubscription = TestDataBuilder.createSubscriptionWithRelations(user, theme);
    }

    @Test
    @DisplayName("Devrait créer un abonnement valide")
    void shouldCreateValidSubscription() {
        // Given - un abonnement valide avec relations
        
        // When - sauvegarde en base
        Subscription savedSubscription = entityManager.persistAndFlush(validSubscription);
        
        // Then - l'abonnement est sauvegardé avec un ID
        assertNotNull(savedSubscription);
        assertEquals(user.getId(), savedSubscription.getUser().getId());
        assertEquals(theme.getId(), savedSubscription.getTheme().getId());
        assertNotNull(savedSubscription.getSubscribedAt());
    }

    @Test
    @DisplayName("Devrait échouer sans utilisateur")
    void shouldFailWithoutUser() {
        // Given - un abonnement sans utilisateur
        validSubscription.setUser(null);
        
        // When/Then - exception de contrainte de base de données
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(validSubscription);
        });
    }

    @Test
    @DisplayName("Devrait échouer sans thème")
    void shouldFailWithoutTheme() {
        // Given - un abonnement sans thème
        validSubscription.setTheme(null);
        
        // When/Then - exception de contrainte de base de données
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(validSubscription);
        });
    }

    @Test
    @DisplayName("Devrait empêcher les doublons d'abonnement utilisateur-thème")
    void shouldPreventDuplicateUserThemeSubscriptions() {
        // Given - premier abonnement sauvegardé
        entityManager.persistAndFlush(validSubscription);
        
        // When - tentative de création d'un second abonnement identique
        Subscription duplicateSubscription = TestDataBuilder.createSubscriptionWithRelations(user, theme);
        
        // Then - exception de contrainte d'unicité
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(duplicateSubscription);
        });
    }

    @Test
    @DisplayName("Devrait permettre au même utilisateur de s'abonner à plusieurs thèmes")
    void shouldAllowSameUserToSubscribeToMultipleThemes() {
        // Given - premier abonnement sauvegardé
        entityManager.persistAndFlush(validSubscription);
        
        // When - création d'un second thème et abonnement
        Theme secondTheme = TestDataBuilder.createTheme("Spring", "Framework Spring");
        secondTheme = entityManager.persistAndFlush(secondTheme);
        
        Subscription secondSubscription = TestDataBuilder.createSubscriptionWithRelations(user, secondTheme);
        
        // Then - le second abonnement peut être sauvegardé
        Subscription savedSecondSubscription = entityManager.persistAndFlush(secondSubscription);
        assertNotNull(savedSecondSubscription);
        assertEquals(user.getId(), savedSecondSubscription.getUser().getId());
        assertEquals(secondTheme.getId(), savedSecondSubscription.getTheme().getId());
    }

    @Test
    @DisplayName("Devrait permettre à plusieurs utilisateurs de s'abonner au même thème")
    void shouldAllowMultipleUsersToSubscribeToSameTheme() {
        // Given - premier abonnement sauvegardé
        entityManager.persistAndFlush(validSubscription);
        
        // When - création d'un second utilisateur et abonnement
        User secondUser = TestDataBuilder.createUser("user2@test.com", "testuser2", "password");
        secondUser = entityManager.persistAndFlush(secondUser);
        
        Subscription secondSubscription = TestDataBuilder.createSubscriptionWithRelations(secondUser, theme);
        
        // Then - le second abonnement peut être sauvegardé
        Subscription savedSecondSubscription = entityManager.persistAndFlush(secondSubscription);
        assertNotNull(savedSecondSubscription);
        assertEquals(secondUser.getId(), savedSecondSubscription.getUser().getId());
        assertEquals(theme.getId(), savedSecondSubscription.getTheme().getId());
    }

    @Test
    @DisplayName("Devrait maintenir l'intégrité référentielle avec l'utilisateur")
    void shouldMaintainReferentialIntegrityWithUser() {
        // Given - un abonnement sauvegardé
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
        // Given - un abonnement sauvegardé
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
}
