package com.elducche.postservice.exceptions;

/**
 * Exception lancée lors de la validation des données d'un post
 */
public class PostValidationException extends RuntimeException {
    public PostValidationException(String message) {
        super(message);
    }
}
