package com.elducche.mdd.service;

import com.elducche.mdd.entity.Theme;

import java.util.List;
import java.util.Optional;

/**
 * Service de gestion des thèmes
 * 
 * Fournit les opérations CRUD sur les thèmes avec gestion
 * des relations et recherches optimisées
 */
public interface ThemeService {
    // Méthodes CRUD minimales pour les tests unitaires
    Optional<Theme> findById(Long id);
    Theme save(Theme theme);
    void deleteById(Long id);
    
    /**
     * Récupère tous les thèmes
     */
    List<Theme> getAllThemes();
    
    /**
     * Récupère un thème par ID
     */
    Optional<Theme> getThemeById(Long id);
    
    /**
     * Récupère un thème par titre
     */
    Optional<Theme> getThemeByTitle(String title);
    
    /**
     * Recherche des thèmes par titre (recherche partielle, insensible à la casse)
     */
    List<Theme> searchThemesByTitle(String title);
    
    /**
     * Récupère les thèmes auxquels un utilisateur est abonné
     */
    List<Theme> getSubscribedThemes(Long userId);
    
    /**
     * Récupère les thèmes auxquels un utilisateur n'est PAS abonné
     */
    List<Theme> getAvailableThemes(Long userId);
    
    /**
     * Compte le nombre de posts dans un thème
     */
    long countPostsInTheme(Long themeId);
    
    /**
     * Compte le nombre d'abonnés d'un thème
     */
    long countSubscribersInTheme(Long themeId);
    
    /**
     * Crée un nouveau thème
     */
    Optional<Theme> createTheme(String title, String description);
    
    /**
     * Met à jour un thème
     */
    Optional<Theme> updateTheme(Long id, String title, String description);
    
    /**
     * Supprime un thème (uniquement si aucun post n'y est associé)
     */
    boolean deleteTheme(Long id);
    
    /**
     * Vérifie si un thème existe
     */
    boolean themeExists(Long id);
    
    /**
     * Vérifie si un titre de thème existe déjà
     */
    boolean themeTitleExists(String title);
}
