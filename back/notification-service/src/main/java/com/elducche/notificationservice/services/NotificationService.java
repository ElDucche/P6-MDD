package com.elducche.notificationservice.services;

import com.elducche.notificationservice.models.Notification;
import com.elducche.notificationservice.repositories.NotificationRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Flux<Notification> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserId(userId);
    }
}
