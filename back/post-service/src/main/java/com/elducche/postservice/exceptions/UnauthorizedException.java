package com.elducche.postservice.exceptions;

/**
 * Exception lancée quand un utilisateur n'est pas autorisé à effectuer une action
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
