package com.elducche.mdd.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO pour l'entité Subscription - Utilisé pour la sérialisation JSON
 * 
 * Ce DTO évite les références circulaires en incluant seulement
 * les données nécessaires pour le frontend.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDTO {
    private Long userId;
    private Long themeId;
    private LocalDateTime subscribedAt;
    
    // Informations de l'utilisateur (sans relations circulaires)
    private UserDTO user;
    
    // Informations du thème (sans relations circulaires)
    private ThemeDTO theme;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDTO {
        private Long id;
        private String username;
        private String email;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ThemeDTO {
        private Long id;
        private String title;
        private String description;
    }
}
