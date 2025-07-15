package com.elducche.api_gateway.services;

import com.elducche.api_gateway.dto.LoginRequest;
import com.elducche.api_gateway.dto.RegisterRequest;
import com.elducche.api_gateway.models.User;
import com.elducche.api_gateway.repositories.UserRepository;
import com.elducche.api_gateway.security.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public Mono<User> register(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return userRepository.save(user);
    }

    public Mono<String> login(LoginRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .map(user -> jwtUtil.generateToken(user.getEmail()))
                .switchIfEmpty(Mono.error(new RuntimeException("Invalid credentials")));
    }
}
