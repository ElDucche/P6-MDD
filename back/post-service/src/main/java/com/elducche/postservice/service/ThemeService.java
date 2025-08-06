package com.elducche.postservice.service;

import com.elducche.postservice.models.Theme;
import com.elducche.postservice.repositories.ThemeRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ThemeService {

    private final ThemeRepository themeRepository;

    public ThemeService(ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

    /**
     * Récupère tous les thèmes
     */
    public List<Theme> getAllThemes() {
        return themeRepository.findAll();
    }

    /**
     * Récupère un thème par son ID
     */
    public Optional<Theme> getThemeById(Long id) {
        return themeRepository.findById(id);
    }

    /**
     * Crée un nouveau thème
     */
    public Theme createTheme(Theme theme) {
        return themeRepository.save(theme);
    }
}
