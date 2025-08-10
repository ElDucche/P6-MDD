package com.elducche.mdd.service;

import com.elducche.mdd.dto.LoginRequest;
import com.elducche.mdd.dto.LoginResponse;
import com.elducche.mdd.dto.RegisterRequest;
import com.elducche.mdd.entity.User;
import com.elducche.mdd.repository.UserRepository;
import com.elducche.mdd.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service d'authentification
 * 
 * Gère l'inscription, la connexion et la génération des tokens JWT
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    /**
     * Connexion d'un utilisateur
     */
    public LoginResponse login(LoginRequest loginRequest) {
        try {
            // Recherche de l'utilisateur
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElse(null);
            
            if (user == null) {
                log.warn("Tentative de connexion avec email inexistant: {}", loginRequest.getEmail());
                return LoginResponse.error("Email ou mot de passe incorrect");
            }
            
            // Vérification du mot de passe
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                log.warn("Tentative de connexion avec mot de passe incorrect pour: {}", loginRequest.getEmail());
                return LoginResponse.error("Email ou mot de passe incorrect");
            }
            
            // Génération du token JWT simplifié
            String token = jwtUtil.generateToken(user.getEmail());
            log.info("Connexion réussie pour l'utilisateur: {}", user.getEmail());
            
            return LoginResponse.success(token);
            
        } catch (Exception e) {
            log.error("Erreur lors de la connexion pour {}: {}", loginRequest.getEmail(), e.getMessage());
            return LoginResponse.error("Erreur technique lors de la connexion");
        }
    }
    
    /**
     * Inscription d'un nouvel utilisateur
     */
    public LoginResponse register(RegisterRequest registerRequest) {
        try {
            // Vérifications d'existence
            if (userRepository.existsByEmail(registerRequest.getEmail())) {
                log.warn("Tentative d'inscription avec email existant: {}", registerRequest.getEmail());
                return LoginResponse.error("Un compte avec cet email existe déjà");
            }
            
            if (userRepository.existsByUsername(registerRequest.getUsername())) {
                log.warn("Tentative d'inscription avec username existant: {}", registerRequest.getUsername());
                return LoginResponse.error("Ce nom d'utilisateur est déjà pris");
            }
            
            // Création de l'utilisateur
            User user = new User();
            user.setEmail(registerRequest.getEmail());
            user.setUsername(registerRequest.getUsername());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            
            User savedUser = userRepository.save(user);
            log.info("Nouveau compte créé pour l'utilisateur: {}", savedUser.getEmail());
            
            // Génération du token JWT pour connexion automatique
            String token = jwtUtil.generateToken(savedUser.getEmail());
            
            return LoginResponse.success(token);
            
        } catch (Exception e) {
            log.error("Erreur lors de l'inscription pour {}: {}", registerRequest.getEmail(), e.getMessage());
            return LoginResponse.error("Erreur technique lors de l'inscription");
        }
    }
    
    /**
     * Validation d'un token JWT
     */
    public boolean isTokenValid(String token) {
        return jwtUtil.isTokenValid(token);
    }
}
