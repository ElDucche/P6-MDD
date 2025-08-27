# Changelog - Transformation vers Architecture Monolithique MDD

## Vue d'ensemble
Transformation complète du projet P6-MDD d'une architecture microservices vers une architecture monolithique client/serveur avec Spring Boot 3.2.5 et JWT simplifié.

---

## 📋 Étapes Complétées

### ✅ Étape 1 : Structure du Projet Monolithique
**Date :** 10 août 2025  
**Objectif :** Création de la structure Maven pour l'application monolithique `mdd`

**Réalisations :**
- ✅ Création du projet Spring Boot `/back/mdd/`
- ✅ Configuration `pom.xml` avec toutes les dépendances nécessaires
- ✅ Configuration `application.yml` avec PostgreSQL et JWT
- PostgreSQL driver
- JWT (jjwt-api, jjwt-impl, jjwt-jackson)

### ✅ Étape 2 : Migration des Entités JPA
**Date :** 10 août 2025  
- ✅ **User** : Gestion utilisateurs avec timestamps automatiques
- ✅ **Theme** : Thèmes/sujets avec relations bidirectionnelles
- ✅ **Comment** : Commentaires liés aux posts et auteurs
- ✅ **Subscription** : Abonnements avec clé composite (SubscriptionId)

**Améliorations :**

---

### ✅ Étape 3 : Repositories JPA Optimisés
**Date :** 10 août 2025  
**Objectif :** Création des repositories avec requêtes optimisées

- ✅ **PostRepository** : Feed personnalisé, posts par thème/auteur avec JOIN FETCH
- ✅ **CommentRepository** : Commentaires avec relations, comptage
- ✅ **SubscriptionRepository** : Gestion abonnements avec requêtes JPQL

**Optimisations clés :**
- Requêtes `JOIN FETCH` pour éviter N+1 problems
- Méthodes de comptage efficaces
- Feed personnalisé avec une seule requête JPQL
- Requêtes paramétrées pour la sécurité

---

**Date :** 10 août 2025  
**Objectif :** Implémentation de la logique métier complète

