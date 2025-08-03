package com.elducche.postservice.controllers;

import com.elducche.postservice.models.Theme;
import com.elducche.postservice.service.UserContextService;
import com.elducche.postservice.services.ThemeService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/themes")
public class ThemeController {

    private final ThemeService themeService;
    private final UserContextService userContextService;

    public ThemeController(ThemeService themeService, UserContextService userContextService) {
        this.themeService = themeService;
        this.userContextService = userContextService;
    }

    @GetMapping
    public List<Theme> getAllThemes(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        // Exemple d'utilisation : log de l'utilisateur qui accède aux thèmes
        if (authHeader != null) {
            UserContextService.UserInfo userInfo = userContextService.getUserInfo(authHeader);
            if (userInfo != null) {
                System.out.println("[THEMES] Utilisateur " + userInfo.getUsername() + " (ID: " + userInfo.getUserId() + ") accède aux thèmes");
            }
        }
        return themeService.getAllThemes();
    }

    @PostMapping
    public Theme createTheme(@RequestBody Theme theme, 
                           @RequestHeader(value = "Authorization", required = false) String authHeader) {
        // Exemple : vérifier l'authentification pour créer un thème
        if (authHeader == null || !userContextService.isAuthenticated(authHeader)) {
            throw new RuntimeException("Authentification requise pour créer un thème");
        }
        
        UserContextService.UserInfo userInfo = userContextService.getUserInfo(authHeader);
        System.out.println("[THEMES] Utilisateur " + userInfo.getUsername() + " (ID: " + userInfo.getUserId() + ") crée un thème: " + theme.getTitle());
        
        return themeService.createTheme(theme);
    }
}
