package com.elducche.mdd.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO pour les réponses de connexion
 */
@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String message;
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
