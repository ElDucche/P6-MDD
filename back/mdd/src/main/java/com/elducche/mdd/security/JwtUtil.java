package com.elducche.mdd.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;

/**
 * Utilitaire simplifié pour la gestion des tokens JWT
 * 
 * Version simplifiée pour application monolithique :
 * - Génération et validation de tokens
 * - Extraction de l'email utilisateur (identifiant principal)
 * - Logique minimale et efficace
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private Key key;

    @PostConstruct
    public void init() {
        if (jwtSecret == null || jwtSecret.length() < 32) {
            throw new IllegalArgumentException("JWT_SECRET doit être défini et contenir au moins 32 caractères.");
        }
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        log.info("JWT Util initialisé avec succès");
    }

    /**
     * Génère un token JWT avec l'email, userId et username
     */
    public String generateToken(String email, Long userId, String username) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("username", username)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Extrait l'email du token (identifiant principal)
     */
    public String getEmailFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (JwtException e) {
            log.warn("Erreur lors de l'extraction de l'email du token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Valide un token JWT
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            return claims.getExpiration().after(new Date());
        } catch (JwtException e) {
            log.warn("Token JWT invalide: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extrait l'email si le token est valide, null sinon
     */
    public String getValidatedEmail(String token) {
        if (isTokenValid(token)) {
            return getEmailFromToken(token);
        }
        return null;
    }
}
