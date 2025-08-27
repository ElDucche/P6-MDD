package com.elducche.mdd.repository;

import com.elducche.mdd.entity.Theme;
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
@DisplayName("Tests du ThemeRepository")
class ThemeRepositoryTest {

    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Devrait trouver un thème par titre")
    void shouldFindThemeByTitle() {
        Theme theme = TestDataBuilder.createTheme("Angular", "Framework frontend");
        entityManager.persistAndFlush(theme);
        Optional<Theme> found = themeRepository.findByTitle("Angular");
        assertTrue(found.isPresent());
        assertEquals("Angular", found.get().getTitle());
    }

    @Test
    @DisplayName("Devrait retourner vide si titre inconnu")
    void shouldReturnEmptyIfTitleNotFound() {
        Optional<Theme> found = themeRepository.findByTitle("Inconnu");
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Devrait sauvegarder un thème")
    void shouldSaveTheme() {
        Theme theme = TestDataBuilder.createTheme("React", "Librairie JS");
        Theme saved = themeRepository.save(theme);
        assertNotNull(saved.getId());
        assertEquals("React", saved.getTitle());
    }

    @Test
    @DisplayName("Devrait supprimer un thème")
    void shouldDeleteTheme() {
        Theme theme = TestDataBuilder.createTheme("Delete", "Suppression");
        theme = entityManager.persistAndFlush(theme);
        themeRepository.delete(theme);
        assertTrue(themeRepository.findById(theme.getId()).isEmpty());
    }
}
