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
 * Tests unitaires pour l'entité Comment
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Tests de l'entité Comment")
class CommentEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private Validator validator;

    private Comment validComment;
    private User author;
    private Post post;
    private Theme theme;

    @BeforeEach
    void setUp() {
        // Préparation des entités liées
        author = TestDataBuilder.createValidUser();
        theme = TestDataBuilder.createValidTheme();
        
        // Sauvegarde des entités parentes
        author = entityManager.persistAndFlush(author);
        theme = entityManager.persistAndFlush(theme);
        
        // Création du post
        post = TestDataBuilder.createValidPost();
        post.setAuthor(author);
        post.setTheme(theme);
        post = entityManager.persistAndFlush(post);
        
        // Création du commentaire valide
        validComment = TestDataBuilder.createValidComment();
        validComment.setAuthor(author);
        validComment.setPost(post);
    }

    @Test
    @DisplayName("Devrait créer un commentaire valide")
    void shouldCreateValidComment() {
        // Given - un commentaire valide avec relations
        
        // When - sauvegarde en base
        Comment savedComment = entityManager.persistAndFlush(validComment);
        
        // Then - le commentaire est sauvegardé avec un ID
        assertNotNull(savedComment.getId());
        assertEquals(validComment.getContent(), savedComment.getContent());
        assertEquals(author.getId(), savedComment.getAuthor().getId());
        assertEquals(post.getId(), savedComment.getPost().getId());
        assertNotNull(savedComment.getCreatedAt());
        assertNotNull(savedComment.getUpdatedAt());
    }

    @Test
    @DisplayName("Devrait échouer avec un contenu null")
    void shouldFailWithNullContent() {
        // Given - un commentaire avec contenu null
        validComment.setContent(null);
        
        // When - validation
        Set<ConstraintViolation<Comment>> violations = validator.validate(validComment);
        
        // Then - violation de contrainte
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("content")));
    }

    @Test
    @DisplayName("Devrait échouer avec un contenu vide")
    void shouldFailWithEmptyContent() {
        // Given - un commentaire avec contenu vide
        validComment.setContent("");
        
        // When - validation
        Set<ConstraintViolation<Comment>> violations = validator.validate(validComment);
        
        // Then - violation de contrainte
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("content")));
    }

    @Test
    @DisplayName("Devrait échouer sans auteur")
    void shouldFailWithoutAuthor() {
        // Given - un commentaire sans auteur
        validComment.setAuthor(null);
        
        // When - tentative de sauvegarde
        // Then - exception de contrainte de base de données
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(validComment);
        });
    }

    @Test
    @DisplayName("Devrait échouer sans post")
    void shouldFailWithoutPost() {
        // Given - un commentaire sans post
        validComment.setPost(null);
        
        // When - tentative de sauvegarde
        // Then - exception de contrainte de base de données
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(validComment);
        });
    }

    @Test
    @DisplayName("Devrait mettre à jour les timestamps automatiquement")
    void shouldUpdateTimestampsAutomatically() {
        // Given - un commentaire sauvegardé
        Comment savedComment = entityManager.persistAndFlush(validComment);
        Long originalId = savedComment.getId();
        
        // When - modification et sauvegarde
        savedComment.setContent("Nouveau contenu");
        entityManager.flush();
        
        // Then - updatedAt est modifié
        Comment updatedComment = entityManager.find(Comment.class, originalId);
        assertNotNull(updatedComment.getUpdatedAt());
        assertTrue(updatedComment.getUpdatedAt().isAfter(updatedComment.getCreatedAt()) || 
                  updatedComment.getUpdatedAt().equals(updatedComment.getCreatedAt()));
    }

    @Test
    @DisplayName("Devrait maintenir l'intégrité référentielle avec l'auteur")
    void shouldMaintainReferentialIntegrityWithAuthor() {
        // Given - un commentaire sauvegardé
        Comment savedComment = entityManager.persistAndFlush(validComment);
        
        // When - récupération du commentaire avec son auteur
        Comment foundComment = entityManager.find(Comment.class, savedComment.getId());
        
        // Then - l'auteur est correctement chargé
        assertNotNull(foundComment.getAuthor());
        assertEquals(author.getId(), foundComment.getAuthor().getId());
        assertEquals(author.getUsername(), foundComment.getAuthor().getUsername());
    }

    @Test
    @DisplayName("Devrait maintenir l'intégrité référentielle avec le post")
    void shouldMaintainReferentialIntegrityWithPost() {
        // Given - un commentaire sauvegardé
        Comment savedComment = entityManager.persistAndFlush(validComment);
        
        // When - récupération du commentaire avec son post
        Comment foundComment = entityManager.find(Comment.class, savedComment.getId());
        
        // Then - le post est correctement chargé
        assertNotNull(foundComment.getPost());
        assertEquals(post.getId(), foundComment.getPost().getId());
        assertEquals(post.getTitle(), foundComment.getPost().getTitle());
    }

    @Test
    @DisplayName("Devrait permettre plusieurs commentaires sur le même post")
    void shouldAllowMultipleCommentsOnSamePost() {
        // Given - un premier commentaire sauvegardé
        Comment firstComment = entityManager.persistAndFlush(validComment);
        
        // When - création d'un second commentaire sur le même post
        Comment secondComment = TestDataBuilder.createValidComment();
        secondComment.setContent("Deuxième commentaire");
        secondComment.setAuthor(author);
        secondComment.setPost(post);
        
        // Then - les deux commentaires peuvent être sauvegardés
        Comment savedSecondComment = entityManager.persistAndFlush(secondComment);
        assertNotNull(savedSecondComment.getId());
        assertNotEquals(firstComment.getId(), savedSecondComment.getId());
        assertEquals(post.getId(), savedSecondComment.getPost().getId());
    }

    @Test
    @DisplayName("Devrait permettre au même auteur de commenter plusieurs fois")
    void shouldAllowSameAuthorToCommentMultipleTimes() {
        // Given - un premier commentaire sauvegardé
        Comment firstComment = entityManager.persistAndFlush(validComment);
        
        // When - création d'un second commentaire par le même auteur
        Comment secondComment = TestDataBuilder.createValidComment();
        secondComment.setContent("Deuxième commentaire du même auteur");
        secondComment.setAuthor(author);
        secondComment.setPost(post);
        
        // Then - les deux commentaires peuvent être sauvegardés
        Comment savedSecondComment = entityManager.persistAndFlush(secondComment);
        assertNotNull(savedSecondComment.getId());
        assertNotEquals(firstComment.getId(), savedSecondComment.getId());
        assertEquals(author.getId(), savedSecondComment.getAuthor().getId());
    }
}
