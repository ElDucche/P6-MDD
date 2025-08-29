package com.elducche.mdd.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO de réponse pour les abonnements.
 * Évite les références circulaires et expose seulement les données nécessaires.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponseDTO {
    
    private Long userId;
    private Long themeId;
    private LocalDateTime subscribedAt;
    
    /**
     * Informations de l'utilisateur associé
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String username;
        private String email;
    }
    
    /**
     * Informations du thème associé
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ThemeInfo {
        private Long id;
        private String title;
        private String description;
    }
    
    private UserInfo user;
    private ThemeInfo theme;
}
