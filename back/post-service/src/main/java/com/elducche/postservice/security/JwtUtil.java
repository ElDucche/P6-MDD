package com.elducche.postservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private String secret = System.getenv("JWT_SECRET");

    private Key getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token).getBody();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Extrait l'ID utilisateur du token JWT
     * 
     * @param token Le token JWT
     * @return L'ID de l'utilisateur
     */
    public Long getUserIdFromToken(String token) {
        return getClaims(token).get("userId", Long.class);
    }

    /**
     * Extrait le nom d'utilisateur du token JWT à partir des claims
     * 
     * @param token Le token JWT
     * @return Le nom d'utilisateur
     */
    public String getUsernameFromClaims(String token) {
        return getClaims(token).get("username", String.class);
    }

    /**
     * Extrait le token du header Authorization
     * 
     * @param authorizationHeader Le header Authorization
     * @return Le token JWT sans le préfixe "Bearer "
     */
    public String extractTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaims(token).getExpiration();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
}
