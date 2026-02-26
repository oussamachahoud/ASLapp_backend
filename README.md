<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.2.3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java" />
  <img src="https://img.shields.io/badge/PostgreSQL-16-316192?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL" />
  <img src="https://img.shields.io/badge/Redis-7-DC382D?style=for-the-badge&logo=redis&logoColor=white" alt="Redis" />
  <img src="https://img.shields.io/badge/JWT-Auth-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white" alt="JWT" />
  <img src="https://img.shields.io/badge/Swagger-OpenAPI-85EA2D?style=for-the-badge&logo=swagger&logoColor=black" alt="Swagger" />
</p>

# 🛒 ASLapp Backend — E-Commerce REST API

> A production-grade, secure e-commerce backend built with **Spring Boot 3**, featuring JWT cookie-based authentication, role-based access control, Redis caching, multi-provider file storage, and comprehensive order management — tailored for the Algerian market.

---

## ✨ Highlights

- 🔐 **Stateless JWT auth** via HttpOnly cookies with refresh-token rotation
- 👥 **Role-based access** — User · Seller · Admin
- 📦 **Full product catalogue** — CRUD, search, pagination, image upload
- 🛒 **Shopping cart** with real-time price recalculation
- 📋 **Order lifecycle** — placement, status tracking, stock validation
- 📧 **Email verification** on signup (Brevo SMTP)
- ⚡ **Redis caching** for high-traffic reads
- 📖 **Interactive Swagger UI** at `/swagger-ui`

---

## 📑 Documentation

| Document | Description |
|----------|-------------|
| [Architecture & Tech Stack](docs/ARCHITECTURE.md) | System design, layered architecture, tech stack, folder structure |
| [Database Design](docs/DATABASE.md) | ER diagram, table definitions, enumerations |
| [Security](docs/SECURITY.md) | Authentication flow, JWT cookies, RBAC, security features |
| [API Reference](docs/API.md) | All REST endpoints with request/response examples |
| [Setup & Deployment](docs/SETUP.md) | Local setup, Docker Compose, environment variables |
| [Frontend Integration](docs/FRONTEND_INTEGRATION.md) | Axios/Fetch config, token refresh, file uploads |
| [Roadmap](docs/ROADMAP.md) | Planned features and future improvements |

---

## 🚀 Quick Start

```bash
# 1. Clone
git clone https://github.com/your-org/ASLapp_backend.git
cd ASLapp_backend

# 2. Start dependencies
docker run -d --name postgres -e POSTGRES_DB=ASLapp -e POSTGRES_PASSWORD=admin -p 5432:5432 postgres:16-alpine
docker run -d --name redis -p 6379:6379 redis:7-alpine

# 3. Run the app
mvnw.cmd clean install -DskipTests
mvnw.cmd spring-boot:run
```

- **API:** http://localhost:8081
- **Swagger UI:** http://localhost:8081/swagger-ui
- **OpenAPI JSON:** http://localhost:8081/v3/api-docs

> 📘 See [Setup & Deployment](docs/SETUP.md) for the full guide including Docker Compose.

---

## 🗂 Project Structure (overview)

```
src/main/java/com/example/aslapp_backend/
├── Config/           # Security, JWT filter, Redis, CORS, OpenAPI
├── controller/       # REST endpoints (Auth, User, Product, Cart, Order, Category)
├── DTOs/             # Request & response data transfer objects
├── models/           # JPA entities & enums
├── repositories/     # Spring Data JPA interfaces
├── sevices/          # Business logic, storage, email, JWT
└── Exeption/         # Custom exception classes
```

> 📘 See [Architecture](docs/ARCHITECTURE.md) for the full annotated tree.

---

## 📄 License

This project is proprietary. All rights reserved.

---

<p align="center">
  Built with ❤️ by the <strong>ASL App Team</strong>
</p>