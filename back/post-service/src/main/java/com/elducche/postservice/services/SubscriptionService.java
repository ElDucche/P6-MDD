package com.elducche.postservice.services;

import com.elducche.postservice.models.Subscription;
import com.elducche.postservice.repositories.SubscriptionRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public Mono<Subscription> subscribe(Subscription subscription) {
        return subscriptionRepository.save(subscription);
    }

    public Flux<Subscription> getSubscriptionsByUser(Long userId) {
        return subscriptionRepository.findByUserId(userId);
    }
}
