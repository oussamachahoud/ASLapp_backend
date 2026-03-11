[← Back to README](../README.md)

# 🔌 Frontend Integration Guide

## Overview

ASLapp uses **cookie-based JWT authentication**. The frontend does **not** need to store or manage tokens manually — the browser handles cookie transport automatically. This guide covers everything a frontend developer needs to integrate with the API.

---

## Environment Configuration

### API Base URL

**Development (Docker):**
```javascript
const API_BASE_URL = "http://localhost:8081";
```

**Development (Local):**
```javascript
const API_BASE_URL = "http://localhost:8081";
```

**Production:**
```javascript
const API_BASE_URL = "https://api.yourdomain.com";
```

---

## Authentication Setup

### Axios (Recommended)

```javascript
import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8081",
  withCredentials: true, // ← Required — sends cookies on every request
  headers: {
    "Content-Type": "application/json",
  },
});

export default api;
```

### Fetch API

```javascript
const response = await fetch("http://localhost:8081/api/auth/login", {
  method: "POST",
  credentials: "include", // ← Required — sends cookies on every request
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify({
    email: "john@example.com",
    password: "Str0ng!Pass",
  }),
});
```

> ⚠️ **`withCredentials: true`** (Axios) or **`credentials: "include"`** (Fetch) is mandatory. Without it, the browser will not attach the `access_token` and `refresh_token` cookies, and every authenticated request will fail with `401`.

---

## Login / Signup Flow

```javascript
// ── Signup ──
await api.post("/api/auth/signup", {
  username: "john_doe",
  password: "Str0ng!Pass",
  email: "john@example.com",
  age: 25,
  reason: "I want to buy ASL products",
});
// → 201  { message: "Signup successful. Please check your email ..." }
// → User must click the verification link in their inbox before logging in.

// ── Login ──
await api.post("/api/auth/login", {
  email: "john@example.com",
  password: "Str0ng!Pass",
});
// → 201  (empty body)
// → Cookies set automatically: access_token (2h), refresh_token (7d)

// ── Fetch profile ──
const { data: profile } = await api.get("/api/users/me");
// → { id, username, email, age, imageURL, addresses, role }
```

---

## Handling Token Expiry (Auto-Refresh)

When the `access_token` expires (after 2 hours), the server responds with `401`. Use an Axios response interceptor to silently refresh and retry:

```javascript
let isRefreshing = false;
let pendingQueue = [];

const processQueue = (error) => {
  pendingQueue.forEach(({ resolve, reject }) =>
    error ? reject(error) : resolve()
  );
  pendingQueue = [];
};

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // Only attempt refresh on 401 and not already retrying
    if (error.response?.status === 401 && !originalRequest._retry) {
      if (isRefreshing) {
        // Queue parallel requests while refresh is in-flight
        return new Promise((resolve, reject) => {
          pendingQueue.push({ resolve, reject });
        }).then(() => api(originalRequest));
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        await api.post("/api/auth/refresh");
        processQueue(null);
        return api(originalRequest); // Retry with new cookie
      } catch (refreshError) {
        processQueue(refreshError);
        // Redirect to login page
        window.location.href = "/login";
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  }
);
```

---

## Logout

```javascript
// ── Single session ──
await api.post("/api/auth/logout");
// → Clears cookies, blacklists refresh token

// ── All sessions (all devices) ──
await api.post("/api/auth/logoutall");
// → Revokes every refresh token for this user
```

After logout, redirect the user to the login page.

---

## File Uploads

### Product Image (multipart/form-data)

```javascript
const createProduct = async (productData, imageFile) => {
  const formData = new FormData();

  // Product JSON — must be sent as a string part named "produit"
  formData.append("produit", JSON.stringify(productData));

  // Optional image file
  if (imageFile) {
    formData.append("file", imageFile);
  }

  const { data } = await api.post("/api/products/add-produit", formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });

  return data; // → ProductResponseDTO
};

// Usage
await createProduct(
  {
    name: "ASL T-Shirt",
    price: 29.99,
    description: "High quality cotton T-Shirt",
    category: { id: 1, name: "Clothing" },
    stock: 100,
  },
  fileInput.files[0]
);
```

### Profile / Product Image Update

```javascript
const updateImage = async (entityType, id, file) => {
  const formData = new FormData();
  formData.append("file", file);

  // entityType: "users" or "products"
  const { data } = await api.post(
    `/api/${entityType}/${id}/update-image`,
    formData,
    { headers: { "Content-Type": "multipart/form-data" } }
  );

  return data;
};
```

