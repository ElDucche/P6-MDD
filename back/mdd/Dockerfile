# Utiliser une image Java 21 pour construire l'application
FROM openjdk:21-jdk-slim AS builder

WORKDIR /app

# Copier les fichiers Maven
COPY pom.xml .
COPY src ./src

# Installer Maven et construire l'application
RUN apt-get update && apt-get install -y maven
RUN mvn clean package -DskipTests

# Image finale
FROM openjdk:21-jre-slim

WORKDIR /app

# Copier le JAR depuis l'étape de construction
COPY --from=builder /app/target/mdd-*.jar app.jar

# Exposer le port
EXPOSE 8080

# Commande pour démarrer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
