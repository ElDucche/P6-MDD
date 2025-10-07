package com.elducche.mdd.service;

import com.elducche.mdd.dto.LoginRequest;
import com.elducche.mdd.dto.LoginResponse;
import com.elducche.mdd.dto.RegisterRequest;
import com.elducche.mdd.entity.User;


import java.util.Optional;

/**
 * Service d'authentification
 * 
 * Gère l'inscription, la connexion et la génération des tokens JWT
 */

public interface AuthService {
    // Méthodes pour les tests unitaires
    Optional<User> findByEmail(String email);

    Optional<User> authenticate(String email, String password) ;

    /**
     * Connexion d'un utilisateur (email ou username accepté dans le champ identifiant)
     */
    LoginResponse login(LoginRequest loginRequest);
    
    /**
     * Inscription d'un nouvel utilisateur
     */
    LoginResponse register(RegisterRequest registerRequest) ;
    
    /**
     * Validation d'un token JWT
     */
    boolean isTokenValid(String token) ;
}
