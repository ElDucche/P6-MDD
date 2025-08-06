package com.elducche.postservice.service;

import com.elducche.postservice.models.Subscription;
import com.elducche.postservice.repositories.SubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public Subscription subscribe(Subscription subscription) {
        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public void unsubscribe(Long userId, Long themeId) {
        subscriptionRepository.deleteByUserIdAndThemeId(userId, themeId);
    }

    public List<Subscription> getSubscriptionsByUser(Long userId) {
        return subscriptionRepository.findByUserId(userId);
    }
}
