package com.elducche.postservice.utils;

import com.elducche.postservice.models.Post;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utilitaire pour mapper les résultats de requêtes natives vers des objets Post
 */
public class PostMapper {
    
    // Constructeur privé pour empêcher l'instanciation
    private PostMapper() {
        throw new IllegalStateException("Utility class");
    }
    
    /**
     * Convertit un Object[] (résultat de requête native) vers un Post avec authorUsername
     */
    public static Post mapToPostWithAuthor(Object[] row) {
        try {
            System.out.println("[DEBUG] PostMapper - Début mapping, row length: " + row.length);
            for (int i = 0; i < row.length; i++) {
                System.out.println("[DEBUG] row[" + i + "] = " + row[i] + " (type: " + (row[i] != null ? row[i].getClass().getSimpleName() : "null") + ")");
            }
            
            // Si row[0] est un Object[], on prend cet Object[] comme données réelles
            Object[] actualRow = row;
            if (row.length == 1 && row[0] instanceof Object[]) {
                actualRow = (Object[]) row[0];
                System.out.println("[DEBUG] Utilisation du Object[] imbriqué, nouvelle longueur: " + actualRow.length);
                for (int i = 0; i < actualRow.length; i++) {
                    System.out.println("[DEBUG] actualRow[" + i + "] = " + actualRow[i] + " (type: " + (actualRow[i] != null ? actualRow[i].getClass().getSimpleName() : "null") + ")");
                }
            }
            
            Post post = new Post();
            
            // Gestion sécurisée des types avec null checks
            post.setId(actualRow[0] != null ? ((Number) actualRow[0]).longValue() : null);
            post.setTitle(actualRow[1] != null ? (String) actualRow[1] : "");
            post.setContent(actualRow[2] != null ? (String) actualRow[2] : "");
            post.setAuthorId(actualRow[3] != null ? ((Number) actualRow[3]).longValue() : null);
            post.setThemeId(actualRow[4] != null ? ((Number) actualRow[4]).longValue() : null);
            
            // Gestion des timestamps avec conversion appropriée
            if (actualRow[5] != null) {
                if (actualRow[5] instanceof LocalDateTime) {
                    post.setCreatedAt((LocalDateTime) actualRow[5]);
                } else if (actualRow[5] instanceof java.sql.Timestamp) {
                    post.setCreatedAt(((java.sql.Timestamp) actualRow[5]).toLocalDateTime());
                } else if (actualRow[5] instanceof java.time.Instant) {
                    post.setCreatedAt(LocalDateTime.ofInstant((java.time.Instant) actualRow[5], java.time.ZoneOffset.UTC));
                }
            }
            
            if (actualRow[6] != null) {
                if (actualRow[6] instanceof LocalDateTime) {
                    post.setUpdatedAt((LocalDateTime) actualRow[6]);
                } else if (actualRow[6] instanceof java.sql.Timestamp) {
                    post.setUpdatedAt(((java.sql.Timestamp) actualRow[6]).toLocalDateTime());
                } else if (actualRow[6] instanceof java.time.Instant) {
                    post.setUpdatedAt(LocalDateTime.ofInstant((java.time.Instant) actualRow[6], java.time.ZoneOffset.UTC));
                }
            }
            
            // Username depuis la jointure
            post.setAuthorUsername(actualRow[7] != null ? (String) actualRow[7] : null);
            
            System.out.println("[DEBUG] PostMapper - Mapping réussi pour post ID: " + post.getId());
            return post;
            
        } catch (Exception e) {
            System.err.println("[ERROR] PostMapper - Erreur lors du mapping: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors du mapping Post: " + e.getMessage(), e);
        }
    }
    
    /**
     * Convertit une liste d'Object[] vers une liste de Post avec authorUsername
     */
    public static List<Post> mapToPostsWithAuthor(List<Object[]> rows) {
        return rows.stream()
                .map(PostMapper::mapToPostWithAuthor)
                .collect(Collectors.toList());
    }
}
