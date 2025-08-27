package com.elducche.mdd.repository;

import com.elducche.mdd.entity.Comment;
import com.elducche.mdd.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Comment
 * 
 * Fournit les opérations CRUD et des méthodes optimisées
 * pour la récupération des commentaires avec leurs relations
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // Trouve les commentaires d'un post (pour les tests)
    List<Comment> findByPost(Post post);
    
    /**
     * Trouve tous les commentaires d'un post avec leurs relations
     * @param postId L'ID du post
     * @return Liste des commentaires triés par date de création
     */
    @Query("SELECT c FROM Comment c JOIN FETCH c.author WHERE c.post.id = :postId ORDER BY c.createdAt DESC")
    List<Comment> findByPostIdWithAuthor(@Param("postId") Long postId);
    
    /**
     * Trouve un commentaire par ID avec ses relations
     * @param id L'ID du commentaire
     * @return Optional contenant le commentaire avec ses relations
     */
    @Query("SELECT c FROM Comment c JOIN FETCH c.author JOIN FETCH c.post WHERE c.id = :id")
    Optional<Comment> findByIdWithAuthorAndPost(@Param("id") Long id);
    
    /**
     * Trouve les commentaires d'un auteur spécifique
     * @param authorId L'ID de l'auteur
     * @return Liste des commentaires de l'auteur
     */
    @Query("SELECT c FROM Comment c JOIN FETCH c.author JOIN FETCH c.post WHERE c.author.id = :authorId ORDER BY c.createdAt DESC")
    List<Comment> findByAuthorIdWithAuthorAndPost(@Param("authorId") Long authorId);
    
    /**
     * Récupère tous les commentaires d'un utilisateur avec les posts et thèmes associés
     */
    @Query("SELECT c FROM Comment c " +
           "JOIN FETCH c.author " +
           "JOIN FETCH c.post p " +
           "JOIN FETCH p.theme " +
           "WHERE c.author.id = :authorId " +
           "ORDER BY c.createdAt DESC")
    List<Comment> findByAuthorIdWithPostAndTheme(@Param("authorId") Long authorId);
    
    /**
     * Récupère tous les commentaires d'un post (simple)
     */
    List<Comment> findByPostId(Long postId);
    
    /**
     * Compte le nombre de commentaires d'un post
     * @param postId L'ID du post
     * @return Nombre de commentaires
     */
    long countByPostId(Long postId);
    
    /**
     * Trouve les commentaires les plus récents d'un utilisateur
     * @param userId L'ID de l'utilisateur
     * @param pageable Objet Pageable pour la limitation et le tri
     * @return Liste des commentaires récents
     */
    @Query("SELECT c FROM Comment c JOIN FETCH c.author JOIN FETCH c.post WHERE c.author.id = :userId ORDER BY c.createdAt DESC")
    List<Comment> findRecentCommentsByUserId(@Param("userId") Long userId, Pageable pageable);
}
