package com.elducche.mdd.service;

import com.elducche.mdd.entity.Subscription;
import com.elducche.mdd.entity.SubscriptionId;
import com.elducche.mdd.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class SubscriptionServiceTest {
    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById() {
        // Création d'une clé composite SubscriptionId
    SubscriptionId id = new SubscriptionId(2L, 1L);
    Subscription subscription = new Subscription();
    subscription.setId(id);
    when(subscriptionRepository.findById(id)).thenReturn(Optional.of(subscription));
    Optional<Subscription> result = subscriptionService.findById(id);
    assertTrue(result.isPresent());
    assertEquals(id, result.get().getId());
    }

    @Test
    void testCreateSubscription() {
    SubscriptionId id = new SubscriptionId(2L, 1L);
    Subscription subscription = new Subscription();
    subscription.setId(id);
    when(subscriptionRepository.save(any(Subscription.class))).thenReturn(subscription);
    Subscription created = subscriptionService.save(subscription);
    assertEquals(id, created.getId());
    }

    @Test
    void testDeleteSubscription() {
    SubscriptionId id = new SubscriptionId(2L, 1L);
    doNothing().when(subscriptionRepository).deleteById(id);
    subscriptionService.deleteById(id);
    verify(subscriptionRepository, times(1)).deleteById(id);
    }
}
