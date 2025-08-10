package com.elducche.mdd.security;

import com.elducche.mdd.entity.User;
import com.elducche.mdd.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * Utilitaire pour la gestion du contexte de sécurité
 * 
 * Simplifie l'accès aux informations de l'utilisateur connecté
 * en utilisant le contexte de sécurité Spring plutôt que le token JWT
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityUtil {
    
    private final UserRepository userRepository;
    
    /**
     * Récupère l'email de l'utilisateur connecté depuis le contexte de sécurité
     */
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() 
            && !"anonymousUser".equals(authentication.getPrincipal())) {
            return authentication.getName(); // Principal = email dans notre cas
        }
        
        return null;
    }
    
    /**
     * Récupère l'utilisateur connecté complet depuis la base de données
     */
    public Optional<User> getCurrentUser() {
        String email = getCurrentUserEmail();
        
        if (email != null) {
            return userRepository.findByEmail(email);
        }
        
        return Optional.empty();
    }
    
    /**
     * Récupère l'ID de l'utilisateur connecté
     */
    public Long getCurrentUserId() {
        return getCurrentUser()
                .map(User::getId)
                .orElse(null);
    }
    
    /**
     * Vérifie si un utilisateur est connecté
     */
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() 
               && !"anonymousUser".equals(authentication.getPrincipal());
    }
    
    /**
     * Vérifie si l'utilisateur connecté correspond à l'ID donné
     */
    public boolean isCurrentUser(Long userId) {
        Long currentUserId = getCurrentUserId();
        return currentUserId != null && currentUserId.equals(userId);
    }
}
