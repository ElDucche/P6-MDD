package com.elducche.mdd.repository;

import com.elducche.mdd.entity.*;
import com.elducche.mdd.util.TestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(properties = {"spring.sql.init.mode=never"})
@DisplayName("Tests du CommentRepository")
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Devrait trouver les commentaires par post")
    void shouldFindCommentsByPost() {
        User author = entityManager.persistAndFlush(TestDataBuilder.createUser("author@comment.com", "authorcomment", "password"));
        Theme theme = entityManager.persistAndFlush(TestDataBuilder.createTheme("Test", "Test"));
        Post post = TestDataBuilder.createValidPost();
        post.setAuthor(author);
        post.setTheme(theme);
        post = entityManager.persistAndFlush(post);
        Comment comment = TestDataBuilder.createValidComment();
        comment.setAuthor(author);
        comment.setPost(post);
        entityManager.persistAndFlush(comment);
        List<Comment> comments = commentRepository.findByPost(post);
        assertFalse(comments.isEmpty());
        assertEquals(post.getId(), comments.get(0).getPost().getId());
    }

    @Test
    @DisplayName("Devrait sauvegarder un commentaire")
    void shouldSaveComment() {
        User author = entityManager.persistAndFlush(TestDataBuilder.createUser("author@save.com", "authorsave", "password"));
        Theme theme = entityManager.persistAndFlush(TestDataBuilder.createTheme("Save", "Save"));
        Post post = TestDataBuilder.createValidPost();
        post.setAuthor(author);
        post.setTheme(theme);
        post = entityManager.persistAndFlush(post);
        Comment comment = TestDataBuilder.createValidComment();
        comment.setAuthor(author);
        comment.setPost(post);
        Comment saved = commentRepository.save(comment);
        assertNotNull(saved.getId());
        assertEquals("Commentaire de test", saved.getContent());
    }

    @Test
    @DisplayName("Devrait supprimer un commentaire")
    void shouldDeleteComment() {
        User author = entityManager.persistAndFlush(TestDataBuilder.createUser("author@del.com", "authordel", "password"));
        Theme theme = entityManager.persistAndFlush(TestDataBuilder.createTheme("Del", "Del"));
        Post post = TestDataBuilder.createValidPost();
        post.setAuthor(author);
        post.setTheme(theme);
        post = entityManager.persistAndFlush(post);
        Comment comment = TestDataBuilder.createValidComment();
        comment.setAuthor(author);
        comment.setPost(post);
        comment = entityManager.persistAndFlush(comment);
        commentRepository.delete(comment);
        assertTrue(commentRepository.findById(comment.getId()).isEmpty());
    }
}
