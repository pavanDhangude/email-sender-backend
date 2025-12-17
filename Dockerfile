# Use Java 21 (Render supports this)
FROM eclipse-temurin:21-jdk-jammy

# App directory
WORKDIR /app

# Copy jar into container
COPY target/EmailSender-0.0.1-SNAPSHOT.jar app.jar

# Expose port (Spring Boot default / Render uses this)
EXPOSE 8080

# Run app
ENTRYPOINT ["java", "-jar", "app.jar"]
