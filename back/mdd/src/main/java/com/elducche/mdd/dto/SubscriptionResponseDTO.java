package com.elducche.mdd.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * DTO de réponse pour les abonnements.
 * Évite les références circulaires et expose seulement les données nécessaires.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Réponse contenant les détails d'un abonnement")
public class SubscriptionResponseDTO {
    
    @Schema(description = "Identifiant de l'utilisateur abonné", example = "1")
    private Long userId;
    
    @Schema(description = "Identifiant du thème", example = "1")
    private Long themeId;
    
    @Schema(description = "Date d'abonnement")
    private LocalDateTime subscribedAt;
    
    @Schema(description = "Informations de l'utilisateur abonné")
    private UserInfo user;
    
    @Schema(description = "Informations du thème")
    private ThemeInfo theme;
    
    /**
     * Informations de l'utilisateur associé
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Informations sur l'utilisateur abonné")
    public static class UserInfo {
        @Schema(description = "Identifiant de l'utilisateur", example = "1")
        private Long id;
        
        @Schema(description = "Nom d'utilisateur", example = "johndoe")
        private String username;
        
        @Schema(description = "Email de l'utilisateur", example = "john@example.com")
        private String email;
    }
    
    /**
     * Informations du thème associé
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Informations sur le thème")
    public static class ThemeInfo {
        @Schema(description = "Identifiant du thème", example = "1")
        private Long id;
        
        @Schema(description = "Titre du thème", example = "Développement")
        private String title;
        
        @Schema(description = "Description du thème", example = "Discussions sur le développement logiciel")
        private String description;
    }
}
