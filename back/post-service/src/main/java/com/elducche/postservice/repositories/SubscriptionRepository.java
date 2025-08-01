package com.elducche.postservice.repositories;

import com.elducche.postservice.models.Subscription;
import com.elducche.postservice.models.SubscriptionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, SubscriptionId> {
    List<Subscription> findByUserId(Long userId);
}
