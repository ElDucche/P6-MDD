package com.elducche.userservice.controller;

import com.elducche.userservice.model.User;
import com.elducche.userservice.service.UserService;
import com.elducche.userservice.service.AuthService;
import com.elducche.userservice.model.dto.LoginRequest;
import com.elducche.userservice.model.dto.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> register(@RequestBody User user) {
        System.out.println("[USER-SERVICE] User reçu : " + user);
        if (userService.findByEmail(user.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already in use");
        }
        userService.register(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("Inscription réussie !");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        String token = authService.login(loginRequest);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
