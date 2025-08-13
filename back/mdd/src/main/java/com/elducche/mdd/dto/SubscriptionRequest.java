package com.elducche.mdd.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO pour la création/suppression d'un abonnement à un thème
 */
@Data
public class SubscriptionRequest {
    
    @NotNull(message = "L'ID du thème est obligatoire")
    private Long themeId;
}
