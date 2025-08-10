package com.elducche.mdd.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;

/**
 * Clé composite pour l'entité Subscription
 * 
 * Combine userId et themeId pour former la clé primaire
 * de la table de relation many-to-many subscriptions
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class SubscriptionId implements Serializable {
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "theme_id")
    private Long themeId;
}
