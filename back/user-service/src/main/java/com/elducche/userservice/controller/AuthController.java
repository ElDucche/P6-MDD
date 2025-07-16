package com.elducche.userservice.controller;

import com.elducche.userservice.model.User;
import com.elducche.userservice.service.UserService;
import com.elducche.userservice.service.AuthService;
import com.elducche.userservice.model.dto.LoginRequest;
import com.elducche.userservice.model.dto.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final AuthService authService;

    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<String>> register(@RequestBody User user) {
        System.out.println("[USER-SERVICE] User reçu : " + user);
        return userService.findByEmail(user.getEmail())
                .flatMap(existing -> Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body("Email already in use")))
                .switchIfEmpty(userService.register(user)
                        .then(Mono.just(ResponseEntity.status(HttpStatus.CREATED).body("Inscription réussie !"))));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<String>> login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest)
                .map(token -> ResponseEntity.ok(token))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials")));
    }
}
