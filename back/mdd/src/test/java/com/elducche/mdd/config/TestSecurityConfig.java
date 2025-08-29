package com.elducche.mdd.config;

import com.elducche.mdd.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Configuration de sécurité pour les tests
 * 
 * Utilise des matchers compatibles avec MockMVC pour éviter les problèmes
 * de résolution de servlet context dans les tests.
 */
@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configuration simplifiée pour les tests
     */
    @Bean
    @Primary
    public SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
        http
            // Désactiver CSRF pour les tests
            .csrf(csrf -> csrf.disable())
            
            // Politique de sessions (stateless)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Configuration des autorisations avec AntPathRequestMatcher pour compatibilité test
            .authorizeHttpRequests(auth -> auth
                // Endpoints d'authentification publics
                .requestMatchers(new AntPathRequestMatcher("/api/auth/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/**", "OPTIONS")).permitAll()
                
                // Endpoints de santé publics (pour monitoring)
                .requestMatchers(new AntPathRequestMatcher("/api/health")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/info")).permitAll()
                
                // Tous les autres endpoints nécessitent une authentification
                .anyRequest().authenticated()
            )
            
            // Configuration des exceptions d'authentification et d'autorisation
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(401);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Token manquant ou invalide\"}");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(403);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"Forbidden\",\"message\":\"Accès refusé\"}");
                })
            )
            
            // Ajouter le filtre JWT avant le filtre d'authentification standard
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }

    /**
     * Encoder de mots de passe pour les tests
     */
    @Bean
    public PasswordEncoder testPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
