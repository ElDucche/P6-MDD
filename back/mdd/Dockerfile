## ================================
## Stage 1 : Build
## ================================
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /workspace

# Installation Maven (léger) + cache dépendances
RUN apk add --no-cache maven

# Copier uniquement le POM pour pré-télécharger les dépendances
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -q -e dependency:go-offline

# Copier le code source
COPY src ./src

# Build (sans tests ici, ils tournent en CI hors image)
RUN --mount=type=cache,target=/root/.m2 mvn -q -e clean package -DskipTests

## ================================
## Stage 2 : Runtime
## ================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Ajout d'un user non-root par sécurité
RUN addgroup -S mdd && adduser -S mdd -G mdd

COPY --from=builder /workspace/target/mdd-*.jar app.jar

EXPOSE 8080
USER mdd

ENTRYPOINT ["java","-jar","app.jar"]
