package com.elducche.mdd.controller;

import com.elducche.mdd.dto.UpdateUserProfileRequest;
import com.elducche.mdd.entity.User;
import com.elducche.mdd.service.UserService;
import com.elducche.mdd.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

/**
 * Contrôleur pour la gestion des utilisateurs
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthUtil authUtil;

    /**
     * Récupère le profil de l'utilisateur connecté
     * @return Le profil utilisateur ou 401
     */
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUserProfile() {
        log.debug("Récupération du profil de l'utilisateur connecté");
        
        return authUtil.executeWithAuth(userId -> {
            Optional<User> user = userService.getUserById(userId);
            return user.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
        });
    }

    /**
     * Met à jour le profil de l'utilisateur connecté
     * @param request Nouvelles données du profil
     * @return Le profil mis à jour ou erreur
     */
    @PutMapping("/me")
    public ResponseEntity<?> updateCurrentUserProfile(@Valid @RequestBody UpdateUserProfileRequest request) {
        log.debug("Mise à jour du profil de l'utilisateur connecté");
        
        return authUtil.executeWithAuthHandleErrors(userId -> {
            Optional<User> updatedUser = userService.updateUserProfile(userId, request);
            return updatedUser.map(ResponseEntity::ok)
                              .orElse(ResponseEntity.notFound().build());
        });
    }

    /**
     * Récupère un utilisateur par son ID
     * @param id ID de l'utilisateur
     * @return L'utilisateur correspondant ou 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        log.debug("Récupération de l'utilisateur avec l'ID : {}", id);
        
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
}
