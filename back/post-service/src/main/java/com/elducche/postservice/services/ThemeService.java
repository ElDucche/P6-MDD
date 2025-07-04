package com.elducche.postservice.services;

import com.elducche.postservice.models.Theme;
import com.elducche.postservice.repositories.ThemeRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ThemeService {

    private final ThemeRepository themeRepository;

    public ThemeService(ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

    public Flux<Theme> getAllThemes() {
        return themeRepository.findAll();
    }

    public Mono<Theme> createTheme(Theme theme) {
        return themeRepository.save(theme);
    }
}
