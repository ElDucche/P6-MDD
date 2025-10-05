package com.elducche.mdd.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO pour les requêtes de création de commentaire
 * Peut être utilisé avec ou sans postId selon le contexte
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête de création de commentaire")
public class CommentCreateRequest {
    
    @Schema(description = "ID du post (optionnel si fourni dans l'URL)", example = "1")
    private Long postId;
    
    @NotBlank(message = "Le contenu est requis")
    @Schema(description = "Contenu du commentaire", example = "Excellent article !")
    private String content;
}
