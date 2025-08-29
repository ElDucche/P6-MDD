package com.elducche.mdd.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour les réponses contenant les informations utilisateur
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