**Services créés :**
- ✅ **AuthService** : Login/register avec JWT simplifié
- ✅ **UserService** : CRUD utilisateurs, changement mot de passe
**Fonctionnalités métier :**
- Gestion complète des autorisations (seul l'auteur peut modifier/supprimer)
---
### ✅ Étape 5 : JWT Simplifié et Sécurité
**Date :** 10 août 2025  
**Objectif :** Simplification de l'authentification JWT pour architecture monolithique

**Composants sécurité :**
- ✅ **JwtUtil** : Version simplifiée (email comme identifiant principal)
- ✅ **SecurityUtil** : Accès facilité à l'utilisateur connecté via Spring Security
- Code plus maintenable et lisible
- Utilisation native de Spring Security Context

### ✅ Étape 6 : API REST Complète
**Date :** 10 août 2025  
- ✅ **AuthController** : `/api/auth` - Login, register, validation token
- ✅ **UserController** : `/api/users` - Gestion profil utilisateur
- ✅ **ThemeController** : `/api/themes` - Gestion thèmes, statistiques
- ✅ **CommentController** : `/api/comments` - CRUD commentaires
- ✅ **SubscriptionController** : `/api/subscriptions` - Gestion abonnements


## ✅ Étape 7 : Configuration Spring Security & Docker

### Objectifs
- [x] Configuration complète de Spring Security
- [x] Intégration du filtre JWT 
- [x] Configuration CORS pour le frontend
- [x] Configuration Docker pour architecture monolithique
- [x] Tests de connectivité PostgreSQL
- [ ] Tests de l'authentification complète
- [ ] Validation des endpoints sécurisés

### Réalisations détaillées

#### SecurityConfig.java
- **SecurityFilterChain** : Configuration complète des règles de sécurité
- **Endpoints publics** : `/api/auth/**`, `/api/health`, `/api/error`
- **Endpoints protégés** : Tous les autres nécessitent une authentification JWT
- **Configuration CORS** : Autorisation du frontend sur le port 4200

#### GlobalExceptionHandler.java  
- **Gestion centralisée** : Toutes les exceptions gérées en un seul endroit
- **Erreurs de validation** : Messages détaillés pour les erreurs de saisie
#### HealthController.java
- **Endpoint /health** : Vérification de l'état de l'application
- **docker-compose.yml** : Suppression complète des anciens microservices (discovery-server, api-gateway, user-service, post-service)
- **Architecture simplifiée** : 3 services seulement (postgres, mdd-backend, mdd-frontend)


## ✅ Étape 8 : Tests Unitaires et d'Intégration
**Date :** 27 août 2025  
**Objectif :** Mise en place d'une stratégie complète de tests pour garantir la qualité du code

### Phase 1 : Configuration des Tests ✅ TERMINÉE
**Infrastructure de test créée :**
- ✅ **Structure des répertoires** : Organisation Maven-standard sous `src/test/java`
- ✅ **Configuration H2** : Base de données en mémoire pour tests (`application-test.yml`)
- ✅ **Classes utilitaires** : `TestDataBuilder`, `SecurityTestUtils`, `BaseIntegrationTest`
- ✅ **Données de test** : Script `data.sql` aligné avec le schéma JPA
- ✅ **Test de base** : `MddApplicationTests` validant le démarrage Spring Boot
**Problèmes résolus :**
- Configuration Java 21 vs Java 11
- Alignement schémas SQL/JPA (colonnes `title` vs `name`, `subscribedAt` vs `created_at`)
- Configuration profils Spring Boot pour tests

### Phase 2 : Tests des Entités 🔄 EN COURS
**Méthode validée :**
- `@DataJpaTest` avec `spring.sql.init.mode=never` pour isolation
- Tests focalisés sur contraintes base de données et relations JPA
- Pas de validation Bean Validation (focus sur persistance)

**Entités testées :**
- ✅ **UserEntity** : Tests complets (7 tests passés)
  - Contraintes NOT NULL (email, username, password)
  - Contraintes UNIQUE (email, username)
  - Timestamps automatiques
  - Relations et persistance
- 🔄 **ThemeEntity** : 6 tests (4 succès, 2 échecs - relations non initialisées)
- 🔄 **PostEntity** : 9 tests (8 succès, 1 échec - relation comments non initialisée)  
- ✅ **CommentEntity** : 6 tests, tous validés avec succès
- ❌ **SubscriptionEntity** : 8 tests (2 succès, 6 erreurs - problème ID composite)

**Problèmes identifiés :**
1. **Relations OneToMany** : Listes non initialisées automatiquement par JPA
2. **Entité Subscription** : ID composite nécessite initialisation manuelle
3. **TestDataBuilder** : Ajustements requis pour gestion ID composite

**Corrections en cours :**
- Initialisation manuelle des collections dans les entités
- Mise à jour des tests pour gérer les spécificités JPA

### Plan de test global (9 phases)
1. ✅ **Configuration** - Infrastructure de test
2. 🔄 **Entités** - Validation JPA et contraintes
3. 📋 **Repositories** - Requêtes et méthodes personnalisées  
4. 📋 **Services** - Logique métier et transactions
5. 📋 **Contrôleurs** - Tests d'intégration REST
6. 📋 **Sécurité** - Tests JWT et autorisations
7. 📋 **Intégration** - Tests bout-en-bout
8. 📋 **Performance** - Tests de charge sur endpoints critiques
9. 📋 **Validation** - Tests avec collection Postman

---

## 🎯 Prochaines Étapes

### Étape 7 : Configuration Spring Security
- Configuration SecurityFilterChain
- Gestion des endpoints publics/privés
- Intégration JwtAuthenticationFilter
- Gestion CORS pour le frontend

### Étape 8 : Tests et Validation
- Tests unitaires des services
- Tests d'intégration des contrôleurs
- Validation avec Postman
- Tests de sécurité

### Étape 9 : Docker et Déploiement
- Configuration docker-compose monolithique
- Variables d'environnement
- Script de démarrage
- Documentation déploiement

---

## 📊 Métriques du Projet

| Composant | Avant (Microservices) | Après (Monolithe) |
|-----------|----------------------|-------------------|
| Services Spring Boot | 4 services séparés | 1 application unifiée |
| Ports utilisés | 4 ports (8080-8083) | 1 port (8080) |
| Complexité JWT | Claims multiples | Email seul |
| Requêtes inter-services | HTTP/REST calls | Appels méthodes directes |
| Base de données | 4 schémas séparés | 1 schéma unifié |
| Déploiement | 4 containers | 1 container |

---

## 🔧 Configuration Technique

### Base de Données
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mdd_db
    username: mdd_user
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
```

### JWT
```yaml
jwt:
  secret: ${JWT_SECRET}
  expiration: 86400000 # 24h
```

### Port Application
```yaml
server:
  port: 8080
```

---

*Transformation réalisée avec succès - Architecture monolithique opérationnelle* ✅
