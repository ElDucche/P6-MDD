package com.elducche.mdd.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO pour les requêtes de création de commentaire
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateRequest {
    
    @NotNull(message = "L'ID du post est requis")
    private Long postId;
    
    @NotBlank(message = "Le contenu est requis")
    private String content;
}
