package com.elducche.mdd.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO pour les réponses de connexion
 */
@Data
@AllArgsConstructor
@Schema(description = "Réponse de connexion utilisateur")
public class LoginResponse {
    
    @Schema(description = "Token JWT d'authentification", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    @Schema(description = "Message de retour", example = "Connexion réussie")
    private String message;
    
    @Schema(description = "Informations de l'utilisateur connecté")
    private UserResponse user;
    
    public static LoginResponse success(String token) {
        return new LoginResponse(token, "Connexion réussie", null);
    }
    
    public static LoginResponse success(String token, UserResponse user) {
        return new LoginResponse(token, "Connexion réussie", user);
    }
    
    public static LoginResponse registered(String token) {
        return new LoginResponse(token, "Inscription réussie", null);
    }
    
    public static LoginResponse registered(String token, UserResponse user) {
        return new LoginResponse(token, "Inscription réussie", user);
    }
    
    public static LoginResponse error(String message) {
        return new LoginResponse(null, message, null);
    }
}
