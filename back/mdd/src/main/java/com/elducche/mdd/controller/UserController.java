package com.elducche.mdd.controller;

import com.elducche.mdd.dto.UpdateUserProfileRequest;
import com.elducche.mdd.dto.UserResponse;
import com.elducche.mdd.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Contrôleur REST pour la gestion des profils utilisateurs
 * 
 * Ce contrôleur gère les opérations sur le profil de l'utilisateur connecté :
 * - Récupération du profil
 * - Mise à jour du profil
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Récupère le profil de l'utilisateur connecté
     * 
     * @param authentication L'authentification Spring Security
     * @return Les informations du profil utilisateur
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        log.info("Récupération du profil pour l'utilisateur: {}", authentication.getName());
        
        String userEmail = authentication.getName();
        UserResponse userResponse = userService.getUserProfile(userEmail);
        
        log.info("Profil récupéré avec succès pour l'utilisateur: {}", userEmail);
        return ResponseEntity.ok(userResponse);
    }

    /**
     * Met à jour le profil de l'utilisateur connecté
     * 
     * @param authentication L'authentification Spring Security
     * @param updateRequest Les nouvelles données du profil
     * @return Le profil utilisateur mis à jour
     */
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUser(
            Authentication authentication,
            @Valid @RequestBody UpdateUserProfileRequest updateRequest) {
        
        log.info("Mise à jour du profil pour l'utilisateur: {}", authentication.getName());
        
        String userEmail = authentication.getName();
        UserResponse updatedUser = userService.updateUserProfile(userEmail, updateRequest);
        
        log.info("Profil mis à jour avec succès pour l'utilisateur: {}", userEmail);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Récupère un utilisateur par son ID
     * 
     * @param id L'ID de l'utilisateur à récupérer
     * @return Les informations de l'utilisateur
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        log.info("Récupération de l'utilisateur avec l'ID: {}", id);
        
        UserResponse userResponse = userService.getUserById(id);
        log.info("Utilisateur récupéré avec succès pour l'ID: {}", id);
        
        return ResponseEntity.ok(userResponse);
    }
}
