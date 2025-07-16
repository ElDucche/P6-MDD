package com.elducche.userservice.controller;

import com.elducche.userservice.model.User;
import com.elducche.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public Mono<ResponseEntity<User>> getUser(Authentication authentication) {
        return userService.findByEmail(authentication.getName())
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("")
    public Mono<ResponseEntity<User>> updateUser(Authentication authentication, @RequestBody User user) {
        return userService.updateUser(authentication.getName(), user)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
