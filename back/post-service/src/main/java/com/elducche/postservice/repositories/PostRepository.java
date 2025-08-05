package com.elducche.postservice.repositories;

import com.elducche.postservice.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    // Requêtes existantes sans jointure (pour compatibilité)
    List<Post> findByThemeId(Long themeId);
    List<Post> findByAuthorId(Long authorId);
    List<Post> findByThemeIdIn(List<Long> themeIds);
    
    // Nouvelles requêtes avec jointure pour inclure le username
    
    @Query(value = """
        SELECT p.id, p.title, p.content, p.author_id, p.theme_id, 
               p.created_at, p.updated_at, u.username as author_username
        FROM posts p 
        LEFT JOIN "USERS" u ON p.author_id = u.id 
        ORDER BY p.created_at DESC
        """, nativeQuery = true)
    List<Object[]> findAllWithAuthorUsername();
    
    @Query(value = """
        SELECT p.id, p.title, p.content, p.author_id, p.theme_id, 
               p.created_at, p.updated_at, u.username as author_username
        FROM posts p 
        LEFT JOIN "USERS" u ON p.author_id = u.id 
        WHERE p.id = :id
        """, nativeQuery = true)
    Optional<Object[]> findByIdWithAuthorUsername(@Param("id") Long id);
    
    @Query(value = """
        SELECT p.id, p.title, p.content, p.author_id, p.theme_id, 
               p.created_at, p.updated_at, u.username as author_username
        FROM posts p 
        LEFT JOIN "USERS" u ON p.author_id = u.id 
        WHERE p.theme_id = :themeId 
        ORDER BY p.created_at DESC
        """, nativeQuery = true)
    List<Object[]> findByThemeIdWithAuthorUsername(@Param("themeId") Long themeId);
    
    @Query(value = """
        SELECT p.id, p.title, p.content, p.author_id, p.theme_id, 
               p.created_at, p.updated_at, u.username as author_username
        FROM posts p 
        LEFT JOIN "USERS" u ON p.author_id = u.id 
        WHERE p.theme_id IN :themeIds 
        ORDER BY p.created_at DESC
        """, nativeQuery = true)
    List<Object[]> findByThemeIdInWithAuthorUsername(@Param("themeIds") List<Long> themeIds);
}
