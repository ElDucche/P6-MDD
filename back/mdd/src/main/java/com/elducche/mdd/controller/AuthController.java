package com.elducche.mdd.controller;

import com.elducche.mdd.dto.LoginRequest;
import com.elducche.mdd.dto.LoginResponse;
import com.elducche.mdd.dto.RegisterRequest;
import com.elducche.mdd.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Contrôleur pour la gestion de l'authentification
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Inscription d'un nouvel utilisateur
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Tentative d'inscription pour l'email: {}", request.getEmail());
        
        try {
            LoginResponse response = authService.register(request);
            if (response.getToken() != null) {
                log.info("Inscription réussie pour l'email: {}", request.getEmail());
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                log.warn("Échec de l'inscription pour l'email: {}", request.getEmail());
                return ResponseEntity.badRequest()
                    .body(response.getMessage());
            }
        } catch (Exception e) {
            log.error("Erreur lors de l'inscription pour l'email {}: {}", request.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur interne du serveur");
        }
    }

    /**
     * Connexion d'un utilisateur
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        log.info("Tentative de connexion pour l'email: {}", request.getEmail());
        
        try {
            LoginResponse response = authService.login(request);
            if (response.getToken() != null) {
                log.info("Connexion réussie pour l'email: {}", request.getEmail());
                return ResponseEntity.ok(response);
            } else {
                log.warn("Échec de la connexion pour l'email: {}", request.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Identifiants invalides");
            }
        } catch (Exception e) {
            log.error("Erreur lors de la connexion pour l'email {}: {}", request.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur interne du serveur");
        }
    }

    /**
     * Validation d'un token JWT
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token manquant ou invalide");
            }
            
            String token = authHeader.substring(7);
            boolean isValid = authService.isTokenValid(token);
            
            if (isValid) {
                return ResponseEntity.ok("Token valide");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token invalide ou expiré");
            }
        } catch (Exception e) {
            log.error("Erreur lors de la validation du token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur interne du serveur");
        }
    }
}