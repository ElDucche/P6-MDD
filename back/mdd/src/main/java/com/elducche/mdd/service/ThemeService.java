package com.elducche.mdd.service;

import com.elducche.mdd.entity.Theme;
import com.elducche.mdd.repository.ThemeRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

/**
 * Service de gestion des thèmes
 * 
 * Fournit les opérations CRUD sur les thèmes avec gestion
 * des relations et recherches optimisées
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ThemeService {
    // Méthodes CRUD minimales pour les tests unitaires
    public Optional<Theme> findById(Long id) {
        return themeRepository.findById(id);
    }

    public Theme save(Theme theme) {
        return themeRepository.save(theme);
    }

    public void deleteById(Long id) {
        themeRepository.deleteById(id);
    }

    
    private final ThemeRepository themeRepository;
    
    /**
     * Récupère tous les thèmes
     */
    public List<Theme> getAllThemes() {
        return themeRepository.findAll();
    }
    
    /**
     * Récupère un thème par ID
     */
    public Optional<Theme> getThemeById(Long id) {
        return themeRepository.findById(id);
    }
    
    /**
     * Récupère un thème par titre
     */
    public Optional<Theme> getThemeByTitle(String title) {
        return themeRepository.findByTitle(title);
    }
    
    /**
     * Recherche des thèmes par titre (recherche partielle, insensible à la casse)
     */
    public List<Theme> searchThemesByTitle(String title) {
        return themeRepository.findByTitleContainingIgnoreCase(title);
    }
    
    /**
     * Récupère les thèmes auxquels un utilisateur est abonné
     */
    public List<Theme> getSubscribedThemes(Long userId) {
        return themeRepository.findSubscribedThemes(userId);
    }
    
    /**
     * Récupère les thèmes auxquels un utilisateur n'est PAS abonné
     */
    public List<Theme> getAvailableThemes(Long userId) {
        return themeRepository.findNonSubscribedThemes(userId);
    }
    
    /**
     * Compte le nombre de posts dans un thème
     */
    public long countPostsInTheme(Long themeId) {
        return themeRepository.countPostsByThemeId(themeId);
    }
    
    /**
     * Compte le nombre d'abonnés d'un thème
     */
    public long countSubscribersInTheme(Long themeId) {
        return themeRepository.countSubscribersByThemeId(themeId);
    }
    
    /**
     * Crée un nouveau thème
     */
    public Optional<Theme> createTheme(String title, String description) {
        try {
            // Vérification de l'unicité du titre
            if (themeRepository.existsByTitle(title)) {
                log.warn("Tentative de création d'un thème avec un titre déjà existant: {}", title);
                return Optional.empty();
            }
            
            Theme theme = new Theme();
            theme.setTitle(title);
            theme.setDescription(description);
            
            Theme savedTheme = themeRepository.save(theme);
            log.info("Nouveau thème créé: {} (ID: {})", title, savedTheme.getId());
            
            return Optional.of(savedTheme);
            
        } catch (Exception e) {
            log.error("Erreur lors de la création du thème '{}': {}", title, e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Met à jour un thème
     */
    public Optional<Theme> updateTheme(Long id, String title, String description) {
        try {
            Optional<Theme> themeOpt = themeRepository.findById(id);
            
            if (themeOpt.isEmpty()) {
                log.warn("Tentative de mise à jour d'un thème inexistant: {}", id);
                return Optional.empty();
            }
            
            Theme theme = themeOpt.get();
            
            // Vérification de l'unicité du titre si changé
            if (title != null && !theme.getTitle().equals(title)) {
                if (themeRepository.existsByTitle(title)) {
                    log.warn("Tentative de mise à jour du thème {} avec un titre déjà existant: {}", id, title);
                    return Optional.empty();
                }
                theme.setTitle(title);
            }
            
            if (description != null) {
                theme.setDescription(description);
            }
            
            Theme savedTheme = themeRepository.save(theme);
            log.info("Thème {} mis à jour", id);
            
            return Optional.of(savedTheme);
            
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour du thème {}: {}", id, e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Supprime un thème (uniquement si aucun post n'y est associé)
     */
    public boolean deleteTheme(Long id) {
        try {
            Optional<Theme> themeOpt = themeRepository.findById(id);
            
            if (themeOpt.isEmpty()) {
                log.warn("Tentative de suppression d'un thème inexistant: {}", id);
                return false;
            }
            
            // Vérification qu'aucun post n'est associé au thème
            long postCount = countPostsInTheme(id);
            if (postCount > 0) {
                log.warn("Tentative de suppression du thème {} qui contient {} post(s)", id, postCount);
                return false;
            }
            
            themeRepository.deleteById(id);
            log.info("Thème {} supprimé", id);
            
            return true;
            
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du thème {}: {}", id, e.getMessage());
            return false;
        }
    }
    
    /**
     * Vérifie si un thème existe
     */
    public boolean themeExists(Long id) {
        return themeRepository.existsById(id);
    }
    
    /**
     * Vérifie si un titre de thème existe déjà
     */
    public boolean themeTitleExists(String title) {
        return themeRepository.existsByTitle(title);
    }
}
