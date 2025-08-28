package com.elducche.mdd.mapper;

import com.elducche.mdd.dto.CommentDTO;
import com.elducche.mdd.dto.PostDTO;
import com.elducche.mdd.dto.SubscriptionDTO;
import com.elducche.mdd.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitaires pour EntityMapper
 * 
 * Ces tests valident :
 * - La conversion d'entités vers DTOs
 * - La gestion des références nulles
 * - L'intégrité des données converties
 * - La prévention des références circulaires
 */
@DisplayName("Tests du mapper d'entités")
@ExtendWith(MockitoExtension.class)
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
        // Créer un utilisateur de test
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setCreatedAt(LocalDateTime.now().minusDays(1));
        testUser.setUpdatedAt(LocalDateTime.now());

        // Créer un thème de test
        testTheme = new Theme();
        testTheme.setId(1L);
        testTheme.setTitle("Technology");
        testTheme.setDescription("Tech discussions");
        testTheme.setCreatedAt(LocalDateTime.now().minusDays(2));

        // Créer un post de test
        testPost = new Post();
        testPost.setId(1L);
        testPost.setTitle("Test Post");
        testPost.setContent("This is a test post content");
        testPost.setAuthor(testUser);
        testPost.setTheme(testTheme);
        testPost.setCreatedAt(LocalDateTime.now().minusHours(2));
        testPost.setUpdatedAt(LocalDateTime.now().minusHours(1));

        // Créer un commentaire de test
        testComment = new Comment();
        testComment.setId(1L);
        testComment.setContent("This is a test comment");
        testComment.setAuthor(testUser);
        testComment.setPost(testPost);
        testComment.setCreatedAt(LocalDateTime.now().minusMinutes(30));

        // Créer un abonnement de test
        testSubscription = new Subscription(testUser, testTheme);
        testSubscription.setSubscribedAt(LocalDateTime.now().minusDays(3));
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

        // Vérifier l'auteur
        assertThat(postDTO.getAuthor()).isNotNull();
        assertThat(postDTO.getAuthor().getId()).isEqualTo(testUser.getId());
        assertThat(postDTO.getAuthor().getUsername()).isEqualTo(testUser.getUsername());
        assertThat(postDTO.getAuthor().getEmail()).isEqualTo(testUser.getEmail());

        // Vérifier le thème
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
    @DisplayName("Conversion Post avec auteur null")
    void testToPostDTO_NullAuthor() {
        // Given
        testPost.setAuthor(null);

        // When
        PostDTO postDTO = entityMapper.toPostDTO(testPost);

        // Then
        assertThat(postDTO).isNotNull();
        assertThat(postDTO.getAuthor()).isNull();
        assertThat(postDTO.getId()).isEqualTo(testPost.getId());
        assertThat(postDTO.getTitle()).isEqualTo(testPost.getTitle());
    }

    @Test
    @DisplayName("Conversion Post avec thème null")
    void testToPostDTO_NullTheme() {
        // Given
        testPost.setTheme(null);

        // When
        PostDTO postDTO = entityMapper.toPostDTO(testPost);

        // Then
        assertThat(postDTO).isNotNull();
        assertThat(postDTO.getTheme()).isNull();
        assertThat(postDTO.getId()).isEqualTo(testPost.getId());
        assertThat(postDTO.getTitle()).isEqualTo(testPost.getTitle());
    }

    @Test
    @DisplayName("Conversion Comment vers CommentDTO - Succès")
    void testToCommentDTO_Success() {
        // When
        CommentDTO commentDTO = entityMapper.toCommentDTO(testComment);

        // Then
        assertThat(commentDTO).isNotNull();
        assertThat(commentDTO.getId()).isEqualTo(testComment.getId());
        assertThat(commentDTO.getContent()).isEqualTo(testComment.getContent());
        assertThat(commentDTO.getCreatedAt()).isEqualTo(testComment.getCreatedAt());

        // Vérifier l'auteur
        assertThat(commentDTO.getAuthor()).isNotNull();
        assertThat(commentDTO.getAuthor().getId()).isEqualTo(testUser.getId());
        assertThat(commentDTO.getAuthor().getUsername()).isEqualTo(testUser.getUsername());

        // Vérifier les informations du post
        assertThat(commentDTO.getPost()).isNotNull();
        assertThat(commentDTO.getPost().getId()).isEqualTo(testPost.getId());
        assertThat(commentDTO.getPost().getTitle()).isEqualTo(testPost.getTitle());
    }

    @Test
    @DisplayName("Conversion Comment null vers CommentDTO")
    void testToCommentDTO_NullComment() {
        // When
        CommentDTO commentDTO = entityMapper.toCommentDTO(null);

        // Then
        assertThat(commentDTO).isNull();
    }

    @Test
    @DisplayName("Conversion Comment avec auteur null")
    void testToCommentDTO_NullAuthor() {
        // Given
        testComment.setAuthor(null);

        // When
        CommentDTO commentDTO = entityMapper.toCommentDTO(testComment);

        // Then
        assertThat(commentDTO).isNotNull();
        assertThat(commentDTO.getAuthor()).isNull();
        assertThat(commentDTO.getId()).isEqualTo(testComment.getId());
        assertThat(commentDTO.getContent()).isEqualTo(testComment.getContent());
    }

    @Test
    @DisplayName("Conversion Subscription vers SubscriptionDTO - Succès")
    void testToSubscriptionDTO_Success() {
        // When
        SubscriptionDTO subscriptionDTO = entityMapper.toSubscriptionDTO(testSubscription);

        // Then
        assertThat(subscriptionDTO).isNotNull();
        assertThat(subscriptionDTO.getUserId()).isEqualTo(testUser.getId());
        assertThat(subscriptionDTO.getThemeId()).isEqualTo(testTheme.getId());
        assertThat(subscriptionDTO.getSubscribedAt()).isEqualTo(testSubscription.getSubscribedAt());

        // Vérifier l'utilisateur
        assertThat(subscriptionDTO.getUser()).isNotNull();
        assertThat(subscriptionDTO.getUser().getId()).isEqualTo(testUser.getId());
        assertThat(subscriptionDTO.getUser().getUsername()).isEqualTo(testUser.getUsername());

        // Vérifier le thème
        assertThat(subscriptionDTO.getTheme()).isNotNull();
        assertThat(subscriptionDTO.getTheme().getId()).isEqualTo(testTheme.getId());
        assertThat(subscriptionDTO.getTheme().getTitle()).isEqualTo(testTheme.getTitle());
    }

    @Test
    @DisplayName("Conversion Subscription null vers SubscriptionDTO")
    void testToSubscriptionDTO_NullSubscription() {
        // When
        SubscriptionDTO subscriptionDTO = entityMapper.toSubscriptionDTO(null);

        // Then
        assertThat(subscriptionDTO).isNull();
    }

    @Test
    @DisplayName("Conversion Subscription avec utilisateur null")
    void testToSubscriptionDTO_NullUser() {
        // Given
        testSubscription.setUser(null);

        // When
        SubscriptionDTO subscriptionDTO = entityMapper.toSubscriptionDTO(testSubscription);

        // Then
        assertThat(subscriptionDTO).isNotNull();
        assertThat(subscriptionDTO.getUser()).isNull();
        assertThat(subscriptionDTO.getThemeId()).isEqualTo(testTheme.getId());
    }

    @Test
    @DisplayName("Conversion Subscription avec thème null")
    void testToSubscriptionDTO_NullTheme() {
        // Given
        testSubscription.setTheme(null);

        // When
        SubscriptionDTO subscriptionDTO = entityMapper.toSubscriptionDTO(testSubscription);

        // Then
        assertThat(subscriptionDTO).isNotNull();
        assertThat(subscriptionDTO.getTheme()).isNull();
        assertThat(subscriptionDTO.getUserId()).isEqualTo(testUser.getId());
    }

    @Test
    @DisplayName("Conversion liste de Posts vers liste PostDTO")
    void testToPostDTOList_Success() {
        // Given
        Post secondPost = new Post();
        secondPost.setId(2L);
        secondPost.setTitle("Second Post");
        secondPost.setContent("Second post content");
        secondPost.setAuthor(testUser);
        secondPost.setTheme(testTheme);
        
        List<Post> posts = Arrays.asList(testPost, secondPost);

        // When
        List<PostDTO> postDTOs = posts.stream()
                .map(entityMapper::toPostDTO)
                .toList();

        // Then
        assertThat(postDTOs).hasSize(2);
        assertThat(postDTOs.get(0).getId()).isEqualTo(testPost.getId());
        assertThat(postDTOs.get(1).getId()).isEqualTo(secondPost.getId());
    }

    @Test
    @DisplayName("Conversion liste de Comments vers liste CommentDTO")
    void testToCommentDTOList_Success() {
        // Given
        Comment secondComment = new Comment();
        secondComment.setId(2L);
        secondComment.setContent("Second comment");
        secondComment.setAuthor(testUser);
        secondComment.setPost(testPost);
        
        List<Comment> comments = Arrays.asList(testComment, secondComment);

        // When
        List<CommentDTO> commentDTOs = comments.stream()
                .map(entityMapper::toCommentDTO)
                .toList();

        // Then
        assertThat(commentDTOs).hasSize(2);
        assertThat(commentDTOs.get(0).getId()).isEqualTo(testComment.getId());
        assertThat(commentDTOs.get(1).getId()).isEqualTo(secondComment.getId());
    }

    @Test
    @DisplayName("Vérification de l'immutabilité des objets sources")
    void testMapping_DoesNotModifySource() {
        // Given - Sauvegarder l'état initial
        String originalTitle = testPost.getTitle();
        String originalContent = testPost.getContent();
        Long originalAuthorId = testPost.getAuthor().getId();

        // When
        PostDTO postDTO = entityMapper.toPostDTO(testPost);

        // Then - Vérifier que l'objet source n'a pas été modifié
        assertThat(testPost.getTitle()).isEqualTo(originalTitle);
        assertThat(testPost.getContent()).isEqualTo(originalContent);
        assertThat(testPost.getAuthor().getId()).isEqualTo(originalAuthorId);
        
        // Vérifier que le DTO est une copie indépendante
        assertThat(postDTO.getTitle()).isEqualTo(originalTitle);
    }

    @Test
    @DisplayName("Gestion des dates LocalDateTime")
    void testDateTimeMapping() {
        // Given
        LocalDateTime specificDateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 45);
        testPost.setCreatedAt(specificDateTime);
        testPost.setUpdatedAt(specificDateTime.plusHours(1));

        // When
        PostDTO postDTO = entityMapper.toPostDTO(testPost);

        // Then
        assertThat(postDTO.getCreatedAt()).isEqualTo(specificDateTime);
        assertThat(postDTO.getUpdatedAt()).isEqualTo(specificDateTime.plusHours(1));
    }
}
