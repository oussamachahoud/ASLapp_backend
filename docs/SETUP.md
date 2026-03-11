# 🚀 Setup & Deployment

## Prerequisites

### For Docker Setup (Recommended)

| Tool | Minimum Version | Purpose |
|------|----------------|---------|
| Docker | 24+ | Container runtime |
| Docker Compose | 2.0+ | Multi-container orchestration |

### For Local Development

| Tool | Minimum Version | Purpose |
|------|----------------|---------|
| Java | 17+ | Runtime |
| Maven | 3.9+ | Build tool (wrapper included) |
| PostgreSQL | 14+ | Primary database |
| Redis | 7+ | Caching & token management |

---

## Docker Setup (Recommended) 🐳

### 1. Clone the repository

```bash
git clone https://github.com/your-org/ASLapp_backend.git
cd ASLapp_backend
```

### 2. Create `.env` file

Copy the environment template and configure:

```bash
cp .env.example .env
```

**`.env` content:**
```properties
# Database
POSTGRES_DB=aslapp
DB_USERNAME=postgres
DB_PASSWORD=your_secure_password

# Redis
REDIS_PASSWORD=

# JWT Secret (use a strong 256-bit key in production)
SECURITY_JWT_SECRET_KEY=your-256-bit-secret-key-here

# SMTP (Brevo)
SPRING_MAIL_HOST=smtp-relay.brevo.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your-brevo-email@example.com
SPRING_MAIL_PASSWORD=your-brevo-api-key

# Storage
APP_STORAGE_LOCAL_UPLOAD_DIR=uploads

# Optional: Azure Storage
# AZURE_STORAGE_ACCOUNT_NAME=your-account
# AZURE_STORAGE_ACCOUNT_ENDPOINT=https://...

# Optional: AWS S3
# AWS_S3_BUCKET_NAME=your-bucket
# AWS_S3_REGION=us-east-1
```

### 3. Start services with Docker Compose

```bash
# Build and start all services (postgres, redis, backend, frontend)
docker compose up -d --build

# Check service status
docker compose ps

# View application logs
docker compose logs -f backend

# Stop all services
docker compose down
```

### 4. Verify everything is running

```bash
# Test API
curl http://localhost:8081/v3/api-docs

# Open in browser
# Backend: http://localhost:8081/swagger-ui
# Frontend: http://localhost:4200
```

### Docker Compose Services

| Service | Image | Port | Purpose |
|---------|-------|------|---------|
| `postgres` | postgres:16-alpine | 5432 | Primary database |
| `redis` | redis:7-alpine | 6379 | Cache & token store |
| `backend` | Local build | 8081 | Spring Boot API |
| `frontend` | Local build | 4200 | Angular app (optional) |

> 📌 **Data Persistence:** All volumes (`postgres_data`, `redis_data`, `uploads_data`) are persistent across container restarts.

---

## Local Development (without Docker)

### 1. Prerequisites

Ensure PostgreSQL 14+, Redis 7+, and Java 17+ are installed locally.

### 2. Clone the repository

```bash
git clone https://github.com/your-org/ASLapp_backend.git
cd ASLapp_backend
```

### 3. Create the PostgreSQL database

```sql
CREATE DATABASE "aslapp";
```

### 4. Configure environment

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/aslapp
spring.datasource.username=postgres
spring.datasource.password=your_password
security.jwt.secret-key=your-256-bit-secret-key
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

### 5. Start PostgreSQL & Redis

**Using Docker containers (minimal footprint):**
```bash
docker run -d --name postgres -e POSTGRES_DB=aslapp -e POSTGRES_PASSWORD=admin -p 5432:5432 postgres:16-alpine
docker run -d --name redis -p 6379:6379 redis:7-alpine
```

**Or locally installed:**
```bash
# On Windows
pg_ctl -D "C:\Program Files\PostgreSQL\data" start
redis-server

# On macOS
brew services start postgresql
brew services start redis

# On Linux
sudo systemctl start postgresql
sudo systemctl start redis-server
```

### 6. Build and run the application

```bash
# Build
mvnw.cmd clean install -DskipTests

# Run
mvnw.cmd spring-boot:run
```

### 7. Verify

