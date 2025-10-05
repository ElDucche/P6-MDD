package com.elducche.mdd.util;

import com.elducche.mdd.entity.User;
import com.elducche.mdd.entity.Theme;
import com.elducche.mdd.entity.Post;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Utilitaire pour créer des données de test uniques
 */
public class TestDataUtil {
    
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * Crée un utilisateur unique pour les tests
     */
    public static User createUniqueUser() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        User user = new User();
        user.setEmail("user" + uuid + "@test.com");
        user.setUsername("user" + uuid);
        user.setPassword(passwordEncoder.encode("Password123!"));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }
    
    /**
     * Crée un thème unique pour les tests
     */
    public static Theme createUniqueTheme() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        Theme theme = new Theme();
        theme.setTitle("Theme " + uuid);
        theme.setDescription("Description for theme " + uuid);
        theme.setCreatedAt(LocalDateTime.now());
        theme.setUpdatedAt(LocalDateTime.now());
        return theme;
    }
    
    /**
     * Crée un post unique pour les tests
     */
    public static Post createUniquePost(User author, Theme theme) {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        Post post = new Post();
        post.setTitle("Post " + uuid);
        post.setContent("Content for post " + uuid);
        post.setAuthor(author);
        post.setTheme(theme);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        return post;
    }
}
