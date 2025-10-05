package com.elducche.mdd.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * DTO pour la réponse des commentaires
 * Évite les références circulaires en incluant seulement les données nécessaires
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Réponse contenant les détails d'un commentaire")
public class CommentResponseDTO {
    
    @Schema(description = "Identifiant unique du commentaire", example = "1")
    private Long id;
    
    @Schema(description = "Contenu du commentaire", example = "Excellent article !")
    private String content;
    
    @Schema(description = "Date de création du commentaire")
    private LocalDateTime createdAt;
    
    @Schema(description = "Date de dernière modification du commentaire")
    private LocalDateTime updatedAt;
    
    @Schema(description = "Informations de l'auteur du commentaire")
    private UserInfo author;
    
    @Schema(description = "Informations du post commenté")
    private PostInfo post;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Informations sur l'auteur du commentaire")
    public static class UserInfo {
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
    @Builder
    @Schema(description = "Informations sur le post commenté")
    public static class PostInfo {
        @Schema(description = "Identifiant du post", example = "1")
        private Long id;
        
        @Schema(description = "Titre du post", example = "Introduction à Spring Boot")
        private String title;
    }
}
