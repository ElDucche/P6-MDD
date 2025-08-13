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
    
    public static LoginResponse success(String token) {
        return new LoginResponse(token, "Connexion réussie");
    }
    
    public static LoginResponse error(String message) {
        return new LoginResponse(null, message);
    }
}
