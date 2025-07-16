package com.elducche.userservice.service;

import com.elducche.userservice.model.User;
import com.elducche.userservice.model.dto.LoginRequest;
import com.elducche.userservice.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AuthService {
    private final UserRepository userRepository;
    @Value("${jwt.secret}")
    private String jwtSecret;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<String> login(LoginRequest loginRequest) {
        return userRepository.findByEmail(loginRequest.getEmail())
                .filter(user -> user.getPassword().equals(loginRequest.getPassword()))
                .map(user -> Jwts.builder()
                        .setSubject(user.getEmail())
                        .signWith(SignatureAlgorithm.HS256, jwtSecret)
                        .compact()
                );
    }
}