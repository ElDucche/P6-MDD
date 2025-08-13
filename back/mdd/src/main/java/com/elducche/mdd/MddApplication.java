package com.elducche.mdd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application principale MDD - Architecture monolithique
 * 
 * Cette application Spring Boot gère :
 * - L'authentification et autorisation (JWT)
 * - La gestion des utilisateurs
 * - La gestion des posts et commentaires
 * - La gestion des thèmes et abonnements
 * - Le système de notifications
 */
@SpringBootApplication
public class MddApplication {

    public static void main(String[] args) {
        SpringApplication.run(MddApplication.class, args);
    }

}
