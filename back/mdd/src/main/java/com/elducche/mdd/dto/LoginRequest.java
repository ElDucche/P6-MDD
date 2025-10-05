package com.elducche.mdd.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO pour les requêtes de connexion
 */
@Data
@Schema(description = "Requête de connexion utilisateur (email OU username)")
public class LoginRequest {

    /**
     * Identifiant pouvant être un email ou un username.
     * Anciennement nommé 'email' (on conserve ce nom de champ pour ne pas casser le front existant).
     * La validation du format email est supprimée pour permettre l'utilisation d'un username.
     */
    @NotBlank(message = "L'identifiant (email ou username) est obligatoire")
    @Schema(description = "Identifiant de connexion: email ou username", examples = {"user@example.com", "johndoe"})
    @JsonAlias({"email"}) // compat rétro avec anciens clients
    private String identifier;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Schema(description = "Mot de passe de l'utilisateur", example = "MonMotDePasse123!")
    private String password;
}
