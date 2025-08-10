package com.elducche.mdd.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO pour les requêtes de création de post
 */
@Data
public class PostCreateRequest {
    
    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 255, message = "Le titre ne peut pas dépasser 255 caractères")
    private String title;
    
    @NotBlank(message = "Le contenu est obligatoire")
    private String content;
    
    @NotNull(message = "L'ID du thème est obligatoire")
    private Long themeId;
}
