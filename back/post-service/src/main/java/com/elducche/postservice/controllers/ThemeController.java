package com.elducche.postservice.controllers;

import com.elducche.postservice.models.Theme;
import com.elducche.postservice.services.ThemeService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/themes")
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @GetMapping
    public Flux<Theme> getAllThemes() {
        return themeService.getAllThemes();
    }

    @PostMapping
    public Mono<Theme> createTheme(@RequestBody Theme theme) {
        return themeService.createTheme(theme);
    }
}
