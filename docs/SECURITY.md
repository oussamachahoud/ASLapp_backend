[← Back to README](../README.md)

# 🔒 Security

## Authentication Flow

```
 ┌────────┐  POST /api/auth/login   ┌────────────┐
 │ Client │ ──────────────────────▶  │   Server   │
 │        │  { email, password }     │            │
 │        │ ◀──────────────────────  │            │
 │        │  Set-Cookie:             │            │
 │        │    access_token (2h)     │            │
 │        │    refresh_token (7d)    │            │
 └────────┘                          └────────────┘
     │
     │  Subsequent requests carry cookies automatically
     ▼
 ┌──────────────────────────────────────────────────┐
 │  JwtAuthenticationFilter                         │
 │  1. Extract JWT from "access_token" cookie       │
 │  2. Validate signature & expiry                  │
 │  3. Load UserDetails from DB                     │
 │  4. Set SecurityContext                          │
 └──────────────────────────────────────────────────┘
```

---

## Token Lifecycle

```
  Signup                Login              API Request           Token Expired
    │                     │                     │                      │
    ▼                     ▼                     ▼                      ▼
 Email sent ──▶   access_token (2h)     Cookie sent auto     POST /api/auth/refresh
 with verify       refresh_token (7d)    by browser             │
 link               │                     │                      ▼
    │                ▼                     ▼                  New access_token
    ▼             Set-Cookie           Filter validates       via Set-Cookie
 Click link       (HttpOnly)           & sets context             │
    │                                                             ▼
    ▼                                                        Retry original
 Account                                                     request
 enabled
```

---

## Key Security Features

| Feature | Implementation |
|---------|---------------|
| **Stateless Sessions** | `SessionCreationPolicy.STATELESS` — no server-side HTTP session |
| **HttpOnly Cookies** | Access & refresh tokens stored in `HttpOnly`, `Secure`, `SameSite=Lax` cookies |
| **Refresh Token Rotation** | Each refresh generates a new JTI; old JTI is invalidated in Redis |
| **Token Blacklisting** | Logout blacklists the refresh token JTI in Redis |
| **Multi-Session Logout** | `/api/auth/logoutall` revokes all active sessions for a user |
| **RBAC** | `@PreAuthorize("hasRole('ADMIN')")` / `hasAnyRole('ADMIN','SELLER')` on protected endpoints |
| **CSRF Disabled** | Safe because authentication uses HttpOnly cookies with `SameSite` protection |
| **Password Hashing** | BCrypt via Spring Security's `PasswordEncoder` |
| **Email Verification** | Account is disabled until the user clicks the verification link sent via email |
| **Input Validation** | Jakarta Bean Validation (`@NotBlank`, `@Email`, `@Size`, `@PositiveOrZero`) on all request DTOs |

---

## Roles & Permissions

### Role Hierarchy

| Role | Inherits | Description |
|------|----------|-------------|
| `ROLE_USER` | — | Browse products, manage cart, place orders |
| `ROLE_SELLER` | `ROLE_USER` | Create & manage products, update stock |
| `ROLE_ADMIN` | `ROLE_SELLER` | Full system access, user management, order status updates |

### Endpoint Access Matrix

| Endpoint | Public | User | Seller | Admin |
|----------|--------|------|--------|-------|
| `POST /api/auth/signup` | ✅ | — | — | — |
| `POST /api/auth/login` | ✅ | — | — | — |
| `GET /api/auth/verify` | ✅ | — | — | — |
| `GET /api/products` | — | ✅ | ✅ | ✅ |
| `GET /api/products/search` | — | ✅ | ✅ | ✅ |
| `POST /api/products/add-produit` | — | ❌ | ✅ | ✅ |
| `PUT /api/products/{id}` | — | ❌ | ✅ | ✅ |
| `PATCH /api/products/{id}/stock` | — | ❌ | ✅ | ✅ |
| `GET /api/cart` | — | ✅ | ✅ | ✅ |
| `POST /api/cart/add` | — | ✅ | ✅ | ✅ |
| `POST /api/orders/place` | — | ✅ | ✅ | ✅ |
| `GET /api/orders` | — | ✅ | ✅ | ✅ |
| `PUT /api/orders/admin/{id}/status` | — | ❌ | ❌ | ✅ |
| `POST /api/category` | — | ❌ | ❌ | ✅ |
| `DELETE /api/category/{id}` | — | ❌ | ❌ | ✅ |
| `GET /api/users/alluser` | — | ❌ | ❌ | ✅ |
| `DELETE /api/users/Delete/{id}` | — | ❌ | ❌ | ✅ |
| `PATCH /api/users/setrole/{id}` | — | ❌ | ❌ | ✅ |
| `GET /api/users/me` | — | ✅ | ✅ | ✅ |
| `PATCH /api/users/me` | — | ✅ | ✅ | ✅ |
| `DELETE /api/users/me` | — | ✅ | ✅ | ✅ |

---

## Cookie Configuration

| Property | `access_token` | `refresh_token` |
|----------|---------------|-----------------|
| HttpOnly | `true` | `true` |
| Secure | `true` | `true` |
| SameSite | `Lax` | `Lax` |
| Path | `/` | `/api/auth/refresh` |
| Max-Age | 2 hours | 7 days |

---

## Error Handling

All security-related errors are handled by the `GlobalExceptionHandler`:

| Exception | HTTP Status | Response |
|-----------|-------------|----------|
| `AuthenticationException` | `401` | `{ "status": 401, "message": "Unauthorized" }` |
| `AccessDeniedException` | `403` | `{ "status": 403, "message": "Forbidden" }` |
| `BusinessException` | varies | `{ "status": <code>, "message": "<detail>" }` |
| `MethodArgumentNotValidException` | `400` | `{ "status": 400, "message": "Validation failed", "errors": { ... } }` |

---

## Docker Security Best Practices

| Practice | Implementation |
|----------|---------------|
| **Non-root user** | Dockerfile runs as `spring:spring` user, not `root` |
| **Minimal base image** | Uses `eclipse-temurin:17-jre-jammy` (small, regularly updated) |
| **Health checks** | PostgreSQL and Redis include liveness probes |
| **Secrets in .env** | Sensitive values in `.env` file (never hardcoded in Dockerfile) |
| **Volume isolation** | Uploads and database data in named Docker volumes |
| **Network isolation** | Docker Compose creates isolated bridge network by default |
| **Resource limits** | Set CPU/memory limits in production docker-compose.yml |
| **Read-only root** | Can enable `read_only: true` for read-only filesystem in production |

---

## Production Security Checklist

- [ ] Use `.env.production` with strong passwords and keys
- [ ] Store `.env` file outside VCS (add to `.gitignore`)
- [ ] Set file permissions: `chmod 600 .env`
- [ ] Rotate JWT secret key regularly
- [ ] Use HTTPS with reverse proxy (Nginx, Traefik)
- [ ] Configure CORS to specific frontend domain only
- [ ] Enable PostgreSQL password authentication
- [ ] Configure Redis with `requirepass` authentication
- [ ] Use managed services (AWS RDS, Azure Database) in production
- [ ] Enable database encryption at rest
- [ ] Audit log all admin actions
- [ ] Implement rate limiting per user/IP
- [ ] Use secrets management vault (HashiCorp Vault, AWS Secrets Manager)
- [ ] Regularly scan images for vulnerabilities (`docker scan`)
- [ ] Keep base images updated regularly
