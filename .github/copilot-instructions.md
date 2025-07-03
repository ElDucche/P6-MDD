# Instructions pour Copilot

Ce fichier contient les instructions et informations relatives au projet P6-MDD.

## Contexte du projet

Mettre en place une architecture microservices Java/Spring Boot pour un projet de type réseau social. L'application permettra aux utilisateurs de s'inscrire, de se connecter, de créer des posts, de suivre d'autres utilisateurs et de recevoir des notifications.

## Architecture

- **Backend**: Java 21 / Spring Boot 3
    - **API Gateway**: `api-gateway` (Spring Cloud Gateway) pour le routage et la sécurité (JWT).
    - **Service Discovery**: `discovery-server` (Eureka).
    - **Microservices**:
        - `user-service`: Gestion des utilisateurs.
        - `post-service`: Gestion des posts, thèmes et commentaires.
        - `notification-service`: Gestion des notifications.
    - **Base de données**: MySQL, accessible en mode réactif avec R2DBC.
- **Frontend**: Angular (dossier `front/angular-app`, à développer).
- **Infrastructure**: Docker Compose (`infra/docker-compose.yml`) pour lancer la base de données MySQL.

## Instructions de développement

1.  **Terminer la configuration des entités et repositories** pour `post-service` et `notification-service` en utilisant R2DBC et les tables définies dans `infra/script.sql`.
2.  **Implémenter la logique métier** dans chaque microservice (CRUD pour les posts, les thèmes, les abonnements, etc.).
3.  **Sécuriser les endpoints** des microservices en validant le token JWT transmis par l'API Gateway.
4.  **Développer le frontend Angular** pour interagir avec l'API Gateway.
5.  **Respecter les bonnes pratiques de développement** :
  - Nettoyer le code inutiles
  - Verifier l'indentation
  - Commenter le code lorsque les fonctionnalités deviennent complexe 
6.  **Ajouter des tests unitaires et d'intégration**.
7. **S'assurer que le code respecte les principes SOLID en POO** et que le code est documenté avec javadoc.

## Commandes utiles

- **Lancer la base de données**:
  ```bash
  cd infra
  docker-compose up -d
  ```
- **Lancer un microservice** (ex: api-gateway):
  ```bash
  cd back/api-gateway
  mvn spring-boot:run
  ```
  (Répéter pour `discovery-server`, `user-service`, `post-service`, `notification-service`)

## Workflow Mode Agent
Lorsque vous travaillez sur une tâche, suivez ces étapes :
- Lister les fichiers à modifier dans la tâche.
- Décomposer le demande en sous-tâches.
- Proposer à l'utilisateur de valider les sous-tâches avant de les implémenter via une elicitation MCP "Valider".