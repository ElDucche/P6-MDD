package com.elducche.postservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Client Feign pour communiquer avec le user-service
 * Utilise Eureka pour la découverte de service
 */
@FeignClient(name = "user-service", path = "/api/user")
public interface UserServiceClient {
    
    /**
     * Récupère les informations d'un utilisateur par son ID
     * 
     * @param userId L'ID de l'utilisateur
     * @param authorizationHeader Le header Authorization avec le token JWT
     * @return Les informations de l'utilisateur
     */
    @GetMapping("/{userId}")
    UserDto getUserById(@PathVariable("userId") Long userId, 
                       @RequestHeader("Authorization") String authorizationHeader);
    
    /**
     * Valide un token JWT et récupère les informations utilisateur
     * 
     * @param authorizationHeader Le header Authorization avec le token JWT
     * @return Les informations de l'utilisateur connecté
     */
    @GetMapping("/me")
    UserDto getCurrentUser(@RequestHeader("Authorization") String authorizationHeader);
}
