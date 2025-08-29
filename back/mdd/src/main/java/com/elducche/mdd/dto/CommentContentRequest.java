package com.elducche.mdd.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO pour les requêtes de création de commentaire sans postId 
 * (utilisé quand le postId est fourni dans l'URL)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentContentRequest {
    
    @NotBlank(message = "Le contenu est requis")
    private String content;
}
