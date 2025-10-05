package com.elducche.mdd.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO pour la mise à jour du profil utilisateur
 */
@Data
@Schema(description = "Requête de mise à jour du profil utilisateur")
public class UpdateUserProfileRequest {
    
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
    @Schema(description = "Nouveau nom d'utilisateur (optionnel)", example = "johnsmith")
    private String username;
    
    @Email(message = "L'email doit être valide")
    @Schema(description = "Nouvel email (optionnel)", example = "john.smith@example.com")
    private String email;
    
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    @Schema(description = "Nouveau mot de passe (optionnel)", example = "nouveauMotDePasse123")
    private String password;
}
