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

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final String jwtSecret = System.getenv("JWT_SECRET");

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
                        return Jwts.builder()
                                .setSubject(user.getEmail())
                                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                                .compact();
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