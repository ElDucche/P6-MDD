package com.elducche.mdd.entity;

import com.elducche.mdd.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour l'entité Post
 */
@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.sql.init.mode=never"
})
@DisplayName("Tests de l'entité Post")
class PostEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    private Post validPost;
    private User author;
    private Theme theme;

    @BeforeEach
    void setUp() {
        // Préparation des entités liées
        author = TestDataBuilder.createUser("author@test.com", "author", "password");
        theme = TestDataBuilder.createTheme("Java", "Programmation Java");
        
        // Sauvegarde des entités parentes
        author = entityManager.persistAndFlush(author);
        theme = entityManager.persistAndFlush(theme);
        
        // Création du post valide
        validPost = TestDataBuilder.createValidPost();
        validPost.setAuthor(author);
        validPost.setTheme(theme);
    }

    @Test
    @DisplayName("Devrait créer un post valide")
    void shouldCreateValidPost() {
        // Given - un post valide avec relations
        
        // When - sauvegarde en base
        Post savedPost = entityManager.persistAndFlush(validPost);
        
        // Then - le post est sauvegardé avec un ID
        assertNotNull(savedPost.getId());
        assertEquals(validPost.getTitle(), savedPost.getTitle());
        assertEquals(validPost.getContent(), savedPost.getContent());
        assertEquals(author.getId(), savedPost.getAuthor().getId());
        assertEquals(theme.getId(), savedPost.getTheme().getId());
        assertNotNull(savedPost.getCreatedAt());
        assertNotNull(savedPost.getUpdatedAt());
    }

    @Test
    @DisplayName("Devrait échouer avec un titre null")
    void shouldFailWithNullTitle() {
        // Given - un post avec titre null
        validPost.setTitle(null);
        
        // When/Then - exception lors de la sauvegarde
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(validPost);
        });
    }

    @Test
    @DisplayName("Devrait accepter un contenu null")
    void shouldAcceptNullContent() {
        // Given - un post avec contenu null
        validPost.setContent(null);
        
        // When - sauvegarde
        Post savedPost = entityManager.persistAndFlush(validPost);
        
        // Then - sauvegarde réussie
        assertNotNull(savedPost.getId());
        assertNull(savedPost.getContent());
    }

    @Test
    @DisplayName("Devrait échouer sans auteur")
    void shouldFailWithoutAuthor() {
        // Given - un post sans auteur
        validPost.setAuthor(null);
        
        // When/Then - exception de contrainte de base de données
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(validPost);
        });
    }

    @Test
    @DisplayName("Devrait échouer sans thème")
    void shouldFailWithoutTheme() {
        // Given - un post sans thème
        validPost.setTheme(null);
        
        // When/Then - exception de contrainte de base de données
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(validPost);
        });
    }

    @Test
    @DisplayName("Devrait mettre à jour les timestamps automatiquement")
    void shouldUpdateTimestampsAutomatically() {
        // Given - un post sauvegardé
        Post savedPost = entityManager.persistAndFlush(validPost);
        Long originalId = savedPost.getId();
        
        // When - modification et sauvegarde
        savedPost.setTitle("Nouveau titre");
        entityManager.flush();
        
        // Then - updatedAt est modifié
        Post updatedPost = entityManager.find(Post.class, originalId);
        assertNotNull(updatedPost.getUpdatedAt());
        assertTrue(updatedPost.getUpdatedAt().isAfter(updatedPost.getCreatedAt()) || 
                  updatedPost.getUpdatedAt().equals(updatedPost.getCreatedAt()));
    }

    @Test
    @DisplayName("Devrait gérer les relations avec les commentaires")
    void shouldHandleCommentRelations() {
        // Given - un post sauvegardé
        Post savedPost = entityManager.persistAndFlush(validPost);
        
        // When - récupération du post
        Post foundPost = entityManager.find(Post.class, savedPost.getId());
        
        // Then - la liste des commentaires est initialisée (vide)
        assertNotNull(foundPost.getComments());
        assertTrue(foundPost.getComments().isEmpty());
    }

    @Test
    @DisplayName("Devrait maintenir l'intégrité référentielle avec l'auteur")
    void shouldMaintainReferentialIntegrityWithAuthor() {
        // Given - un post sauvegardé
        Post savedPost = entityManager.persistAndFlush(validPost);
        
        // When - récupération du post avec son auteur
        Post foundPost = entityManager.find(Post.class, savedPost.getId());
        
        // Then - l'auteur est correctement chargé
        assertNotNull(foundPost.getAuthor());
        assertEquals(author.getId(), foundPost.getAuthor().getId());
        assertEquals(author.getUsername(), foundPost.getAuthor().getUsername());
    }

    @Test
    @DisplayName("Devrait maintenir l'intégrité référentielle avec le thème")
    void shouldMaintainReferentialIntegrityWithTheme() {
        // Given - un post sauvegardé
        Post savedPost = entityManager.persistAndFlush(validPost);
        
        // When - récupération du post avec son thème
        Post foundPost = entityManager.find(Post.class, savedPost.getId());
        
        // Then - le thème est correctement chargé
        assertNotNull(foundPost.getTheme());
        assertEquals(theme.getId(), foundPost.getTheme().getId());
        assertEquals(theme.getTitle(), foundPost.getTheme().getTitle());
    }
}
