package com.elducche.postservice.repositories;

import com.elducche.postservice.models.Subscription;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface SubscriptionRepository extends R2dbcRepository<Subscription, Long> {
}
