# Étape 1 : Build avec Maven

# Étape 1 : Build avec Maven 3.9+ et JDK 21
FROM eclipse-temurin:21-jdk-alpine AS build
ENV MAVEN_VERSION=3.9.6
ENV MAVEN_HOME=/usr/share/maven
RUN apk add --no-cache curl tar bash && \
    curl -fsSL https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz | tar -xz -C /usr/share && \
    mv /usr/share/apache-maven-${MAVEN_VERSION} $MAVEN_HOME && \
    ln -s $MAVEN_HOME/bin/mvn /usr/bin/mvn
WORKDIR /app
COPY . .
RUN mvn --version && mvn clean package -DskipTests

# Étape 2 : Image d'exécution

# Étape 2 : Image d'exécution
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8761
CMD ["java", "-jar", "app.jar"]
