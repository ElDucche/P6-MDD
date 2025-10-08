package com.elducche.mdd.service.impl;

import com.elducche.mdd.dto.UpdateUserProfileRequest;
import com.elducche.mdd.dto.UserResponse;
import com.elducche.mdd.entity.User;
import com.elducche.mdd.exception.ResourceNotFoundException;
import com.elducche.mdd.repository.UserRepository;
import com.elducche.mdd.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

/**
 * Implémentation du service de gestion des utilisateurs
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
    
    @Override
    public UserResponse getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        
        return new UserResponse(user.getId(), user.getEmail(), user.getUsername(), 
                              user.getCreatedAt(), user.getUpdatedAt());
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        return new UserResponse(user.getId(), user.getEmail(), user.getUsername(),
                              user.getCreatedAt(), user.getUpdatedAt());
    }

    @Override
    public UserResponse updateUserProfile(String email, UpdateUserProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        
        if (request.getUsername() != null && !request.getUsername().trim().isEmpty() &&
            !request.getUsername().equals(user.getUsername())) {
            
            if (userRepository.existsByUsername(request.getUsername())) {
                log.warn("Tentative de mise à jour avec username existant: {}", request.getUsername());
                throw new IllegalArgumentException("Ce nom d'utilisateur est déjà pris");
            }
            user.setUsername(request.getUsername());
        }
        
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty() &&
            !request.getEmail().equals(user.getEmail())) {
            
            if (userRepository.existsByEmail(request.getEmail())) {
                log.warn("Tentative de mise à jour avec email existant: {}", request.getEmail());
                throw new IllegalArgumentException("Email déjà utilisé");
            }
            user.setEmail(request.getEmail());
        }
        
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        User savedUser = userRepository.save(user);
        log.info("Profil mis à jour pour l'utilisateur email: {}", email);
        return new UserResponse(savedUser.getId(), savedUser.getEmail(), savedUser.getUsername(),
                              savedUser.getCreatedAt(), savedUser.getUpdatedAt());
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    @Override
    public Optional<User> findUserById(Long id) {
        return findById(id);
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    @Override
    public Optional<User> updateUser(Long userId, User userUpdates) {
        return userRepository.findById(userId)
                .map(existingUser -> {
                    if (userUpdates.getUsername() != null && 
                        !userUpdates.getUsername().equals(existingUser.getUsername())) {
                        
                        if (userRepository.existsByUsername(userUpdates.getUsername())) {
                            log.warn("Tentative de mise à jour avec username existant: {}", userUpdates.getUsername());
                            throw new IllegalArgumentException("Ce nom d'utilisateur est déjà pris");
                        }
                        existingUser.setUsername(userUpdates.getUsername());
                    }
                    
                    if (userUpdates.getEmail() != null && 
                        !userUpdates.getEmail().equals(existingUser.getEmail())) {
                        
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
    
    @Override
    public Optional<User> updateUserProfile(Long userId, UpdateUserProfileRequest request) {
        return userRepository.findById(userId)
                .map(existingUser -> {
                    if (request.getUsername() != null && !request.getUsername().trim().isEmpty() &&
                        !request.getUsername().equals(existingUser.getUsername())) {
                        
                        if (userRepository.existsByUsername(request.getUsername())) {
                            log.warn("Tentative de mise à jour avec username existant: {}", request.getUsername());
                            throw new IllegalArgumentException("Ce nom d'utilisateur est déjà pris");
                        }
                        existingUser.setUsername(request.getUsername());
                    }
                    
                    if (request.getEmail() != null && !request.getEmail().trim().isEmpty() &&
                        !request.getEmail().equals(existingUser.getEmail())) {
                        
                        if (userRepository.existsByEmail(request.getEmail())) {
                            log.warn("Tentative de mise à jour avec email existant: {}", request.getEmail());
                            throw new IllegalArgumentException("Un compte avec cet email existe déjà");
                        }
                        existingUser.setEmail(request.getEmail());
                    }
                    
                    if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
                        existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
                    }
                    
                    User savedUser = userRepository.save(existingUser);
                    log.info("Profil mis à jour pour l'utilisateur ID: {}", userId);
                    return savedUser;
                });
    }
    
    @Override
    public boolean changePassword(Long userId, String currentPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isEmpty()) {
            log.warn("Tentative de changement de mot de passe pour utilisateur inexistant: {}", userId);
            return false;
        }
        
        User user = userOpt.get();
        
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            log.warn("Tentative de changement de mot de passe avec mot de passe actuel incorrect pour: {}", userId);
            return false;
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        log.info("Mot de passe changé pour l'utilisateur ID: {}", userId);
        return true;
    }
    
    @Override
    public boolean deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.warn("Tentative de suppression d'un utilisateur inexistant: {}", userId);
            return false;
        }
        
        userRepository.deleteById(userId);
        log.info("Utilisateur supprimé ID: {}", userId);
        return true;
    }
    
    @Override
    public boolean checkPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}
