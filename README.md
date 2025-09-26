# P6-MDD – Démarrage simplifié (dev local)

Ce projet se lance en local avec :
1. La base PostgreSQL via Docker (fichier `infra/docker-compose.yml`).
2. Le backend Spring Boot avec Maven.
3. Le frontend Angular avec npm.

## 1. Pré-requis
| Outil | Version conseillée | Remarques |
|-------|--------------------|-----------|
| Docker & Docker Compose | 24+ | Uniquement pour la base PostgreSQL |
| Java | 21 (Temurin) | Pour exécuter le backend |
| Maven | 3.9+ | Build & run backend |
| Node / npm | Node 20 / npm 10 | Lancer le frontend |

## 2. Lancer la base de données
Depuis la racine du repo :
```bash
cd infra
docker compose up -d postgres
```
Paramètres par défaut (modifiable via le `docker-compose.yml`) :
- Host: localhost
- Port: 5432
- DB: mdd_db
- User: user / password: password

Pour arrêter la base :
```bash
docker compose down
```
Pour repartir propre (supprimer les données) :
```bash
docker compose down -v
```

## 3. Lancer le backend
Dans un nouveau terminal :
```bash
cd back/mdd
mvn spring-boot:run
```
Le backend écoute sur le port `8081` (voir `application.yml`).

Endpoints utiles :
- Swagger UI : http://localhost:8081/swagger-ui/index.html
- Auth login : POST http://localhost:8081/api/auth/login (payload `{ "identifier": "usertest@example.com", "password": "?Password1" }`)

Variables d'environnement (optionnel) :
- `POSTGRES_HOST`, `POSTGRES_PORT`, `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`
- `JWT_SECRET` (≥ 64 caractères pour HS512)

## 4. Lancer le frontend
Dans un autre terminal :
```bash
cd front/angular-app
npm install
npm start
```
Par défaut (dev server) : http://localhost:4200

Si l'API est sur 8081, vérifiez que `environment.ts` contient :
```ts
apiUrl: 'http://localhost:8081'
```

## 5. Tests backend
```bash
cd back/mdd
mvn clean test
```
Rapports :
- Tests : `target/surefire-reports/`
- Couverture : `target/site/jacoco/index.html` (seuil instruction ≥ 70%)

## 6. Tests frontend
```bash
cd front/angular-app
npm test
```
Autres scripts :
```bash
npm run test:watch
npm run test:coverage
```
Couverture : `front/angular-app/coverage/index.html`.

## 7. Arrêt rapide
- Stop backend : Ctrl+C dans le terminal Maven
- Stop frontend : Ctrl+C dans le terminal Angular
- Stop base : `cd infra && docker compose down`

## 8. Dépannage
| Problème | Piste |
|----------|-------|
| 401 sur API | Variable JWT ou port incorrect, vérifier backend up |
| Connexion DB échoue | Vérifier conteneur postgres (`docker ps`) et credentials |
| Front ne trouve pas l'API | Mauvaise valeur `apiUrl` / backend pas démarré |
| Couverture insuffisante | Ajouter tests jusqu'à ≥ 70% instructions |