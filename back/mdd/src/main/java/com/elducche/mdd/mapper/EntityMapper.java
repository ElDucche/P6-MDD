package com.elducche.mdd.mapper;

import com.elducche.mdd.dto.PostDTO;
import com.elducche.mdd.dto.CommentDTO;
import com.elducche.mdd.dto.SubscriptionDTO;
import com.elducche.mdd.entity.Post;
import com.elducche.mdd.entity.Comment;
import com.elducche.mdd.entity.Subscription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Mapper pour convertir les entités en DTOs
 * 
 * Cette classe permet d'éviter les références circulaires lors de la sérialisation JSON
 */
@Slf4j
@Component
public class EntityMapper {
    
    /**
     * Convertit un Post en PostDTO
     */
    public PostDTO toPostDTO(Post post) {
        if (post == null) {
            log.warn("Tentative de conversion d'un post null");
            return null;
        }
        
        try {
            PostDTO.AuthorDTO authorDTO = null;
            if (post.getAuthor() != null) {
                authorDTO = new PostDTO.AuthorDTO(
                    post.getAuthor().getId(),
                    post.getAuthor().getUsername()
                );
            } else {
                log.warn("Post {} sans auteur", post.getId());
            }
            
            PostDTO.ThemeDTO themeDTO = null;
            if (post.getTheme() != null) {
                themeDTO = new PostDTO.ThemeDTO(
                    post.getTheme().getId(),
                    post.getTheme().getTitle(),
                    post.getTheme().getDescription()
                );
            } else {
                log.warn("Post {} sans thème", post.getId());
            }
            
            return new PostDTO(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                authorDTO,
                themeDTO
            );
        } catch (Exception e) {
            log.error("Erreur lors de la conversion du post {} en DTO: {}", post.getId(), e.getMessage());
            throw e;
        }
    }
    
    /**
     * Convertit un Comment en CommentDTO
     */
    public CommentDTO toCommentDTO(Comment comment) {
        if (comment == null) {
            return null;
        }
        
        CommentDTO.AuthorDTO authorDTO = new CommentDTO.AuthorDTO(
            comment.getAuthor().getId(),
            comment.getAuthor().getUsername()
        );
        
        CommentDTO.PostInfoDTO postDTO = new CommentDTO.PostInfoDTO(
            comment.getPost().getId(),
            comment.getPost().getTitle()
        );
        
        return new CommentDTO(
            comment.getId(),
            comment.getContent(),
            comment.getCreatedAt(),
            comment.getUpdatedAt(),
            authorDTO,
            postDTO
        );
    }
    
    /**
     * Convertit un Subscription en SubscriptionDTO
     */
    public SubscriptionDTO toSubscriptionDTO(Subscription subscription) {
        if (subscription == null) {
            return null;
        }
        
        SubscriptionDTO.UserDTO userDTO = new SubscriptionDTO.UserDTO(
            subscription.getUser().getId(),
            subscription.getUser().getUsername(),
            subscription.getUser().getEmail()
        );
        
        SubscriptionDTO.ThemeDTO themeDTO = new SubscriptionDTO.ThemeDTO(
            subscription.getTheme().getId(),
            subscription.getTheme().getTitle(),
            subscription.getTheme().getDescription()
        );
        
        return new SubscriptionDTO(
            subscription.getId().getUserId(),
            subscription.getId().getThemeId(),
            subscription.getSubscribedAt(),
            userDTO,
            themeDTO
        );
    }
}
