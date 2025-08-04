package com.elducche.postservice.service;

import com.elducche.postservice.models.Theme;
import com.elducche.postservice.repositories.ThemeRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ThemeService {

    private final ThemeRepository themeRepository;

    public ThemeService(ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

    public List<Theme> getAllThemes() {
        return themeRepository.findAll();
    }

    public Theme createTheme(Theme theme) {
        return themeRepository.save(theme);
    }
}
