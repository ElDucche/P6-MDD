package com.elducche.mdd.service;

import com.elducche.mdd.entity.Theme;
import com.elducche.mdd.repository.ThemeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ThemeServiceTest {
    @Mock
    private ThemeRepository themeRepository;

    @InjectMocks
    private ThemeService themeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById() {
        Theme theme = new Theme();
        theme.setId(1L);
        when(themeRepository.findById(1L)).thenReturn(Optional.of(theme));
        Optional<Theme> result = themeService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    // ... autres tests CRUD à compléter
}
