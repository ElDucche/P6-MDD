package com.elducche.postservice.controllers;

import com.elducche.postservice.models.Theme;
import com.elducche.postservice.services.ThemeService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/themes")
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @GetMapping
    public List<Theme> getAllThemes() {
        return themeService.getAllThemes();
    }

    @PostMapping
    public Theme createTheme(@RequestBody Theme theme) {
        return themeService.createTheme(theme);
    }
}
