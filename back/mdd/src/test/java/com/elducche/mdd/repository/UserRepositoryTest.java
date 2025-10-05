package com.elducche.mdd.repository;

import com.elducche.mdd.entity.User;
import com.elducche.mdd.util.TestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(properties = {"spring.sql.init.mode=never"})
@DisplayName("Tests du UserRepository")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Devrait trouver un utilisateur par email")
    void shouldFindUserByEmail() {
        User user = TestDataBuilder.createUser("findme@example.com", "findme", "password");
        entityManager.persistAndFlush(user);
        Optional<User> found = userRepository.findByEmail("findme@example.com");
        assertTrue(found.isPresent());
        assertEquals("findme", found.get().getUsername());
    }

    @Test
    @DisplayName("Devrait retourner vide si email inconnu")
    void shouldReturnEmptyIfEmailNotFound() {
        Optional<User> found = userRepository.findByEmail("unknown@example.com");
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Devrait sauvegarder un utilisateur")
    void shouldSaveUser() {
        User user = TestDataBuilder.createUser("save@example.com", "saveuser", "password");
        User saved = userRepository.save(user);
        assertNotNull(saved.getId());
        assertEquals("saveuser", saved.getUsername());
    }

    @Test
    @DisplayName("Devrait supprimer un utilisateur")
    void shouldDeleteUser() {
        User user = TestDataBuilder.createUser("delete@example.com", "deleteuser", "password");
        user = entityManager.persistAndFlush(user);
        userRepository.delete(user);
        assertTrue(userRepository.findById(user.getId()).isEmpty());
    }
}
