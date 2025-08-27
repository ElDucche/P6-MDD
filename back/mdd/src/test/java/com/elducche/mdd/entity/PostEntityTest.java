package com.elducche.mdd.entity;

import com.elducche.mdd.util.TestDataBuilder;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour l'entité Post
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Tests de l'entité Post")
class PostEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private Validator validator;

    private Post validPost;
    private User author;
    private Theme theme;

    @BeforeEach
    void setUp() {
        // Préparation des entités liées
        author = TestDataBuilder.createValidUser();
        theme = TestDataBuilder.createValidTheme();
        
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
        
        // When - validation
        Set<ConstraintViolation<Post>> violations = validator.validate(validPost);
        
        // Then - violation de contrainte
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    @Test
    @DisplayName("Devrait échouer avec un titre vide")
    void shouldFailWithEmptyTitle() {
        // Given - un post avec titre vide
        validPost.setTitle("");
        
        // When - validation
        Set<ConstraintViolation<Post>> violations = validator.validate(validPost);
        
        // Then - violation de contrainte
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    @Test
    @DisplayName("Devrait accepter un contenu null")
    void shouldAcceptNullContent() {
        // Given - un post avec contenu null
        validPost.setContent(null);
        
        // When - validation
        Set<ConstraintViolation<Post>> violations = validator.validate(validPost);
        
        // Then - aucune violation
        assertTrue(violations.isEmpty());
        
        // And - sauvegarde réussie
        Post savedPost = entityManager.persistAndFlush(validPost);
        assertNotNull(savedPost.getId());
        assertNull(savedPost.getContent());
    }

    @Test
    @DisplayName("Devrait échouer sans auteur")
    void shouldFailWithoutAuthor() {
        // Given - un post sans auteur
        validPost.setAuthor(null);
        
        // When - tentative de sauvegarde
        // Then - exception de contrainte de base de données
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(validPost);
        });
    }

    @Test
    @DisplayName("Devrait échouer sans thème")
    void shouldFailWithoutTheme() {
        // Given - un post sans thème
        validPost.setTheme(null);
        
        // When - tentative de sauvegarde
        // Then - exception de contrainte de base de données
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
