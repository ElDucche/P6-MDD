package com.elducche.mdd.controller;

import com.elducche.mdd.dto.LoginRequest;
import com.elducche.mdd.dto.LoginResponse;
import com.elducche.mdd.dto.RegisterRequest;
import com.elducche.mdd.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

/**
 * Contrôleur pour la gestion de l'authentification
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
// @RequiredArgsConstructor
public class AuthController {

    private static final String ERREUR_INTERNE = "Erreur interne du serveur";

    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Inscription d'un nouvel utilisateur
     */
    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid @RequestBody RegisterRequest request, HttpServletResponse response) {
        log.info("Tentative d'inscription pour l'email: {}", request.getEmail());
        
        try {
            LoginResponse loginResponse = authService.register(request);
            if (loginResponse.getToken() != null) {
                log.info("Inscription réussie pour l'email: {}", request.getEmail());
                
                // Créer le cookie HttpOnly sécurisé
                ResponseCookie cookie = createAuthCookie(loginResponse.getToken());
                response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
                
                // Retourner la réponse sans le token (il est dans le cookie)
                LoginResponse responseWithoutToken = new LoginResponse(null, loginResponse.getMessage(), null);
                return ResponseEntity.status(HttpStatus.CREATED).body(responseWithoutToken);
            } else {
                log.warn("Échec de l'inscription pour l'email: {}", request.getEmail());
                return ResponseEntity.badRequest()
                    .body(loginResponse.getMessage());
            }
        } catch (Exception e) {
            log.error("Erreur lors de l'inscription pour l'email {}: {}", request.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ERREUR_INTERNE);
        }
    }

    /**
     * Connexion d'un utilisateur
     */
    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        log.info("Tentative de connexion pour l'identifiant: {}", request.getIdentifier());
        
        try {
            LoginResponse loginResponse = authService.login(request);
            if (loginResponse.getToken() != null) {
                log.info("Connexion réussie pour l'identifiant: {}", request.getIdentifier());
                
                // Créer le cookie HttpOnly sécurisé
                ResponseCookie cookie = createAuthCookie(loginResponse.getToken());
                response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
                
                // Retourner la réponse sans le token (il est dans le cookie)
                LoginResponse responseWithoutToken = new LoginResponse(null, loginResponse.getMessage(), null);
                return ResponseEntity.ok(responseWithoutToken);
            } else {
                log.warn("Échec de la connexion pour l'identifiant: {}", request.getIdentifier());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Identifiants invalides");
            }
        } catch (Exception e) {
            log.error("Erreur lors de la connexion pour l'identifiant {}: {}", request.getIdentifier(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ERREUR_INTERNE);
        }
    }

    /**
     * Déconnexion de l'utilisateur (suppression du cookie)
     */
    @PostMapping("/logout")
    public ResponseEntity<Object> logout(HttpServletResponse response) {
        log.info("Déconnexion de l'utilisateur");
        
        // Créer un cookie expiré pour supprimer le cookie d'authentification
        ResponseCookie cookie = ResponseCookie.from("authToken", "")
            .httpOnly(true)
            .secure(false) // true en production avec HTTPS
            .path("/")
            .maxAge(0)
            .sameSite("Lax")
            .build();
        
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        
        return ResponseEntity.ok("Déconnexion réussie");
    }

    /**
     * Validation d'un token JWT
     */
    @GetMapping("/validate")
    public ResponseEntity<Object> validateToken(@RequestHeader(value = "Authorization", required = false) String authHeader) {
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
                .body(ERREUR_INTERNE);
        }
    }

    /**
     * Crée un cookie HttpOnly sécurisé pour le token JWT
     */
    private ResponseCookie createAuthCookie(String token) {
        return ResponseCookie.from("authToken", token)
            .httpOnly(true)  // Protection XSS : inaccessible via JavaScript
            .secure(false)   // true en production avec HTTPS, false pour dev
            .path("/")       // Cookie valide pour toute l'application
            .maxAge(24 * 60 * 60) // 24 heures
            .sameSite("Lax") // Protection CSRF partielle (Strict serait plus sûr mais peut poser problème)
            .build();
    }
}