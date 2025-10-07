package com.elducche.mdd.service;

import com.elducche.mdd.dto.UpdateUserProfileRequest;
import com.elducche.mdd.dto.UserResponse;
import com.elducche.mdd.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Service de gestion des utilisateurs
 * 
 * Fournit les opérations CRUD et métier sur les utilisateurs
 */
public interface UserService {
    // Méthodes CRUD minimales pour les tests unitaires
    User save(User user);
    void deleteById(Long id);
    
    /**
     * Récupère le profil utilisateur par email
     */
    UserResponse getUserProfile(String email);

    /**
     * Récupère un utilisateur par son ID et retourne un UserResponse
     */
    UserResponse getUserById(Long id);

    /**
     * Met à jour le profil utilisateur par email
     */
    UserResponse updateUserProfile(String email, UpdateUserProfileRequest request);

    /**
     * Trouve un utilisateur par son ID
     */
    Optional<User> findById(Long id);
    
    /**
     * Alias pour findById (compatibilité avec les contrôleurs)
     */
    Optional<User> findUserById(Long id);
    
    /**
     * Trouve un utilisateur par son email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Trouve un utilisateur par son username
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Trouve tous les utilisateurs
     */
    List<User> findAll();
    
    /**
     * Met à jour le profil d'un utilisateur
     */
    Optional<User> updateUser(Long userId, User userUpdates);
    
    /**
     * Met à jour le profil utilisateur avec un DTO
     */
    Optional<User> updateUserProfile(Long userId, UpdateUserProfileRequest request);
    
    /**
     * Change le mot de passe d'un utilisateur
     */
    boolean changePassword(Long userId, String currentPassword, String newPassword);
    
    /**
     * Supprime un utilisateur
     */
    boolean deleteUser(Long userId);
    
    /**
     * Vérifie si un mot de passe correspond à celui de l'utilisateur
     */
    boolean checkPassword(User user, String rawPassword);
}
