package com.elducche.mdd.service;

import com.elducche.mdd.entity.Subscription;
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
        Subscription subscription = new Subscription();
        // TODO: set composite key if needed
        when(subscriptionRepository.findById(any())).thenReturn(Optional.of(subscription));
        Optional<Subscription> result = subscriptionService.findById(any());
        assertTrue(result.isPresent());
    }

    // ... autres tests CRUD à compléter
}
