package com.elducche.postservice.exceptions;

/**
 * Exception lancée quand un post n'est pas trouvé
 */
public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(String message) {
        super(message);
    }
    
    public PostNotFoundException(Long id) {
        super("Post not found with id: " + id);
    }
}
