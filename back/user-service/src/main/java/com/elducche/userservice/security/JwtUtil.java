package com.elducche.userservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    private final String jwtSecret = System.getenv("JWT_SECRET");

    private Key key;

    @PostConstruct
    public void init() {
        if (jwtSecret == null || jwtSecret.length() < 32) {
            throw new IllegalArgumentException("JWT_SECRET doit être défini et contenir au moins 32 caractères.");
        }
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public Claims getAllClaimsFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            System.out.println("[JWT] Token reçu : " + token);
            System.out.println("[JWT] Claims : " + claims);
            return claims;
        } catch (io.jsonwebtoken.security.SignatureException se) {
            System.out.println("[JWT] Erreur de signature lors du parsing du token : " + token);
            se.printStackTrace();
            throw se;
        } catch (io.jsonwebtoken.MalformedJwtException me) {
            System.out.println("[JWT] Token mal formé : " + token);
            me.printStackTrace();
            throw me;
        } catch (Exception e) {
            System.out.println("[JWT] Erreur inconnue lors du parsing du token : " + token);
            e.printStackTrace();
            throw e;
        }
    }

    public String getUsernameFromToken(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    public Date getExpirationDateFromToken(String token) {
        return getAllClaimsFromToken(token).getExpiration();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Boolean validateToken(String token) {
        try {
            boolean expired = isTokenExpired(token);
            System.out.println("[JWT] Validation du token : " + token + " | Expiré : " + expired);
            return !expired;
        } catch (Exception e) {
            System.out.println("[JWT] Erreur lors de la validation du token : " + token);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Génère un token JWT enrichi avec les informations utilisateur
     * 
     * @param userId L'ID de l'utilisateur
     * @param email L'email de l'utilisateur
     * @param username Le nom d'utilisateur
     * @return Le token JWT
     */
    public String generateToken(Long userId, String email, String username) {
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("username", username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrait l'ID utilisateur du token JWT
     * 
     * @param token Le token JWT
     * @return L'ID de l'utilisateur
     */
    public Long getUserIdFromToken(String token) {
        return getAllClaimsFromToken(token).get("userId", Long.class);
    }

    /**
     * Extrait le nom d'utilisateur du token JWT à partir des claims
     * 
     * @param token Le token JWT
     * @return Le nom d'utilisateur
     */
    public String getUsernameFromClaims(String token) {
        return getAllClaimsFromToken(token).get("username", String.class);
    }
}
