# ---------- Build stage ----------
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom and source
COPY pom.xml .
COPY src ./src

# Build jar
RUN mvn clean package -DskipTests

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port (Render uses 8080)
EXPOSE 8080

# Run app
ENTRYPOINT ["java", "-jar", "app.jar"]
