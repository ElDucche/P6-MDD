package com.elducche.mdd.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO pour les requêtes de création de post
 */
@Data
@Schema(description = "Requête de création de post")
public class PostCreateRequest {
    
    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 255, message = "Le titre ne peut pas dépasser 255 caractères")
    @Schema(description = "Titre du post", example = "Introduction à Spring Boot")
    private String title;
    
    @NotBlank(message = "Le contenu est obligatoire")
    @Schema(description = "Contenu du post", example = "Spring Boot simplifie le développement...")
    private String content;
    
    @NotNull(message = "L'ID du thème est obligatoire")
    @Schema(description = "Identifiant du thème", example = "1")
    private Long themeId;
}
