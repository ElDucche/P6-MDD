package com.elducche.mdd.util;

/**
 * Constantes utilisées dans les tests
 * 
 * Cette classe centralise toutes les constantes utilisées
 * dans les tests unitaires et d'intégration.
 */
public final class TestConstants {

    // ===== UTILISATEURS DE TEST =====
    public static final String TEST_USER_EMAIL = "usertest@example.com";
    public static final String TEST_USER_USERNAME = "usertest";
    public static final String TEST_USER_PASSWORD = "?Password1";
    public static final String TEST_USER_ENCODED_PASSWORD = "$2a$10$0YJZt5qFZwqw5r6X8xDa0OmtZhY4qZL5K8xY6K8Y6K8Y6K8Y6K8Y6O";
    
    public static final String JOHN_DOE_EMAIL = "john.doe@example.com";
    public static final String JOHN_DOE_USERNAME = "johndoe";
    
    public static final String JANE_SMITH_EMAIL = "jane.smith@example.com";
    public static final String JANE_SMITH_USERNAME = "janesmith";

    // ===== THÈMES DE TEST =====
    public static final String JAVA_THEME_NAME = "Java";
    public static final String JAVA_THEME_DESCRIPTION = "Discussions sur le langage Java et ses frameworks";
    
    public static final String ANGULAR_THEME_NAME = "Angular";
    public static final String ANGULAR_THEME_DESCRIPTION = "Frontend moderne avec Angular";
    
    public static final String SPRING_BOOT_THEME_NAME = "Spring Boot";
    public static final String SPRING_BOOT_THEME_DESCRIPTION = "Développement backend avec Spring Boot";

    // ===== POSTS DE TEST =====
    public static final String SAMPLE_POST_TITLE = "Introduction à Spring Boot";
    public static final String SAMPLE_POST_CONTENT = "Spring Boot simplifie le développement Java...";
    
    public static final String ANGULAR_POST_TITLE = "Les nouveautés d'Angular 17";
    public static final String ANGULAR_POST_CONTENT = "Angular 17 apporte de nombreuses améliorations...";

    // ===== COMMENTAIRES DE TEST =====
    public static final String SAMPLE_COMMENT_CONTENT = "Excellent article ! Merci pour le partage.";
    public static final String QUESTION_COMMENT_CONTENT = "J'ai une question sur l'injection de dépendances...";

    // ===== DONNÉES INVALIDES POUR LES TESTS =====
    public static final String INVALID_EMAIL = "email-invalide";
    public static final String EMPTY_STRING = "";
    public static final String NULL_STRING = null;
    public static final String TOO_LONG_STRING = "a".repeat(256); // Chaîne trop longue
    
    // ===== MOTS DE PASSE INVALIDES =====
    public static final String TOO_SHORT_PASSWORD = "123";
    public static final String PASSWORD_WITHOUT_SPECIAL_CHAR = "Password1";
    public static final String PASSWORD_WITHOUT_NUMBER = "Password!";
    public static final String PASSWORD_WITHOUT_UPPERCASE = "password1!";

    // ===== JWT ET SÉCURITÉ =====
    public static final String TEST_JWT_SECRET = "TestSecretKeyForJWTTokenGenerationInTestEnvironmentMustBe256BitsLong123456789";
    public static final long TEST_JWT_EXPIRATION = 3600000L; // 1 heure
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    
    // ===== ENDPOINTS API =====
    public static final String API_BASE_PATH = "/api";
    public static final String AUTH_BASE_PATH = API_BASE_PATH + "/auth";
    public static final String USERS_BASE_PATH = API_BASE_PATH + "/users";
    public static final String POSTS_BASE_PATH = API_BASE_PATH + "/posts";
    public static final String COMMENTS_BASE_PATH = API_BASE_PATH + "/comments";
    public static final String THEMES_BASE_PATH = API_BASE_PATH + "/themes";
    public static final String SUBSCRIPTIONS_BASE_PATH = API_BASE_PATH + "/subscriptions";
    
    // Endpoints spécifiques
    public static final String LOGIN_ENDPOINT = AUTH_BASE_PATH + "/login";
    public static final String REGISTER_ENDPOINT = AUTH_BASE_PATH + "/register";
    public static final String VALIDATE_TOKEN_ENDPOINT = AUTH_BASE_PATH + "/validate";
    public static final String PROFILE_ENDPOINT = USERS_BASE_PATH + "/me";

    // ===== MESSAGES D'ERREUR ATTENDUS =====
    public static final String EMAIL_ALREADY_EXISTS_ERROR = "Un compte avec cet email existe déjà";
    public static final String USERNAME_ALREADY_EXISTS_ERROR = "Ce nom d'utilisateur est déjà pris";
    public static final String INVALID_CREDENTIALS_ERROR = "Email ou mot de passe incorrect";
    public static final String TOKEN_MISSING_ERROR = "Token manquant ou invalide";
    public static final String TOKEN_INVALID_ERROR = "Token invalide ou expiré";
    public static final String INTERNAL_SERVER_ERROR = "Erreur interne du serveur";

    // ===== IDS DE TEST =====
    public static final Long TEST_USER_ID = 1L;
    public static final Long JOHN_DOE_ID = 2L;
    public static final Long JANE_SMITH_ID = 3L;
    
    public static final Long JAVA_THEME_ID = 1L;
    public static final Long ANGULAR_THEME_ID = 2L;
    public static final Long SPRING_BOOT_THEME_ID = 3L;
    
    public static final Long SAMPLE_POST_ID = 1L;
    public static final Long ANGULAR_POST_ID = 2L;
    
    public static final Long SAMPLE_COMMENT_ID = 1L;

    // ===== PAGINATION =====
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int DEFAULT_PAGE_NUMBER = 0;

    // ===== CONSTRUCTEUR PRIVÉ =====
    private TestConstants() {
        throw new UnsupportedOperationException("Cette classe ne doit pas être instanciée");
    }
}
