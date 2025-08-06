package com.elducche.postservice.service;

import com.elducche.postservice.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service pour extraire les informations utilisateur depuis les tokens JWT.
 * Simplifie l'accès aux données utilisateur dans les contrôleurs.
 */
@Service
public class UserContextService {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * DTO contenant les informations utilisateur extraites du token
     */
    public static class UserInfo {
        private final Long userId;
        private final String email;
        private final String username;

        public UserInfo(Long userId, String email, String username) {
            this.userId = userId;
            this.email = email;
            this.username = username;
        }

        public Long getUserId() { return userId; }
        public String getEmail() { return email; }
        public String getUsername() { return username; }
        
        @Override
        public String toString() {
            return "UserInfo{userId=" + userId + ", email='" + email + "', username='" + username + "'}";
        }
    }

    /**
     * Extrait les informations utilisateur du header Authorization
     * 
     * @param authorizationHeader Le header Authorization contenant le token JWT
     * @return Les informations utilisateur ou null si le token est invalide
     */
    public UserInfo getUserInfo(String authorizationHeader) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authorizationHeader);
            if (token == null || !jwtUtil.validateToken(token)) {
                return null;
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            String email = jwtUtil.getUsernameFromToken(token); // Subject = email
            String username = jwtUtil.getUsernameFromClaims(token);

            return new UserInfo(userId, email, username);
        } catch (Exception e) {
            System.out.println("[USER-CONTEXT] Erreur lors de l'extraction des informations utilisateur : " + e.getMessage());
            return null;
        }
    }

    /**
     * Extrait seulement l'ID utilisateur du header Authorization
     * 
     * @param authorizationHeader Le header Authorization contenant le token JWT
     * @return L'ID de l'utilisateur ou null si le token est invalide
     */
    public Long getUserId(String authorizationHeader) {
        UserInfo userInfo = getUserInfo(authorizationHeader);
        return userInfo != null ? userInfo.getUserId() : null;
    }

    /**
     * Vérifie si l'utilisateur est authentifié
     * 
     * @param authorizationHeader Le header Authorization
     * @return true si l'utilisateur est authentifié, false sinon
     */
    public boolean isAuthenticated(String authorizationHeader) {
        return getUserInfo(authorizationHeader) != null;
    }
}
