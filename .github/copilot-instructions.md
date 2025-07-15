## Accès privé via Codespaces et GitHub Token

Si l'URL de l'API Gateway est en mode "private" dans Codespaces, il est obligatoire d'ajouter le token GitHub à chaque requête sortante.

- Le header `X-Github-Token` doit être ajouté automatiquement par l'API Gateway sur toutes les requêtes sortantes.
- Le token doit être fourni via la variable d'environnement `GITHUB_TOKEN` (voir https://docs.github.com/en/codespaces/developing-in-a-codespace/forwarding-ports-in-your-codespace#forwarding-ports-privately).
- Sans ce header, toute requête sur un port privé retournera une erreur 401.
# Instructions pour Copilot

Ce fichier contient les instructions et informations relatives au projet P6-MDD.

## Informations générales
- Nous sommes sur github codespaces. J'utilise une VM pour le développement.
- Il n'est pas question de localhost dans ce projet, toutes les applications sont accessibles sur des adresses liée à ma VM.
- **IMPORTANT :** Pour éviter les erreurs 401 sur le port 8080 lors de l'accès à l'API Gateway ou à tout service exposé, il faut configurer le port comme "public" dans Codespaces. Voir la documentation officielle : https://docs.github.com/en/codespaces/developing-in-a-codespace/forwarding-ports-in-your-codespace

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
- **Infrastructure**: Base de données PostgreSQL.

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

## Instructions spéciales pour le front/angular-app
- Utiliser Tailwind CSS pour le style.
- Lorsque tu génère un composant, n'ajoute pas de code dans la partie component.html, je l'ajouterai moi-même.

## Commandes utiles

- **Lancer un profil docker-compose**:
  - Le fichier `docker-compose.yml` principal se trouve à la racine du projet.
  - Pour lancer un profil spécifique, utilisez la commande :
    ```bash
    docker-compose --profile <nom_du_profil> up -d
    ```
  - Réfère toi toujours au fichier `docker-compose.yml` pour les profils disponibles.

## Workflow Mode Agent
Lorsque vous travaillez sur une tâche, suivez ces étapes :
- Lister les fichiers à modifier dans la tâche.
- Décomposer le demande en sous-tâches.
- Proposer à l'utilisateur de valider les sous-tâches avant de les implémenter.