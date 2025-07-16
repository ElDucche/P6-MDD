package com.elducche.userservice.controller;

import com.elducche.userservice.model.User;
import com.elducche.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
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
    public Mono<ResponseEntity<String>> login(@RequestBody User loginRequest) {
        return userService.findByEmail(loginRequest.getEmail())
                .flatMap(user -> userService.checkPassword(user, loginRequest.getPassword())
                        .flatMap(match -> match ? Mono.just(ResponseEntity.ok("Login successful"))
                                : Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials"))))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found")));
    }
}
