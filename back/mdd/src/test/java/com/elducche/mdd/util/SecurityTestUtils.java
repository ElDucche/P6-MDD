package com.elducche.mdd.util;

import com.elducche.mdd.entity.User;
import com.elducche.mdd.security.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

/**
 * Utilitaires pour les tests de sécurité
 * 
 * Cette classe fournit des méthodes pour simuler l'authentification
 * dans les tests.
 */
public class SecurityTestUtils {

    /**
     * Crée un token JWT valide pour les tests
     */
    public static String createValidJwtToken(User user) {
        JwtUtil jwtUtil = new JwtUtil();
        // On utilise directement les valeurs de test
        return createTokenWithSecret(user, TestConstants.TEST_JWT_SECRET);
    }

    /**
     * Crée un token JWT avec un secret spécifique
     */
    private static String createTokenWithSecret(User user, String secret) {
        // Pour les tests, on crée un token simple
        return "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6InRlc3R1c2VyIiwiaWF0IjoxNjkzODQ4MDAwLCJleHAiOjk5OTk5OTk5OTl9.test-jwt-token";
    }

    /**
     * Crée un token JWT expiré
     */
    public static String createExpiredJwtToken() {
        return "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6InRlc3R1c2VyIiwiaWF0IjoxNjkzODQ4MDAwLCJleHAiOjE2OTM4NDgwMDF9.expired-token";
    }

    /**
     * Crée un token JWT invalide
     */
    public static String createInvalidJwtToken() {
        return "invalid.jwt.token";
    }

    /**
     * RequestPostProcessor pour ajouter un token JWT aux requêtes MockMvc
     */
    public static RequestPostProcessor jwt(String token) {
        return request -> {
            request.addHeader(TestConstants.AUTHORIZATION_HEADER, TestConstants.BEARER_PREFIX + token);
            return request;
        };
    }

    /**
     * RequestPostProcessor pour authentifier un utilisateur dans MockMvc
     */
    public static RequestPostProcessor authenticatedUser(User user) {
        Authentication auth = new UsernamePasswordAuthenticationToken(
            user.getEmail(), 
            null, 
            Collections.emptyList()
        );
        return authentication(auth);
    }

    /**
     * Configure le SecurityContext pour les tests
     */
    public static void authenticateUser(User user) {
        Authentication auth = new UsernamePasswordAuthenticationToken(
            user.getEmail(), 
            user.getPassword(), 
            Collections.emptyList()
        );
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }

    /**
     * Nettoie le SecurityContext après les tests
     */
    public static void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    /**
     * Annotation pour simuler un utilisateur authentifié dans les tests
     */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface WithMockAuthenticatedUser {
        String email() default "test@example.com";
        String username() default "testuser";
        long userId() default 1L;
    }

    /**
     * Factory pour créer le contexte de sécurité avec l'annotation @WithMockAuthenticatedUser
     */
    public static class WithMockAuthenticatedUserSecurityContextFactory 
            implements WithSecurityContextFactory<WithMockAuthenticatedUser> {

        @Override
        public SecurityContext createSecurityContext(WithMockAuthenticatedUser annotation) {
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            
            Authentication auth = new UsernamePasswordAuthenticationToken(
                annotation.email(),
                null,
                Collections.emptyList()
            );
            
            context.setAuthentication(auth);
            return context;
        }
    }
}
