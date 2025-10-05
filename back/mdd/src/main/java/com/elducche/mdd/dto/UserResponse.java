package com.elducche.mdd.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * DTO pour les réponses contenant les informations utilisateur
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Réponse contenant les informations d'un utilisateur")
public class UserResponse {
    
    @Schema(description = "Identifiant unique de l'utilisateur", example = "1")
    private Long id;
    
    @Schema(description = "Email de l'utilisateur", example = "user@example.com")
    private String email;
    
    @Schema(description = "Nom d'utilisateur", example = "johndoe")
    private String username;
    
    @Schema(description = "Date de création du compte")
    private LocalDateTime createdAt;
    
    @Schema(description = "Date de dernière modification du profil")
    private LocalDateTime updatedAt;
}