| URL | Description |
|-----|-------------|
| http://localhost:8081/api/auth/signup | Signup endpoint |
| http://localhost:8081/swagger-ui | Interactive API docs |
| http://localhost:8081/v3/api-docs | OpenAPI JSON spec |

---

## Environment Variables Reference

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `SERVER_PORT` | Application port | `8081` | No |
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL | — | Yes |
| `SPRING_DATASOURCE_USERNAME` | DB username | — | Yes |
| `SPRING_DATASOURCE_PASSWORD` | DB password | — | Yes |
| `SECURITY_JWT_SECRET_KEY` | JWT signing secret (**256-bit in production**) | — | Yes |
| `SECURITY_JWT_EXPIRATION_TIME` | Access token TTL (ms) | `7200000` (2 hours) | No |
| `SPRING_MAIL_HOST` | SMTP server hostname | `smtp-relay.brevo.com` | Yes |
| `SPRING_MAIL_PORT` | SMTP port | `587` | Yes |
| `SPRING_MAIL_USERNAME` | SMTP username | — | Yes |
| `SPRING_MAIL_PASSWORD` | SMTP password | — | Yes |
| `SPRING_DATA_REDIS_HOST` | Redis hostname | `localhost` | Yes |
| `SPRING_DATA_REDIS_PORT` | Redis port | `6379` | Yes |
| `SPRING_DATA_REDIS_PASSWORD` | Redis password | _(empty)_ | No |
| `APP_STORAGE_LOCAL_UPLOAD_DIR` | Local file upload directory | `uploads` | No |
| `AZURE_STORAGE_ACCOUNT_NAME` | Azure Blob account name | — | If using Azure |
| `AZURE_STORAGE_ACCOUNT_ENDPOINT` | Azure Blob endpoint | — | If using Azure |
| `AWS_S3_BUCKET_NAME` | AWS S3 bucket name | — | If using AWS |
| `AWS_S3_REGION` | AWS S3 region | — | If using AWS |

---

## Production Deployment

### Docker Production Checklist

- [ ] Use strong JWT secret key (256-bit minimum)
- [ ] Set `SPRING_JPA_HIBERNATE_DDL_AUTO=validate` (never `update` or `create-drop`)
- [ ] Configure production CORS origins in `SecurityConfiguration.java`
- [ ] Use `.env` file with restricted file permissions (`chmod 600 .env`)
- [ ] Store secrets in a vault (HashiCorp Vault, AWS Secrets Manager, etc.)
- [ ] Enable HTTPS with a reverse proxy (Nginx, Traefik)
- [ ] Set PostgreSQL password to a strong value
- [ ] Configure Redis authentication
- [ ] Use managed PostgreSQL (AWS RDS, Azure Database for PostgreSQL)
- [ ] Use managed Redis (AWS ElastiCache, Azure Cache for Redis)
- [ ] Set resource limits in `docker-compose.yml` (memory, CPU)
- [ ] Configure log aggregation (ELK, Splunk, CloudWatch)

### Example Production docker-compose.yml

```yaml
version: "3.9"
services:
  backend:
    build: .
    container_name: aslapp_backend
    restart: always
    env_file: .env.production
    ports:
      - "8081:8081"
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_healthy
    deploy:
      resources:
        limits:
          cpus: "2"
          memory: 2G
        reservations:
          cpus: "1"
          memory: 1G
```

---

## Troubleshooting

### Docker service fails to start

```bash
# Check logs
docker compose logs backend

# Verify database health
docker compose logs postgres

# Verify Redis health
docker compose logs redis

# Rebuild from scratch
docker compose down -v  # Remove volumes
docker compose up -d --build
```

### Port already in use

```bash
# Find and kill process on port 5432 (PostgreSQL)
lsof -i :5432
kill -9 <PID>

# Find and kill process on port 6379 (Redis)
lsof -i :6379
kill -9 <PID>

# Or change ports in docker-compose.yml
```

### Database connection failed

```bash
# Verify database readiness
docker compose ps postgres

# Check database logs
docker compose logs postgres

# Test connection from backend container
docker compose exec backend pg_isready -h postgres -U postgres
```

### Redis connection failed

```bash
# Verify Redis readiness
docker compose ps redis

# Test Redis connection
docker compose exec redis redis-cli ping
```
