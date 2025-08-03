package com.elducche.userservice.service;

import com.elducche.userservice.model.User;
import com.elducche.userservice.model.dto.LoginRequest;
import com.elducche.userservice.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.elducche.userservice.security.JwtUtil;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final String jwtSecret = System.getenv("JWT_SECRET");
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public String login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail());
        if (user == null) {
            System.out.println("[AUTH] Utilisateur non trouvé: " + loginRequest.getEmail());
            return null;
        }
        boolean match = BCrypt.checkpw(loginRequest.getPassword(), user.getPassword());
        if (!match) {
            System.out.println("[AUTH] Mot de passe incorrect pour l'utilisateur: " + loginRequest.getEmail());
            return null;
        }
        try {
            // Génération d'un token enrichi avec l'ID utilisateur et le nom d'utilisateur
            return jwtUtil.generateEnrichedToken(user.getId(), user.getEmail(), user.getUsername());
        } catch (Exception e) {
            System.out.println("[AUTH] Erreur lors de la génération du token JWT: " + e.getMessage());
            return null;
        }
    }
}