package com.elducche.postservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "subscriptions")
@IdClass(SubscriptionId.class)
public class Subscription {
    @Id
    @Column(name = "user_id")
    private Long userId;
    
    @Id
    @Column(name = "theme_id")
    private Long themeId;
}
