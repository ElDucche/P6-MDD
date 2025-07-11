FROM eclipse-temurin:21-jdk-alpine
RUN apk add --no-cache maven
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests
EXPOSE 8083
CMD ["java", "-jar", "target/*.jar"]
