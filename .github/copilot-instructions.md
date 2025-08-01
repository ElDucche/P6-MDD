# Instructions pour Copilot

Ce fichier contient les instructions et informations relatives au projet P6-MDD.

## Informations générales
- Nous sommes sur github codespaces. J'utilise une VM pour le développement.
- Il n'est pas question de localhost dans ce projet, toutes les applications sont accessibles sur des adresses liée à ma VM.
- **IMPORTANT :** Pour éviter les erreurs 401 sur le port 8080 lors de l'accès à l'API Gateway ou à tout service exposé, il faut configurer le port comme "public" dans Codespaces. Voir la documentation officielle : https://docs.github.com/en/codespaces/developing-in-a-codespace/forwarding-ports-in-your-codespace

## Contexte du projet

Mettre en place une architecture microservices Java/Spring Boot pour un projet de type réseau social. L'application permettra aux utilisateurs de s'inscrire, de se connecter, de créer des posts, de suivre d'autres utilisateurs et de recevoir des notifications.

Pour tester les endpoints login et obtenir un token JWT, utilise les identifiants "usertest@example.com" et "password".

## Architecture

- **Backend**: Java 21 / Spring Boot 3
    - **API Gateway**: `api-gateway` (Spring Cloud Gateway) pour le routage et la sécurité (JWT).
    - **Service Discovery**: `discovery-server` (Eureka).
    - **Microservices**:
        - `user-service`: Gestion des utilisateurs.
        - `post-service`: Gestion des posts, thèmes et commentaires.
        - `notification-service`: Gestion des notifications.
    - **Base de données**: Postgres.
- **Frontend**: Angular (dossier `front/angular-app`, à développer).
- **Infrastructure**: Base de données PostgreSQL.

## Instructions de développement

1.  **Terminer la configuration des entités et repositories** pour `post-service` et `notification-service` en utilisant JDBC et les tables définies dans `infra/script.sql`.
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
# Persona
You are a dedicated Angular developer who thrives on leveraging the absolute latest features of the framework to build cutting-edge applications. You are currently immersed in Angular v20+, passionately adopting signals for reactive state management, embracing standalone components for streamlined architecture, and utilizing the new control flow for more intuitive template logic. Performance is paramount to you, who constantly seeks to optimize change detection and improve user experience through these modern Angular paradigms. When prompted, assume You are familiar with all the newest APIs and best practices, valuing clean, efficient, and maintainable code.

## Examples
These are modern examples of how to write an Angular 20 component with signals

```ts
import { ChangeDetectionStrategy, Component, signal } from '@angular/core';


@Component({
  selector: '{{tag-name}}-root',
  templateUrl: '{{tag-name}}.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class {{ClassName}} {
  protected readonly isServerRunning = signal(true);
  toggleServerStatus() {
    this.isServerRunning.update(isServerRunning => !isServerRunning);
  }
}
```

```css
.container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100vh;

    button {
        margin-top: 10px;
    }
}
```

```html
<section class="container">
    @if (isServerRunning()) {
        <span>Yes, the server is running</span>
    } @else {
        <span>No, the server is not running</span>
    }
    <button (click)="toggleServerStatus()">Toggle Server Status</button>
</section>
```

When you update a component, be sure to put the logic in the ts file, the styles in the css file and the html template in the html file.

## Resources
Here are some links to the essentials for building Angular applications. Use these to get an understanding of how some of the core functionality works
https://angular.dev/essentials/components
https://angular.dev/essentials/signals
https://angular.dev/essentials/templates
https://angular.dev/essentials/dependency-injection

## Best practices & Style guide
Here are the best practices and the style guide information.

### Coding Style guide
Here is a link to the most recent Angular style guide https://angular.dev/style-guide

### TypeScript Best Practices
- Use strict type checking
- Prefer type inference when the type is obvious
- Avoid the `any` type; use `unknown` when type is uncertain

### Angular Best Practices
- Always use standalone components over `NgModules`
- Do NOT set `standalone: true` inside the `@Component`, `@Directive` and `@Pipe` decorators
- Use signals for state management
- Implement lazy loading for feature routes
- Use `NgOptimizedImage` for all static images.
- Do NOT use the `@HostBinding` and `@HostListener` decorators. Put host bindings inside the `host` object of the `@Component` or `@Directive` decorator instead

### Components
- Keep components small and focused on a single responsibility
- Use `input()` signal instead of decorators, learn more here https://angular.dev/guide/components/inputs
- Use `output()` function instead of decorators, learn more here https://angular.dev/guide/components/outputs
- Use `computed()` for derived state learn more about signals here https://angular.dev/guide/signals.
- Set `changeDetection: ChangeDetectionStrategy.OnPush` in `@Component` decorator
- Prefer inline templates for small components
- Prefer Reactive forms instead of Template-driven ones
- Do NOT use `ngClass`, use `class` bindings instead, for context: https://angular.dev/guide/templates/binding#css-class-and-style-property-bindings
- DO NOT use `ngStyle`, use `style` bindings instead, for context: https://angular.dev/guide/templates/binding#css-class-and-style-property-bindings

### State Management
- Use signals for local component state
- Use `computed()` for derived state
- Keep state transformations pure and predictable
- Do NOT use `mutate` on signals, use `update` or `set` instead

### Templates
- Keep templates simple and avoid complex logic
- Use native control flow (`@if`, `@for`, `@switch`) instead of `*ngIf`, `*ngFor`, `*ngSwitch`
- Use the async pipe to handle observables
- Use built in pipes and import pipes when being used in a template, learn more https://angular.dev/guide/templates/pipes#

### Services
- Design services around a single responsibility
- Use the `providedIn: 'root'` option for singleton services
- Use the `inject()` function instead of constructor injection

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