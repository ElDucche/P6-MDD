package com.elducche.mdd.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO pour l'entité Comment - Utilisé pour la sérialisation JSON
 * 
 * Ce DTO évite les références circulaires en incluant seulement
 * les données nécessaires pour le frontend.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Informations de l'auteur (sans relations circulaires)
    private AuthorDTO author;
    
    // Informations du post (sans relations circulaires)
    private PostInfoDTO post;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthorDTO {
        private Long id;
        private String username;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostInfoDTO {
        private Long id;
        private String title;
    }
}
