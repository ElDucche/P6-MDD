package com.elducche.mdd.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolationException;

/**
 * Gestionnaire global des exceptions pour l'API REST
 * 
 * Cette classe intercepte les exceptions levées par les contrôleurs
 * et retourne des réponses HTTP appropriées avec des messages d'erreur clairs.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gestion des erreurs de validation des données d'entrée (@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("Erreur de validation des données d'entrée: {}", ex.getMessage());
        
        StringBuilder errorMessage = new StringBuilder("Données invalides: ");
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errorMessage.append(error.getField())
                    .append(" - ")
                    .append(error.getDefaultMessage())
                    .append("; ")
        );
        
        return ResponseEntity.badRequest().body(errorMessage.toString());
    }

    /**
     * Gestion des erreurs de contraintes de validation
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("Erreur de contrainte de validation: {}", ex.getMessage());
        return ResponseEntity.badRequest().body("Données invalides: " + ex.getMessage());
    }

    /**
     * Gestion des erreurs de format JSON (JSON malformé)
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.warn("Erreur de format JSON: {}", ex.getMessage());
        return ResponseEntity.badRequest().body("Format JSON invalide");
    }

    /**
     * Gestion des erreurs de Content-Type non supporté
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<String> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        log.warn("Content-Type non supporté: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body("Content-Type non supporté. Utilisez application/json");
    }

    /**
     * Gestion des erreurs de type d'argument incorrect
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.warn("Erreur de type d'argument: {}", ex.getMessage());
        Class<?> requiredTypeClass = ex.getRequiredType();
        String requiredType = requiredTypeClass != null ? requiredTypeClass.getSimpleName() : "inconnu";
        return ResponseEntity.badRequest()
                .body("Paramètre invalide: " + ex.getName() + " doit être de type " + requiredType);
    }

    /**
     * Gestion des autres exceptions non prévues
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        log.error("Erreur inattendue: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur interne du serveur");
    }
}
