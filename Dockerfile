FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Skips tests to make the build faster
RUN mvn clean package -DskipTests

# 2. Run Stage: Uses a lightweight Java runtime to actually run the app
FROM eclipse-temurin:21-jre-alpine AS runner
WORKDIR /app
# Copies only the finished JAR file from the "builder" stage
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]