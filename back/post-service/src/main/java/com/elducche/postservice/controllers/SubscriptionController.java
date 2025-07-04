package com.elducche.postservice.controllers;

import com.elducche.postservice.models.Subscription;
import com.elducche.postservice.services.SubscriptionService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping
    public Mono<Subscription> subscribe(@RequestBody Subscription subscription) {
        return subscriptionService.subscribe(subscription);
    }

    @GetMapping("/user/{userId}")
    public Flux<Subscription> getSubscriptionsByUser(@PathVariable Long userId) {
        return subscriptionService.getSubscriptionsByUser(userId);
    }
}
