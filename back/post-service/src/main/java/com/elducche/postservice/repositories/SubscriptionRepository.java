package com.elducche.postservice.repositories;

import com.elducche.postservice.models.Subscription;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface SubscriptionRepository extends CrudRepository<Subscription, Long> {
    List<Subscription> findByUserId(Long userId);
}
