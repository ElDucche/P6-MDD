package com.elducche.postservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("SUBSCRIPTIONS")
public class Subscription {
    private Long userId;
    private Long themeId;
}
