package com.elducche.mdd.repository;

import com.elducche.mdd.entity.*;
import com.elducche.mdd.util.TestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(properties = {"spring.sql.init.mode=never"})
@DisplayName("Tests du SubscriptionRepository")
class SubscriptionRepositoryTest {

    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Devrait trouver les abonnements par utilisateur")
    void shouldFindSubscriptionsByUser() {
        User user = entityManager.persistAndFlush(TestDataBuilder.createUser("sub@user.com", "subuser", "password"));
        Theme theme = entityManager.persistAndFlush(TestDataBuilder.createTheme("Sub", "Sub"));
        Subscription subscription = TestDataBuilder.createSubscriptionWithRelations(user, theme);
        entityManager.persistAndFlush(subscription);
        List<Subscription> subs = subscriptionRepository.findByUser(user);
        assertFalse(subs.isEmpty());
        assertEquals(user.getId(), subs.get(0).getUser().getId());
    }

    @Test
    @DisplayName("Devrait trouver les abonnements par thème")
    void shouldFindSubscriptionsByTheme() {
        User user = entityManager.persistAndFlush(TestDataBuilder.createUser("sub2@user.com", "subuser2", "password"));
        Theme theme = entityManager.persistAndFlush(TestDataBuilder.createTheme("Sub2", "Sub2"));
        Subscription subscription = TestDataBuilder.createSubscriptionWithRelations(user, theme);
        entityManager.persistAndFlush(subscription);
        List<Subscription> subs = subscriptionRepository.findByTheme(theme);
        assertFalse(subs.isEmpty());
        assertEquals(theme.getId(), subs.get(0).getTheme().getId());
    }

    @Test
    @DisplayName("Devrait sauvegarder un abonnement")
    void shouldSaveSubscription() {
        User user = entityManager.persistAndFlush(TestDataBuilder.createUser("save@sub.com", "savesub", "password"));
        Theme theme = entityManager.persistAndFlush(TestDataBuilder.createTheme("SaveSub", "SaveSub"));
        Subscription subscription = TestDataBuilder.createSubscriptionWithRelations(user, theme);
        Subscription saved = subscriptionRepository.save(subscription);
        assertNotNull(saved.getId());
        assertEquals(user.getId(), saved.getUser().getId());
        assertEquals(theme.getId(), saved.getTheme().getId());
    }

    @Test
    @DisplayName("Devrait supprimer un abonnement")
    void shouldDeleteSubscription() {
        User user = entityManager.persistAndFlush(TestDataBuilder.createUser("del@sub.com", "delsub", "password"));
        Theme theme = entityManager.persistAndFlush(TestDataBuilder.createTheme("DelSub", "DelSub"));
        Subscription subscription = TestDataBuilder.createSubscriptionWithRelations(user, theme);
        subscription = entityManager.persistAndFlush(subscription);
        subscriptionRepository.delete(subscription);
        assertTrue(subscriptionRepository.findById(subscription.getId()).isEmpty());
    }
}
