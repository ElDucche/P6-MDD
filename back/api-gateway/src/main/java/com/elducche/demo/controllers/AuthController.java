package com.elducche.demo.controllers;

import com.elducche.demo.dto.AuthResponse;
import com.elducche.demo.dto.LoginRequest;
import com.elducche.demo.dto.RegisterRequest;
import com.elducche.demo.services.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public Mono<Void> register(@RequestBody RegisterRequest request) {
        return authService.register(request).then();
    }

    @PostMapping("/login")
    public Mono<AuthResponse> login(@RequestBody LoginRequest request) {
        return authService.login(request).map(AuthResponse::new);
    }
}
