package com.elducche.postservice.utils;

import com.elducche.postservice.models.Comment;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utilitaire pour mapper les résultats de requêtes natives vers des objets Comment
 */
public class CommentMapper {
    
    // Constructeur privé pour empêcher l'instanciation
    private CommentMapper() {
        throw new IllegalStateException("Utility class");
    }
    
    /**
     * Convertit un Object[] (résultat de requête native) vers un Comment avec authorUsername
     */
    public static Comment mapToCommentWithAuthor(Object[] row) {
        try {
            // Gestion du cas Object[] imbriqué comme pour PostMapper
            Object[] actualRow = row;
            if (row.length == 1 && row[0] instanceof Object[]) {
                actualRow = (Object[]) row[0];
            }
            
            Comment comment = new Comment();
            
            // Gestion sécurisée des types avec null checks
            comment.setId(actualRow[0] != null ? ((Number) actualRow[0]).longValue() : null);
            comment.setContent(actualRow[1] != null ? (String) actualRow[1] : "");
            comment.setAuthorId(actualRow[2] != null ? ((Number) actualRow[2]).longValue() : null);
            comment.setPostId(actualRow[3] != null ? ((Number) actualRow[3]).longValue() : null);
            
            // Gestion des timestamps avec conversion appropriée
            if (actualRow[4] != null) {
                if (actualRow[4] instanceof LocalDateTime) {
                    comment.setCreatedAt((LocalDateTime) actualRow[4]);
                } else if (actualRow[4] instanceof java.sql.Timestamp) {
                    comment.setCreatedAt(((java.sql.Timestamp) actualRow[4]).toLocalDateTime());
                } else if (actualRow[4] instanceof java.time.Instant) {
                    comment.setCreatedAt(LocalDateTime.ofInstant((java.time.Instant) actualRow[4], java.time.ZoneOffset.UTC));
                }
            }
            
            if (actualRow[5] != null) {
                if (actualRow[5] instanceof LocalDateTime) {
                    comment.setUpdatedAt((LocalDateTime) actualRow[5]);
                } else if (actualRow[5] instanceof java.sql.Timestamp) {
                    comment.setUpdatedAt(((java.sql.Timestamp) actualRow[5]).toLocalDateTime());
                } else if (actualRow[5] instanceof java.time.Instant) {
                    comment.setUpdatedAt(LocalDateTime.ofInstant((java.time.Instant) actualRow[5], java.time.ZoneOffset.UTC));
                }
            }
            
            // Username depuis la jointure
            comment.setAuthorUsername(actualRow[6] != null ? (String) actualRow[6] : null);
            
            return comment;
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du mapping Comment: " + e.getMessage(), e);
        }
    }
    
    /**
     * Convertit une liste d'Object[] vers une liste de Comment avec authorUsername
     */
    public static List<Comment> mapToCommentsWithAuthor(List<Object[]> rows) {
        return rows.stream()
                .map(CommentMapper::mapToCommentWithAuthor)
                .collect(Collectors.toList());
    }
}
