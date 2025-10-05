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
 * Tests unitaires pour l'entité Comment
 */
@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.sql.init.mode=never"
})
@DisplayName("Tests de l'entité Comment")
class CommentEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    private Comment validComment;
    private User author;
    private Post post;
    private Theme theme;

    @BeforeEach
    void setUp() {
        // Préparation des entités liées
        author = TestDataBuilder.createUser("author@test.com", "author", "password");
        theme = TestDataBuilder.createTheme("Java", "Programmation Java");
        
        // Sauvegarde des entités parentes
        author = entityManager.persistAndFlush(author);
        theme = entityManager.persistAndFlush(theme);
        
        // Création du post parent
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
        
        // When/Then - exception lors de la sauvegarde
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(validComment);
        });
    }

    @Test
    @DisplayName("Devrait échouer sans auteur")
    void shouldFailWithoutAuthor() {
        // Given - un commentaire sans auteur
        validComment.setAuthor(null);
        
        // When/Then - exception de contrainte de base de données
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(validComment);
        });
    }

    @Test
    @DisplayName("Devrait échouer sans post")
    void shouldFailWithoutPost() {
        // Given - un commentaire sans post
        validComment.setPost(null);
        
        // When/Then - exception de contrainte de base de données
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(validComment);
        });
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
