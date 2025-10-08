package com.elducche.mdd.service;

import com.elducche.mdd.entity.Theme;
import com.elducche.mdd.repository.ThemeRepository;
import com.elducche.mdd.service.impl.ThemeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ThemeServiceTest {
    private static final String TEST_TITLE = "Nature";
    private static final String TEST_DESCRIPTION = "Description du thème Nature";
    
    @Mock
    private ThemeRepository themeRepository;

    @InjectMocks
    private ThemeServiceImpl themeService;

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

    @Test
    void testCreateTheme() {
        Theme theme = new Theme();
        theme.setTitle(TEST_TITLE);
        when(themeRepository.save(any(Theme.class))).thenReturn(theme);
        Theme created = themeService.save(theme);
        assertEquals(TEST_TITLE, created.getTitle());
    }

    @Test
    void testUpdateTheme() {
        Theme theme = new Theme();
        theme.setId(1L);
        theme.setTitle(TEST_TITLE);
        when(themeRepository.save(any(Theme.class))).thenReturn(theme);
        Theme updated = themeService.save(theme);
        assertEquals(1L, updated.getId());
        assertEquals(TEST_TITLE, updated.getTitle());
    }

    @Test
    void testDeleteTheme() {
        doNothing().when(themeRepository).deleteById(1L);
        themeService.deleteById(1L);
        verify(themeRepository, times(1)).deleteById(1L);
    }
    
    @Test
    void testGetAllThemes() {
        Theme theme1 = new Theme();
        theme1.setId(1L);
        theme1.setTitle("Theme1");
        
        Theme theme2 = new Theme();
        theme2.setId(2L);
        theme2.setTitle("Theme2");
        
        when(themeRepository.findAll()).thenReturn(Arrays.asList(theme1, theme2));
        
        List<Theme> themes = themeService.getAllThemes();
        
        assertEquals(2, themes.size());
        verify(themeRepository, times(1)).findAll();
    }
    
    @Test
    void testGetThemeByTitle() {
        Theme theme = new Theme();
        theme.setId(1L);
        theme.setTitle(TEST_TITLE);
        
        when(themeRepository.findByTitle(TEST_TITLE)).thenReturn(Optional.of(theme));
        
        Optional<Theme> result = themeService.getThemeByTitle(TEST_TITLE);
        
        assertTrue(result.isPresent());
        assertEquals(TEST_TITLE, result.get().getTitle());
        verify(themeRepository, times(1)).findByTitle(TEST_TITLE);
    }
    
    @Test
    void testSearchThemesByTitle() {
        Theme theme1 = new Theme();
        theme1.setId(1L);
        theme1.setTitle("Nature");
        
        Theme theme2 = new Theme();
        theme2.setId(2L);
        theme2.setTitle("Natural Science");
        
        when(themeRepository.findByTitleContainingIgnoreCase("nat")).thenReturn(Arrays.asList(theme1, theme2));
        
        List<Theme> results = themeService.searchThemesByTitle("nat");
        
        assertEquals(2, results.size());
        verify(themeRepository, times(1)).findByTitleContainingIgnoreCase("nat");
    }
    
    @Test
    void testGetSubscribedThemes() {
        Long userId = 1L;
        Theme theme1 = new Theme();
        theme1.setId(1L);
        
        when(themeRepository.findSubscribedThemes(userId)).thenReturn(Arrays.asList(theme1));
        
        List<Theme> themes = themeService.getSubscribedThemes(userId);
        
        assertEquals(1, themes.size());
        verify(themeRepository, times(1)).findSubscribedThemes(userId);
    }
    
    @Test
    void testGetAvailableThemes() {
        Long userId = 1L;
        Theme theme1 = new Theme();
        theme1.setId(2L);
        
        when(themeRepository.findNonSubscribedThemes(userId)).thenReturn(Arrays.asList(theme1));
        
        List<Theme> themes = themeService.getAvailableThemes(userId);
        
        assertEquals(1, themes.size());
        verify(themeRepository, times(1)).findNonSubscribedThemes(userId);
    }
    
    @Test
    void testCountPostsInTheme() {
        Long themeId = 1L;
        when(themeRepository.countPostsByThemeId(themeId)).thenReturn(5L);
        
        long count = themeService.countPostsInTheme(themeId);
        
        assertEquals(5L, count);
        verify(themeRepository, times(1)).countPostsByThemeId(themeId);
    }
    
    @Test
    void testCountSubscribersInTheme() {
        Long themeId = 1L;
        when(themeRepository.countSubscribersByThemeId(themeId)).thenReturn(10L);
        
        long count = themeService.countSubscribersInTheme(themeId);
        
        assertEquals(10L, count);
        verify(themeRepository, times(1)).countSubscribersByThemeId(themeId);
    }
    
    @Test
    void testCreateThemeWithValidData() {
        Theme savedTheme = new Theme();
        savedTheme.setId(1L);
        savedTheme.setTitle(TEST_TITLE);
        savedTheme.setDescription(TEST_DESCRIPTION);
        
        when(themeRepository.existsByTitle(TEST_TITLE)).thenReturn(false);
        when(themeRepository.save(any(Theme.class))).thenReturn(savedTheme);
        
        Optional<Theme> result = themeService.createTheme(TEST_TITLE, TEST_DESCRIPTION);
        
        assertTrue(result.isPresent());
        assertEquals(TEST_TITLE, result.get().getTitle());
        verify(themeRepository, times(1)).existsByTitle(TEST_TITLE);
        verify(themeRepository, times(1)).save(any(Theme.class));
    }
    
    @Test
    void testCreateThemeWithDuplicateTitle() {
        when(themeRepository.existsByTitle(TEST_TITLE)).thenReturn(true);
        
        Optional<Theme> result = themeService.createTheme(TEST_TITLE, TEST_DESCRIPTION);
        
        assertFalse(result.isPresent());
        verify(themeRepository, times(1)).existsByTitle(TEST_TITLE);
        verify(themeRepository, never()).save(any(Theme.class));
    }
    
    @Test
    void testUpdateThemeWithNewTitle() {
        Theme existingTheme = new Theme();
        existingTheme.setId(1L);
        existingTheme.setTitle("Old Title");
        existingTheme.setDescription("Old Description");
        
        Theme updatedTheme = new Theme();
        updatedTheme.setId(1L);
        updatedTheme.setTitle(TEST_TITLE);
        updatedTheme.setDescription(TEST_DESCRIPTION);
        
        when(themeRepository.findById(1L)).thenReturn(Optional.of(existingTheme));
        when(themeRepository.existsByTitle(TEST_TITLE)).thenReturn(false);
        when(themeRepository.save(any(Theme.class))).thenReturn(updatedTheme);
        
        Optional<Theme> result = themeService.updateTheme(1L, TEST_TITLE, TEST_DESCRIPTION);
        
        assertTrue(result.isPresent());
        assertEquals(TEST_TITLE, result.get().getTitle());
        verify(themeRepository, times(1)).findById(1L);
        verify(themeRepository, times(1)).existsByTitle(TEST_TITLE);
        verify(themeRepository, times(1)).save(any(Theme.class));
    }
    
    @Test
    void testUpdateThemeWithDuplicateTitle() {
        Theme existingTheme = new Theme();
        existingTheme.setId(1L);
        existingTheme.setTitle("Old Title");
        
        when(themeRepository.findById(1L)).thenReturn(Optional.of(existingTheme));
        when(themeRepository.existsByTitle(TEST_TITLE)).thenReturn(true);
        
        Optional<Theme> result = themeService.updateTheme(1L, TEST_TITLE, TEST_DESCRIPTION);
        
        assertFalse(result.isPresent());
        verify(themeRepository, times(1)).findById(1L);
        verify(themeRepository, times(1)).existsByTitle(TEST_TITLE);
        verify(themeRepository, never()).save(any(Theme.class));
    }
    
    @Test
    void testUpdateNonExistentTheme() {
        when(themeRepository.findById(999L)).thenReturn(Optional.empty());
        
        Optional<Theme> result = themeService.updateTheme(999L, TEST_TITLE, TEST_DESCRIPTION);
        
        assertFalse(result.isPresent());
        verify(themeRepository, times(1)).findById(999L);
        verify(themeRepository, never()).save(any(Theme.class));
    }
    
    @Test
    void testDeleteThemeSuccessfully() {
        Theme theme = new Theme();
        theme.setId(1L);
        
        when(themeRepository.findById(1L)).thenReturn(Optional.of(theme));
        when(themeRepository.countPostsByThemeId(1L)).thenReturn(0L);
        doNothing().when(themeRepository).deleteById(1L);
        
        boolean result = themeService.deleteTheme(1L);
        
        assertTrue(result);
        verify(themeRepository, times(1)).findById(1L);
        verify(themeRepository, times(1)).countPostsByThemeId(1L);
        verify(themeRepository, times(1)).deleteById(1L);
    }
    
    @Test
    void testDeleteThemeWithPosts() {
        Theme theme = new Theme();
        theme.setId(1L);
        
        when(themeRepository.findById(1L)).thenReturn(Optional.of(theme));
        when(themeRepository.countPostsByThemeId(1L)).thenReturn(5L);
        
        boolean result = themeService.deleteTheme(1L);
        
        assertFalse(result);
        verify(themeRepository, times(1)).findById(1L);
        verify(themeRepository, times(1)).countPostsByThemeId(1L);
        verify(themeRepository, never()).deleteById(1L);
    }
    
    @Test
    void testDeleteNonExistentTheme() {
        when(themeRepository.findById(999L)).thenReturn(Optional.empty());
        
        boolean result = themeService.deleteTheme(999L);
        
        assertFalse(result);
        verify(themeRepository, times(1)).findById(999L);
        verify(themeRepository, never()).deleteById(999L);
    }
    
    @Test
    void testThemeExists() {
        when(themeRepository.existsById(1L)).thenReturn(true);
        
        boolean exists = themeService.themeExists(1L);
        
        assertTrue(exists);
        verify(themeRepository, times(1)).existsById(1L);
    }
    
    @Test
    void testThemeTitleExists() {
        when(themeRepository.existsByTitle(TEST_TITLE)).thenReturn(true);
        
        boolean exists = themeService.themeTitleExists(TEST_TITLE);
        
        assertTrue(exists);
        verify(themeRepository, times(1)).existsByTitle(TEST_TITLE);
    }
}
