# ─── Stage 1: Build ───────────────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy dependency manifests first for layer caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build (skip tests during image build)
COPY src ./src
RUN mvn clean package -DskipTests -B

# ─── Stage 2: Runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Create a non-root user for security
RUN addgroup --system spring && adduser --system --ingroup spring spring

# Copy the fat JAR from the build stage
COPY --from=builder /app/target/*.jar app.jar

# Create uploads directory with proper permissions BEFORE switching user
RUN mkdir -p /app/uploads && chown -R spring:spring /app/uploads

# Persistent volume for user-uploaded files
VOLUME /app/uploads

# Expose the application port
EXPOSE 8080

# Switch to non-root user
USER spring:spring

ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-jar", "app.jar"]
