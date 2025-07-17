package com.elducche.userservice.service;

import com.elducche.userservice.model.User;
import com.elducche.userservice.model.dto.LoginRequest;
import com.elducche.userservice.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
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

    public Mono<String> login(LoginRequest loginRequest) {
        return userRepository.findByEmail(loginRequest.getEmail())
                .filter(user -> {
                    boolean match = BCrypt.checkpw(loginRequest.getPassword(), user.getPassword());
                    if (!match) {
                        System.out.println("[AUTH] Mot de passe incorrect pour l'utilisateur: " + loginRequest.getEmail());
                    }
                    return match;
                })
                .map(user -> {
                    try {
                        // Utilise JwtUtil pour générer le token avec iat/exp
                        return jwtUtil.generateToken(user.getEmail());
                    } catch (Exception e) {
                        System.out.println("[AUTH] Erreur lors de la génération du token JWT: " + e.getMessage());
                        return null;
                    }
                })
                .filter(token -> token != null)
                .onErrorResume(e -> {
                    System.out.println("[AUTH] Exception lors du login: " + e.getMessage());
                    return Mono.empty();
                });
    }
}