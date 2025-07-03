package com.elducche.notificationservice.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("NOTIFICATIONS")
public class Notification {
    @Id
    private Long id;
    private Long userId;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;
}