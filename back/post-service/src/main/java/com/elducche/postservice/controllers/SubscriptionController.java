package com.elducche.postservice.controllers;

import com.elducche.postservice.models.Subscription;
import com.elducche.postservice.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping
    public Subscription subscribe(@RequestBody Subscription subscription) {
        return subscriptionService.subscribe(subscription);
    }

    @DeleteMapping("/{userId}/{themeId}")
    public ResponseEntity<Void> unsubscribe(@PathVariable Long userId, @PathVariable Long themeId) {
        subscriptionService.unsubscribe(userId, themeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}")
    public List<Subscription> getSubscriptionsByUser(@PathVariable Long userId) {
        return subscriptionService.getSubscriptionsByUser(userId);
    }
}
