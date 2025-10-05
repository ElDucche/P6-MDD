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
@DisplayName("Tests du PostRepository")
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Devrait trouver les posts par thème")
    void shouldFindPostsByTheme() {
        Theme theme = TestDataBuilder.createTheme("Java", "Programmation Java");
        theme = entityManager.persistAndFlush(theme);
        User author = TestDataBuilder.createUser("author@java.com", "authorjava", "password");
        author = entityManager.persistAndFlush(author);
        Post post = TestDataBuilder.createValidPost();
        post.setAuthor(author);
        post.setTheme(theme);
        entityManager.persistAndFlush(post);
        List<Post> posts = postRepository.findByTheme(theme);
        assertFalse(posts.isEmpty());
        assertEquals(theme.getId(), posts.get(0).getTheme().getId());
    }

    @Test
    @DisplayName("Devrait sauvegarder un post")
    void shouldSavePost() {
        Theme theme = entityManager.persistAndFlush(TestDataBuilder.createTheme("Spring", "Framework Spring"));
        User author = entityManager.persistAndFlush(TestDataBuilder.createUser("author@spring.com", "authorspring", "password"));
        Post post = TestDataBuilder.createValidPost();
        post.setAuthor(author);
        post.setTheme(theme);
        Post saved = postRepository.save(post);
        assertNotNull(saved.getId());
        assertEquals("Titre test", saved.getTitle());
    }

    @Test
    @DisplayName("Devrait supprimer un post")
    void shouldDeletePost() {
        Theme theme = entityManager.persistAndFlush(TestDataBuilder.createTheme("Delete", "Suppression"));
        User author = entityManager.persistAndFlush(TestDataBuilder.createUser("delete@post.com", "deletepost", "password"));
        Post post = TestDataBuilder.createValidPost();
        post.setAuthor(author);
        post.setTheme(theme);
        post = entityManager.persistAndFlush(post);
        postRepository.delete(post);
        assertTrue(postRepository.findById(post.getId()).isEmpty());
    }
}