---

## Pagination Pattern

All list endpoints return Spring's `Page` wrapper. Use query params to navigate:

```javascript
const getProducts = async (page = 0, size = 10, sortBy = "id", direction = "asc") => {
  const { data } = await api.get("/api/products", {
    params: { page, size, sortBy, direction },
  });
  return data;
};
```

**Response structure:**
```json
{
  "content": [ /* array of items */ ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": { "sorted": true, "direction": "ASC" }
  },
  "totalElements": 42,
  "totalPages": 5,
  "size": 10,
  "number": 0,
  "first": true,
  "last": false,
  "empty": false
}
```

**React example:**
```jsx
const [products, setProducts] = useState([]);
const [page, setPage] = useState(0);
const [totalPages, setTotalPages] = useState(0);

useEffect(() => {
  api.get("/api/products", { params: { page, size: 12 } })
    .then(({ data }) => {
      setProducts(data.content);
      setTotalPages(data.totalPages);
    });
}, [page]);
```

---

## Error Handling

All errors follow a consistent JSON shape. Create a global handler:

```javascript
api.interceptors.response.use(
  (res) => res,
  (error) => {
    const data = error.response?.data;

    if (data?.errors) {
      // Validation error (400) — field-level messages
      // data.errors = { email: "must not be blank", ... }
      return Promise.reject({ type: "VALIDATION", fields: data.errors });
    }

    if (data?.message) {
      // Business / auth error
      return Promise.reject({ type: "BUSINESS", message: data.message, status: data.status });
    }

    return Promise.reject({ type: "UNKNOWN", message: "Something went wrong" });
  }
);
```

---

## CORS Notes

**Development:** The backend currently allows all origins (`*`).

**Production:** The backend must be updated to whitelist your frontend domain:

```java
// SecurityConfiguration.java
configuration.setAllowedOrigins(List.of("https://yourdomain.com"));
configuration.setAllowCredentials(true);
```

> `allowCredentials(true)` is required for cookies to work cross-origin. When it's `true`, `allowedOrigins` **cannot** be `*` — it must list explicit origins.

---

## Docker Deployment

### Frontend Container Setup

The frontend can be containerized and deployed alongside the backend using Docker Compose.

**Dockerfile for Angular app:**
```dockerfile
# Build stage
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# Serve with Nginx
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

### docker-compose.yml Configuration

The frontend service is automatically orchestrated with backend:

```yaml
frontend:
  build:
    context: ../ASLappfrontend
    dockerfile: Dockerfile
  container_name: aslapp_frontend
  restart: unless-stopped
  ports:
    - "4200:80"
  environment:
    - NODE_ENV=production
  depends_on:
    - backend
```

**Access in Docker:**
- Frontend: http://localhost:4200
- Backend API: http://localhost:8081
- Swagger UI: http://localhost:8081/swagger-ui

> 📌 When running in Docker, both frontend and backend can reference each other by service name:
> - Backend → Frontend: `http://frontend:4200`
> - Frontend → Backend: `http://backend:8081`

---

## Quick Reference

| Action | Method | Endpoint | Auth |
|--------|--------|----------|------|
| Register | `POST` | `/api/auth/signup` | 🌐 |
| Login | `POST` | `/api/auth/login` | 🌐 |
| Refresh token | `POST` | `/api/auth/refresh` | 🔐 |
| Logout | `POST` | `/api/auth/logout` | 🔐 |
| My profile | `GET` | `/api/users/me` | 🔐 |
| Update profile | `PATCH` | `/api/users/me` | 🔐 |
| Add address | `POST` | `/api/users/me/address` | 🔐 |
| List products | `GET` | `/api/products?page=0&size=10` | 🔐 |
| Search products | `GET` | `/api/products/search?q=shirt` | 🔐 |
| View cart | `GET` | `/api/cart` | 🔐 |
| Add to cart | `POST` | `/api/cart/add?idProduct=1&quantity=2` | 🔐 |
| Remove from cart | `DELETE` | `/api/cart/remove/{itemId}` | 🔐 |
| Place order | `POST` | `/api/orders/place` | 🔐 |
| My orders | `GET` | `/api/orders` | 🔐 |

> 📘 Full endpoint details → [API Reference](API.md)
