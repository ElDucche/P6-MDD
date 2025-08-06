package com.elducche.postservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String authToken = authHeader.substring(7);
            System.out.println("[JWT FILTER] Token reçu : " + authToken);
            if (jwtUtil.validateToken(authToken)) {
                // Extraire l'ID utilisateur du token JWT
                Long userId = jwtUtil.getUserIdFromToken(authToken);
                System.out.println("[JWT FILTER] UserId extrait : " + userId);
                
                // Utiliser l'userId comme principal pour que authentication.getName() retourne l'ID
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId.toString(), null, java.util.Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("[JWT FILTER] Authentication définie avec userId : " + userId);
            } else {
                System.out.println("[JWT FILTER] Token invalide");
            }
        } else {
            System.out.println("[JWT FILTER] Pas de token Authorization Bearer trouvé");
        }
        filterChain.doFilter(request, response);
    }
}
