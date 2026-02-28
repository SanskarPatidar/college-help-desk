FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY .mvn ./.mvn
COPY mvnw pom.xml ./
COPY src ./src
COPY install-ollama-models.sh ./
# Skips tests to make the build faster
RUN ./mvnw clean package -DskipTests

# 2. Run Stage: Uses a lightweight Java runtime to actually run the app
FROM gcr.io/distroless/java21-debian12 AS runner
WORKDIR /app
# Copies only the finished JAR file from the "builder" stage
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]