package com.elducche.mdd.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Contrôleur pour les endpoints de santé et informations système
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class HealthController {

    @Value("${spring.application.name:MDD}")
    private String applicationName;

    @Value("${server.port:8080}")
    private String serverPort;

    /**
     * Endpoint de santé de l'application
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = Map.of(
            "status", "UP",
            "application", applicationName,
            "port", serverPort,
            "timestamp", LocalDateTime.now(),
            "message", "Application MDD opérationnelle"
        );
        
        return ResponseEntity.ok(health);
    }

    /**
     * Informations sur l'API
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> info = Map.of(
            "application", Map.of(
                "name", "MDD - Monde de Dév",
                "description", "API REST pour réseau social de développeurs",
                "version", "1.0.0",
                "architecture", "Monolithique Spring Boot"
            ),
            "endpoints", Map.of(
                "authentication", "/api/auth/*",
                "users", "/api/users/*",
                "posts", "/api/posts/*",
                "themes", "/api/themes/*",
                "comments", "/api/comments/*",
                "subscriptions", "/api/subscriptions/*"
            ),
            "security", Map.of(
                "type", "JWT Bearer Token",
                "header", "Authorization: Bearer <token>"
            )
        );
        
        return ResponseEntity.ok(info);
    }
}
