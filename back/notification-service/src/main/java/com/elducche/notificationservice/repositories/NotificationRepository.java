package com.elducche.notificationservice.repositories;

import com.elducche.notificationservice.models.Notification;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface NotificationRepository extends ReactiveCrudRepository<Notification, Long> {
    Flux<Notification> findByUserId(Long userId);
}
