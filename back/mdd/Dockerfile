# Utiliser une image Java 21 pour construire l'application
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copier les fichiers Maven
COPY pom.xml .
COPY src ./src

# Installer Maven et construire l'application
RUN apk add --no-cache maven && mvn clean package -DskipTests

# Image finale
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copier le JAR depuis l'étape de construction
COPY --from=builder /app/target/mdd-*.jar app.jar

# Exposer le port
EXPOSE 8080

# Commande pour démarrer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
