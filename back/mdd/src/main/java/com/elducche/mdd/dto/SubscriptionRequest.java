package com.elducche.mdd.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO pour la création/suppression d'un abonnement à un thème
 */
@Data
@Schema(description = "Requête d'abonnement/désabonnement à un thème")
public class SubscriptionRequest {
    
    @NotNull(message = "L'ID du thème est obligatoire")
    @Schema(description = "Identifiant du thème", example = "1")
    private Long themeId;
}
