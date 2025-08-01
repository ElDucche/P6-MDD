package com.elducche.postservice.exceptions;

/**
 * Exception lancée quand un thème n'est pas trouvé
 */
public class ThemeNotFoundException extends RuntimeException {
    public ThemeNotFoundException(String message) {
        super(message);
    }
    
    public ThemeNotFoundException(Long id) {
        super("Theme not found with id: " + id);
    }
}
