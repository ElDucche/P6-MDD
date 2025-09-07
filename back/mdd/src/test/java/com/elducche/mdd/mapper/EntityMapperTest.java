package com.elducche.mdd.mapper;

import com.elducche.mdd.dto.CommentResponseDTO;
import com.elducche.mdd.dto.PostDTO;
import com.elducche.mdd.dto.SubscriptionResponseDTO;
import com.elducche.mdd.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitaires pour EntityMapper
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests EntityMapper")
class EntityMapperTest {

    @InjectMocks
    private EntityMapper entityMapper;

    private User testUser;
    private Theme testTheme;
    private Post testPost;
    private Comment testComment;
    private Subscription testSubscription;

    @BeforeEach
    void setUp() {
        // Création des objets de test
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        testTheme = new Theme();
        testTheme.setId(1L);
        testTheme.setTitle("Test Theme");
        testTheme.setDescription("Description du thème de test");
        testTheme.setCreatedAt(LocalDateTime.now());
        testTheme.setUpdatedAt(LocalDateTime.now());

        testPost = new Post();
        testPost.setId(1L);
        testPost.setTitle("Test Post");
        testPost.setContent("Contenu du post de test");
        testPost.setAuthor(testUser);
        testPost.setTheme(testTheme);
        testPost.setCreatedAt(LocalDateTime.now());
        testPost.setUpdatedAt(LocalDateTime.now());

        testComment = new Comment();
        testComment.setId(1L);
        testComment.setContent("Commentaire de test");
        testComment.setAuthor(testUser);
        testComment.setPost(testPost);
        testComment.setCreatedAt(LocalDateTime.now());
        testComment.setUpdatedAt(LocalDateTime.now());

        SubscriptionId subscriptionId = new SubscriptionId(testUser.getId(), testTheme.getId());
        testSubscription = new Subscription();
        testSubscription.setId(subscriptionId);
        testSubscription.setUser(testUser);
        testSubscription.setTheme(testTheme);
        testSubscription.setSubscribedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Conversion Post vers PostDTO - Succès")
    void testToPostDTO_Success() {
        // When
        PostDTO postDTO = entityMapper.toPostDTO(testPost);

        // Then
        assertThat(postDTO).isNotNull();
        assertThat(postDTO.getId()).isEqualTo(testPost.getId());
        assertThat(postDTO.getTitle()).isEqualTo(testPost.getTitle());
        assertThat(postDTO.getContent()).isEqualTo(testPost.getContent());
        assertThat(postDTO.getCreatedAt()).isEqualTo(testPost.getCreatedAt());
        assertThat(postDTO.getUpdatedAt()).isEqualTo(testPost.getUpdatedAt());

        // Vérifications auteur
        assertThat(postDTO.getAuthor()).isNotNull();
        assertThat(postDTO.getAuthor().getId()).isEqualTo(testUser.getId());
        assertThat(postDTO.getAuthor().getUsername()).isEqualTo(testUser.getUsername());
        assertThat(postDTO.getAuthor().getEmail()).isEqualTo(testUser.getEmail());

        // Vérifications thème
        assertThat(postDTO.getTheme()).isNotNull();
        assertThat(postDTO.getTheme().getId()).isEqualTo(testTheme.getId());
        assertThat(postDTO.getTheme().getTitle()).isEqualTo(testTheme.getTitle());
        assertThat(postDTO.getTheme().getDescription()).isEqualTo(testTheme.getDescription());
    }

    @Test
    @DisplayName("Conversion Post null vers PostDTO")
    void testToPostDTO_NullPost() {
        // When
        PostDTO postDTO = entityMapper.toPostDTO(null);

        // Then
        assertThat(postDTO).isNull();
    }

    @Test
    @DisplayName("Conversion Comment vers CommentResponseDTO - Succès")
    void testToCommentResponseDTO_Success() {
        // When
        CommentResponseDTO commentDTO = entityMapper.toCommentResponseDTO(testComment);

        // Then
        assertThat(commentDTO).isNotNull();
        assertThat(commentDTO.getId()).isEqualTo(testComment.getId());
        assertThat(commentDTO.getContent()).isEqualTo(testComment.getContent());
        assertThat(commentDTO.getCreatedAt()).isEqualTo(testComment.getCreatedAt());
        assertThat(commentDTO.getUpdatedAt()).isEqualTo(testComment.getUpdatedAt());

        // Vérifications auteur
        assertThat(commentDTO.getAuthor()).isNotNull();
        assertThat(commentDTO.getAuthor().getId()).isEqualTo(testUser.getId());
        assertThat(commentDTO.getAuthor().getUsername()).isEqualTo(testUser.getUsername());
        assertThat(commentDTO.getAuthor().getEmail()).isEqualTo(testUser.getEmail());

        // Vérifications post
        assertThat(commentDTO.getPost()).isNotNull();
        assertThat(commentDTO.getPost().getId()).isEqualTo(testPost.getId());
        assertThat(commentDTO.getPost().getTitle()).isEqualTo(testPost.getTitle());
    }

    @Test
    @DisplayName("Conversion Comment null vers CommentResponseDTO")
    void testToCommentResponseDTO_NullComment() {
        // When
        CommentResponseDTO commentDTO = entityMapper.toCommentResponseDTO(null);

        // Then
        assertThat(commentDTO).isNull();
    }

    @Test
    @DisplayName("Conversion Subscription vers SubscriptionResponseDTO - Succès")
    void testToSubscriptionResponseDTO_Success() {
        // When
        SubscriptionResponseDTO subscriptionDTO = entityMapper.toSubscriptionResponseDTO(testSubscription);

        // Then
        assertThat(subscriptionDTO).isNotNull();
        assertThat(subscriptionDTO.getUserId()).isEqualTo(testUser.getId());
        assertThat(subscriptionDTO.getThemeId()).isEqualTo(testTheme.getId());
        assertThat(subscriptionDTO.getSubscribedAt()).isEqualTo(testSubscription.getSubscribedAt());

        // Vérifications utilisateur
        assertThat(subscriptionDTO.getUser()).isNotNull();
        assertThat(subscriptionDTO.getUser().getId()).isEqualTo(testUser.getId());
        assertThat(subscriptionDTO.getUser().getUsername()).isEqualTo(testUser.getUsername());
        assertThat(subscriptionDTO.getUser().getEmail()).isEqualTo(testUser.getEmail());

        // Vérifications thème
        assertThat(subscriptionDTO.getTheme()).isNotNull();
        assertThat(subscriptionDTO.getTheme().getId()).isEqualTo(testTheme.getId());
        assertThat(subscriptionDTO.getTheme().getTitle()).isEqualTo(testTheme.getTitle());
        assertThat(subscriptionDTO.getTheme().getDescription()).isEqualTo(testTheme.getDescription());
    }

    @Test
    @DisplayName("Conversion Subscription null vers SubscriptionResponseDTO")
    void testToSubscriptionResponseDTO_NullSubscription() {
        // When
        SubscriptionResponseDTO subscriptionDTO = entityMapper.toSubscriptionResponseDTO(null);

        // Then
        assertThat(subscriptionDTO).isNull();
    }
}
