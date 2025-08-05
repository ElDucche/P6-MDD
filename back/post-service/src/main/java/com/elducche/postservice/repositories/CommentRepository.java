package com.elducche.postservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.elducche.postservice.models.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);
    
    // Requête avec JOIN pour récupérer les usernames
    @Query(value = """
        SELECT c.id, c.content, c.author_id, c.post_id, 
               c.created_at, c.updated_at, u.username as author_username
        FROM comments c 
        LEFT JOIN users u ON c.author_id = u.id 
        WHERE c.post_id = :postId 
        ORDER BY c.created_at DESC
        """, nativeQuery = true)
    List<Object[]> findByPostIdWithAuthorUsername(@Param("postId") Long postId);
    
    // Requête pour récupérer un commentaire par ID avec le username
    @Query(value = """
        SELECT c.id, c.content, c.author_id, c.post_id, 
               c.created_at, c.updated_at, u.username as author_username
        FROM comments c 
        LEFT JOIN users u ON c.author_id = u.id 
        WHERE c.id = :commentId
        """, nativeQuery = true)
    Optional<Object[]> findByIdWithAuthorUsername(@Param("commentId") Long commentId);
}
