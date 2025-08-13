package com.elducche.mdd.service;

import com.elducche.mdd.dto.UpdateUserProfileRequest;
import com.elducche.mdd.entity.User;
import com.elducche.mdd.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

/**
 * Service de gestion des utilisateurs
 * 
 * Fournit les opérations CRUD et métier sur les utilisateurs
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Trouve un utilisateur par son ID
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * Alias pour findById (compatibilité avec les contrôleurs)
     */
    public Optional<User> getUserById(Long id) {
        return findById(id);
    }
    
    /**
     * Trouve un utilisateur par son email
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Trouve un utilisateur par son username
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * Trouve tous les utilisateurs
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    /**
     * Met à jour le profil d'un utilisateur
     */
    public Optional<User> updateUser(Long userId, User userUpdates) {
        return userRepository.findById(userId)
                .map(existingUser -> {
                    // Mise à jour des champs modifiables
                    if (userUpdates.getUsername() != null && 
                        !userUpdates.getUsername().equals(existingUser.getUsername())) {
                        
                        // Vérification que le nouveau username n'existe pas déjà
                        if (userRepository.existsByUsername(userUpdates.getUsername())) {
                            log.warn("Tentative de mise à jour avec username existant: {}", userUpdates.getUsername());
                            throw new IllegalArgumentException("Ce nom d'utilisateur est déjà pris");
                        }
                        existingUser.setUsername(userUpdates.getUsername());
                    }
                    
                    if (userUpdates.getEmail() != null && 
                        !userUpdates.getEmail().equals(existingUser.getEmail())) {
                        
                        // Vérification que le nouvel email n'existe pas déjà
                        if (userRepository.existsByEmail(userUpdates.getEmail())) {
                            log.warn("Tentative de mise à jour avec email existant: {}", userUpdates.getEmail());
                            throw new IllegalArgumentException("Un compte avec cet email existe déjà");
                        }
                        existingUser.setEmail(userUpdates.getEmail());
                    }
                    
                    User savedUser = userRepository.save(existingUser);
                    log.info("Profil mis à jour pour l'utilisateur ID: {}", userId);
                    return savedUser;
                });
    }
    
    /**
     * Met à jour le profil utilisateur avec un DTO
     */
    public Optional<User> updateUserProfile(Long userId, UpdateUserProfileRequest request) {
        return userRepository.findById(userId)
                .map(existingUser -> {
                    // Mise à jour du nom d'utilisateur
                    if (request.getUsername() != null && !request.getUsername().trim().isEmpty() &&
                        !request.getUsername().equals(existingUser.getUsername())) {
                        
                        if (userRepository.existsByUsername(request.getUsername())) {
                            log.warn("Tentative de mise à jour avec username existant: {}", request.getUsername());
                            throw new IllegalArgumentException("Ce nom d'utilisateur est déjà pris");
                        }
                        existingUser.setUsername(request.getUsername());
                    }
                    
                    // Mise à jour de l'email
                    if (request.getEmail() != null && !request.getEmail().trim().isEmpty() &&
                        !request.getEmail().equals(existingUser.getEmail())) {
                        
                        if (userRepository.existsByEmail(request.getEmail())) {
                            log.warn("Tentative de mise à jour avec email existant: {}", request.getEmail());
                            throw new IllegalArgumentException("Un compte avec cet email existe déjà");
                        }
                        existingUser.setEmail(request.getEmail());
                    }
                    
                    // Mise à jour du mot de passe
                    if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
                        existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
                    }
                    
                    User savedUser = userRepository.save(existingUser);
                    log.info("Profil mis à jour pour l'utilisateur ID: {}", userId);
                    return savedUser;
                });
    }
    
    /**
     * Change le mot de passe d'un utilisateur
     */
    public boolean changePassword(Long userId, String currentPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isEmpty()) {
            log.warn("Tentative de changement de mot de passe pour utilisateur inexistant: {}", userId);
            return false;
        }
        
        User user = userOpt.get();
        
        // Vérification du mot de passe actuel
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            log.warn("Tentative de changement de mot de passe avec mot de passe actuel incorrect pour: {}", userId);
            return false;
        }
        
        // Mise à jour du mot de passe
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        log.info("Mot de passe changé pour l'utilisateur ID: {}", userId);
        return true;
    }
    
    /**
     * Supprime un utilisateur (soft delete possible à implémenter plus tard)
     */
    public boolean deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.warn("Tentative de suppression d'un utilisateur inexistant: {}", userId);
            return false;
        }
        
        userRepository.deleteById(userId);
        log.info("Utilisateur supprimé ID: {}", userId);
        return true;
    }
    
    /**
     * Vérifie si un mot de passe correspond à celui de l'utilisateur
     */
    public boolean checkPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}
