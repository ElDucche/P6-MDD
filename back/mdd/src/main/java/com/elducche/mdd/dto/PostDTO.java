package com.elducche.mdd.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * DTO pour l'entité Post - Utilisé pour la sérialisation JSON
 * 
 * Ce DTO évite les références circulaires en incluant seulement
 * les données nécessaires pour le frontend.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Réponse contenant les détails d'un post")
public class PostDTO {
    
    @Schema(description = "Identifiant unique du post", example = "1")
    private Long id;
    
    @Schema(description = "Titre du post", example = "Introduction à Spring Boot")
    private String title;
    
    @Schema(description = "Contenu du post", example = "Spring Boot simplifie le développement d'applications Java...")
    private String content;
    
    @Schema(description = "Date de création du post")
    private LocalDateTime createdAt;
    
    @Schema(description = "Date de dernière modification du post")
    private LocalDateTime updatedAt;
    
    @Schema(description = "Informations de l'auteur du post")
    private AuthorDTO author;
    
    @Schema(description = "Informations du thème du post")
    private ThemeDTO theme;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Informations sur l'auteur du post")
    public static class AuthorDTO {
        @Schema(description = "Identifiant de l'utilisateur", example = "1")
        private Long id;
        
        @Schema(description = "Nom d'utilisateur", example = "johndoe")
        private String username;
        
        @Schema(description = "Email de l'utilisateur", example = "john@example.com")
        private String email;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Informations sur le thème du post")
    public static class ThemeDTO {
        @Schema(description = "Identifiant du thème", example = "1")
        private Long id;
        
        @Schema(description = "Titre du thème", example = "Développement")
        private String title;
        
        @Schema(description = "Description du thème", example = "Discussions sur le développement logiciel")
        private String description;
    }
}
