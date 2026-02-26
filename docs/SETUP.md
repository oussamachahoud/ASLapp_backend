[← Back to README](../README.md)

# 🚀 Setup & Deployment

## Prerequisites

| Tool | Minimum Version | Purpose |
|------|----------------|---------|
| Java | 17+ | Runtime |
| Maven | 3.9+ | Build tool (wrapper included) |
| PostgreSQL | 14+ | Primary database |
| Redis | 7+ | Caching & token management |
| Docker | 24+ | _(optional)_ Containerised deployment |

---

## Environment Variables

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `SERVER_PORT` | Application port | `8081` | No |
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/ASLapp` | Yes |
| `SPRING_DATASOURCE_USERNAME` | DB username | `postgres` | Yes |
| `SPRING_DATASOURCE_PASSWORD` | DB password | `admin` | Yes |
| `SECURITY_JWT_SECRET_KEY` | JWT signing secret (**use a strong 256-bit key in production**) | — | Yes |
| `SECURITY_JWT_EXPIRATION_TIME` | Access token TTL (ms) | `2678400000` | No |
| `SPRING_MAIL_HOST` | SMTP server hostname | `smtp-relay.brevo.com` | Yes |
| `SPRING_MAIL_PORT` | SMTP port | `587` | Yes |
| `SPRING_MAIL_USERNAME` | SMTP username | — | Yes |
| `SPRING_MAIL_PASSWORD` | SMTP password | — | Yes |
| `SPRING_DATA_REDIS_HOST` | Redis hostname | `localhost` | Yes |
| `SPRING_DATA_REDIS_PORT` | Redis port | `32768` | Yes |
| `SPRING_DATA_REDIS_PASSWORD` | Redis password | _(empty)_ | No |
| `APP_STORAGE_LOCAL_UPLOAD_DIR` | Local file upload directory | `uploads` | No |
| `AZURE_STORAGE_ACCOUNT_NAME` | Azure Blob account name | — | If using Azure |
| `AZURE_STORAGE_ACCOUNT_ENDPOINT` | Azure Blob endpoint | — | If using Azure |
| `SPRING_CLOUD_AWS_S3_ENABLED` | Enable AWS S3 storage | `false` | No |

---

## Run Locally (step-by-step)

### 1. Clone the repository

```bash
git clone https://github.com/your-org/ASLapp_backend.git
cd ASLapp_backend
```

### 2. Create the PostgreSQL database

```sql
CREATE DATABASE "ASLapp";
```

### 3. Configure environment

Edit `src/main/resources/application.properties` or override with environment variables:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ASLapp
spring.datasource.username=postgres
spring.datasource.password=your_password
security.jwt.secret-key=your-256-bit-secret-key
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

### 4. Start Redis

```bash
# Using Docker
docker run -d --name redis -p 6379:6379 redis:7-alpine

# Or using local installation
redis-server
```

### 5. Build and run

```bash
# Linux / macOS
./mvnw clean install -DskipTests
./mvnw spring-boot:run

# Windows
mvnw.cmd clean install -DskipTests
mvnw.cmd spring-boot:run
```

### 6. Verify

| URL | Description |
|-----|-------------|
| http://localhost:8081/api/auth/signup | Signup endpoint |
| http://localhost:8081/swagger-ui | Interactive API docs |
| http://localhost:8081/v3/api-docs | OpenAPI JSON spec |

---

## Docker Setup

### Dockerfile

Create a `Dockerfile` in the project root:

```dockerfile
# ---- Build Stage ----
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B
COPY src src
RUN ./mvnw package -DskipTests -B

# ---- Runtime Stage ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=build /app/target/ASLapp_backend-0.0.1-SNAPSHOT.jar app.jar
RUN mkdir -p /app/uploads && chown -R appuser:appgroup /app
USER appuser
EXPOSE 8081
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-jar", "app.jar"]
```

### docker-compose.yml

```yaml
version: "3.9"

services:
  postgres:
    image: postgres:16-alpine
    container_name: aslapp-db
    environment:
      POSTGRES_DB: ASLapp
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: admin
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s
      retries: 5

  redis:
    image: redis:7-alpine
    container_name: aslapp-redis
    ports:
      - "6379:6379"
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5

  app:
    build: .
    container_name: aslapp-backend
    ports:
      - "8081:8081"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/ASLapp
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: admin
      SPRING_DATA_REDIS_HOST: redis
      SPRING_DATA_REDIS_PORT: 6379
      SECURITY_JWT_SECRET_KEY: change-me-to-a-strong-256-bit-secret
      SPRING_JPA_HIBERNATE_DDL_AUTO: update
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_healthy
    volumes:
      - uploads:/app/uploads

volumes:
  pgdata:
  uploads:
```

### Run with Docker Compose

```bash
# Start all services
docker compose up -d --build

# Check status
docker compose ps

# View application logs
docker compose logs -f app

# Stop all services
docker compose down
```

### Verify

```bash
curl http://localhost:8081/v3/api-docs | head -c 200
```
