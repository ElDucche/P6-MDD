package com.elducche.mdd.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
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
public class PostDTO {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Informations de l'auteur (sans relations circulaires)
    private AuthorDTO author;
    
    // Informations du thème (sans relations circulaires)
    private ThemeDTO theme;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthorDTO {
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
