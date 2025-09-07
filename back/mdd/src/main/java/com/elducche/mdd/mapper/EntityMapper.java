package com.elducche.mdd.mapper;

import com.elducche.mdd.dto.PostDTO;
import com.elducche.mdd.dto.CommentResponseDTO;
import com.elducche.mdd.dto.SubscriptionResponseDTO;
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
                    post.getAuthor().getUsername(),
                    post.getAuthor().getEmail()
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
     * Convertit un Comment en CommentResponseDTO avec informations complètes
     */
    public CommentResponseDTO toCommentResponseDTO(Comment comment) {
        if (comment == null) {
            return null;
        }
        
        CommentResponseDTO.UserInfo authorInfo = null;
        if (comment.getAuthor() != null) {
            authorInfo = CommentResponseDTO.UserInfo.builder()
                .id(comment.getAuthor().getId())
                .username(comment.getAuthor().getUsername())
                .email(comment.getAuthor().getEmail())
                .build();
        }
        
        CommentResponseDTO.PostInfo postInfo = null;
        if (comment.getPost() != null) {
            postInfo = CommentResponseDTO.PostInfo.builder()
                .id(comment.getPost().getId())
                .title(comment.getPost().getTitle())
                .build();
        }
        
        return CommentResponseDTO.builder()
            .id(comment.getId())
            .content(comment.getContent())
            .createdAt(comment.getCreatedAt())
            .updatedAt(comment.getUpdatedAt())
            .author(authorInfo)
            .post(postInfo)
            .build();
    }
    
    /**
     * Convertit un Subscription en SubscriptionResponseDTO
     */
    public SubscriptionResponseDTO toSubscriptionResponseDTO(Subscription subscription) {
        if (subscription == null) {
            return null;
        }
        
        SubscriptionResponseDTO.UserInfo userInfo = null;
        if (subscription.getUser() != null) {
            userInfo = new SubscriptionResponseDTO.UserInfo(
                subscription.getUser().getId(),
                subscription.getUser().getUsername(),
                subscription.getUser().getEmail()
            );
        }
        
        SubscriptionResponseDTO.ThemeInfo themeInfo = null;
        if (subscription.getTheme() != null) {
            themeInfo = new SubscriptionResponseDTO.ThemeInfo(
                subscription.getTheme().getId(),
                subscription.getTheme().getTitle(),
                subscription.getTheme().getDescription()
            );
        }
        
        return new SubscriptionResponseDTO(
            subscription.getId().getUserId(),
            subscription.getId().getThemeId(),
            subscription.getSubscribedAt(),
            userInfo,
            themeInfo
        );
    }
}
