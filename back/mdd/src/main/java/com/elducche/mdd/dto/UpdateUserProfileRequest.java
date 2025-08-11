package com.elducche.mdd.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO pour la mise à jour du profil utilisateur
 */
@Data
public class UpdateUserProfileRequest {
    
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
    private String username;
    
    @Email(message = "L'email doit être valide")
    private String email;
    
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String password;
}
