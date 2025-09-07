package com.elducche.mdd.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO pour les requêtes de connexion
 */
@Data
@Schema(description = "Requête de connexion utilisateur")
public class LoginRequest {
    
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Le format de l'email est invalide")
    @Schema(description = "Email de l'utilisateur", example = "user@example.com")
    private String email;
    
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Schema(description = "Mot de passe de l'utilisateur", example = "monMotDePasse123")
    private String password;
}
