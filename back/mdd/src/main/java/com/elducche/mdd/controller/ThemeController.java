package com.elducche.mdd.controller;

import com.elducche.mdd.entity.Theme;
import com.elducche.mdd.service.ThemeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Contrôleur pour la gestion des thèmes
 */
@Slf4j
@RestController
@RequestMapping("/api/themes")
@RequiredArgsConstructor
public class ThemeController {

    private final ThemeService themeService;

    /**
     * Récupère tous les thèmes
     * @return Liste de tous les thèmes disponibles
     */
    @GetMapping
    public ResponseEntity<List<Theme>> getAllThemes() {
        log.debug("Récupération de tous les thèmes");
        List<Theme> themes = themeService.getAllThemes();
        return ResponseEntity.ok(themes);
    }

    /**
     * Récupère un thème par son ID
     * @param id ID du thème
     * @return Le thème correspondant ou 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<Theme> getThemeById(@PathVariable Long id) {
        log.debug("Récupération du thème avec l'ID : {}", id);
        
        Optional<Theme> theme = themeService.getThemeById(id);
        return theme.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
}
