package com.elducche.mdd.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entité Subscription - Représente l'abonnement d'un utilisateur à un thème
 * 
 * Cette entité utilise une clé composée (userId + themeId) :
 * - Relation many-to-many entre User et Theme
 * - Métadonnées de l'abonnement
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "subscriptions")
public class Subscription {
    
    @EmbeddedId
    @EqualsAndHashCode.Include
    private SubscriptionId id;
    
    @Column(name = "subscribed_at")
    private LocalDateTime subscribedAt;
    
    // Relations JPA
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("themeId")
    @JoinColumn(name = "theme_id")
    private Theme theme;
    
    @PrePersist
    protected void onCreate() {
        subscribedAt = LocalDateTime.now();
    }
    
    /**
     * Constructeur utilitaire pour créer un abonnement
     */
    public Subscription(User user, Theme theme) {
        this.user = user;
        this.theme = theme;
        this.id = new SubscriptionId(user.getId(), theme.getId());
        this.subscribedAt = LocalDateTime.now();
    }
}
