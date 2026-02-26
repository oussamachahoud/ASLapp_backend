[← Back to README](../README.md)

# 📡 API Reference

**Base URL:** `http://localhost:8081`
**Swagger UI:** [http://localhost:8081/swagger-ui](http://localhost:8081/swagger-ui)
**OpenAPI JSON:** [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs)

**Authentication:** JWT tokens stored in `HttpOnly` cookies. After login, the browser automatically sends cookies on each request — no `Authorization` header needed.

**Legend:** 🌐 Public · 🔐 Authenticated · 🔑 Role-restricted

---

## Table of Contents

- [1. Authentication](#1-authentication-apiauth)
- [2. Users](#2-users-apiusers)
- [3. Products](#3-products-apiproducts)
- [4. Categories](#4-categories-apicategory)
- [5. Cart](#5-cart-apicart)
- [6. Orders](#6-orders-apiorders)
- [Error Response Format](#error-response-format)

---

## 1. Authentication (`/api/auth`)

### POST `/api/auth/signup` — Register a new user 🌐

**Request:**
```json
{
  "username": "john_doe",
  "password": "Str0ng!Pass",
  "email": "john@example.com",
  "age": 25,
  "reason": "I want to buy ASL products"
}
```

**Response** `201 Created`:
```json
{
  "message": "Signup successful. Please check your email to verify your account."
}
```

**cURL:**
```bash
curl -X POST http://localhost:8081/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "Str0ng!Pass",
    "email": "john@example.com",
    "age": 25,
    "reason": "I want to buy ASL products"
  }'
```

---

### POST `/api/auth/login` — Authenticate user 🌐

**Request:**
```json
{
  "email": "john@example.com",
  "password": "Str0ng!Pass"
}
```

**Response** `201 Created`:
- **Body:** Empty
- **Headers:**
  - `Set-Cookie: access_token=<jwt>; Path=/; HttpOnly; Secure; SameSite=Lax; Max-Age=7200`
  - `Set-Cookie: refresh_token=<jwt>; Path=/api/auth/refresh; HttpOnly; Secure; SameSite=Lax; Max-Age=604800`
  - `Location: /api/users/me`

**cURL:**
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "email": "john@example.com",
    "password": "Str0ng!Pass"
  }'
```

---

### GET `/api/auth/verify?token={token}` — Verify email 🌐

**Response** `200 OK`:
```json
{
  "message": "Account verified successfully"
}
```

---

### POST `/api/auth/refresh` — Refresh access token 🔐

Requires the `refresh_token` cookie. Returns a new `access_token` cookie.

**cURL:**
```bash
curl -X POST http://localhost:8081/api/auth/refresh \
  -b cookies.txt -c cookies.txt
```

---

### POST `/api/auth/logout` — Log out current session 🔐

Clears both cookies and blacklists the refresh token in Redis.

**Response** `200 OK`:
```json
{
  "message": "Logout successful"
}
```

---

### POST `/api/auth/logoutall` — Log out all sessions 🔐

Revokes all refresh tokens for the authenticated user.

**Response** `200 OK`:
```json
{
  "message": "Logout successful"
}
```

---

## 2. Users (`/api/users`)

### GET `/api/users/me` — Get my profile 🔐

**Response** `200 OK`:
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "age": 25,
  "imageURL": "/uploads/4b4027b0-b6e6-456b-af0c-ed1eb199897c.jpg",
  "addresses": [
    {
      "id": 1,
      "street": "123 Rue Didouche Mourad",
      "wilaya": "Alger",
      "commune": "Bab El Oued",
      "codePostal": "16000"
    }
  ],
  "role": ["ROLE_USER"]
}
```

---

### PATCH `/api/users/me` — Update my profile 🔐

**Request** (all fields optional):
```json
{
  "username": "new_username",
  "email": "newemail@example.com",
  "age": 30
}
```

**Response** `200 OK`:
```json
{
  "id": 1,
  "username": "new_username",
  "email": "newemail@example.com",
  "age": 30,
  "imageURL": null,
  "role": ["ROLE_USER"]
}
```

---

### POST `/api/users/me/address` — Add an address 🔐

**Request:**
```json
{
  "street": "123 Rue Didouche Mourad",
  "wilaya": "Alger",
  "commune": "Bab El Oued",
  "codePostal": "16000"
}
```

**Response** `201 Created`:
```json
{
  "id": 1,
  "street": "123 Rue Didouche Mourad",
  "wilaya": "Alger",
  "commune": "Bab El Oued",
  "codePostal": "16000"
}
```

---

### DELETE `/api/users/me/address/{id}` — Remove an address 🔐

**Response** `202 Accepted`:
```
the address was delete
```

---

### POST `/api/users/{id}/update-image` — Upload profile image 🔐

**Request:** `multipart/form-data` with field `file`

**cURL:**
```bash
curl -X POST http://localhost:8081/api/users/1/update-image \
  -b cookies.txt \
  -F "file=@profile.jpg"
```

**Response** `200 OK`:
```
Image uploaded successfully. New URL: /uploads/uuid.jpg
```

---

### DELETE `/api/users/me` — Delete my account 🔐

**Response** `200 OK`:
```
User deletes
```

---

### GET `/api/users/alluser` — List all users (paginated) 🔑 ADMIN

**Query Params:** `page` (0), `size` (10), `sortBy` (id), `direction` (asc)

**cURL:**
```bash
curl "http://localhost:8081/api/users/alluser?page=0&size=10&sortBy=id&direction=asc" \
  -b cookies.txt
```

**Response** `200 OK`: Paginated `userDTO` objects.

---

### GET `/api/users/users-with-addresses` — List users with addresses 🔑 ADMIN

Same pagination params. Returns `userWithAddressResponseDTO` objects.

---

### GET `/api/users/find/{id}` — Find user by ID 🔑 ADMIN

**Response** `200 OK`:
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "age": 25,
  "imageURL": null,
  "role": ["ROLE_USER"]
}
```

---

### PATCH `/api/users/setrole/{id}` — Set user role 🔑 ADMIN

**Request:**
```json
{
  "role": "ROLE_SELLER"
}
```

Valid values: `ROLE_USER`, `ROLE_SELLER`, `ROLE_ADMIN`

**Response** `200 OK`: Updated `userDTO`.

---

### DELETE `/api/users/Delete/{id}` — Delete a user 🔑 ADMIN

**Response** `200 OK`:
```
User deletes
```

---

## 3. Products (`/api/products`)

### GET `/api/products` — List all products (paginated) 🔐

**Query Params:**

| Param | Default | Description |
|-------|---------|-------------|
| `page` | `0` | Page number (zero-based) |
| `size` | `10` | Page size |
| `sortBy` | `id` | Sort field (`id`, `name`, `price`, `stock`) |
| `direction` | `asc` | `asc` or `desc` |

**cURL:**
```bash
curl "http://localhost:8081/api/products?page=0&size=10&sortBy=price&direction=asc" \
  -b cookies.txt
```

**Response** `200 OK`: Spring `Page<Product>` wrapper.

---

### GET `/api/products/category/{category}` — Filter by category 🔐

**cURL:**
```bash
curl "http://localhost:8081/api/products/category/Electronics?page=0&size=10" \
  -b cookies.txt
```

---

### GET `/api/products/search?q={query}` — Search products 🔐

**cURL:**
```bash
curl "http://localhost:8081/api/products/search?q=shirt&page=0&size=10" \
  -b cookies.txt
```

---

### POST `/api/products/add-produit` — Create a product 🔑 ADMIN / SELLER

**Content-Type:** `multipart/form-data`

| Part | Type | Required | Description |
|------|------|----------|-------------|
| `produit` | JSON string | ✅ | Product data |
| `file` | File | ❌ | Product image |

**JSON for `produit` part:**
```json
{
  "name": "ASL T-Shirt",
  "price": 29.99,
  "description": "High quality cotton T-Shirt",
  "category": { "id": 1, "name": "Clothing" },
  "stock": 100
}
```

**cURL:**
```bash
curl -X POST http://localhost:8081/api/products/add-produit \
  -b cookies.txt \
  -F 'produit={"name":"ASL T-Shirt","price":29.99,"description":"High quality cotton T-Shirt","category":{"id":1,"name":"Clothing"},"stock":100}' \
  -F "file=@tshirt.jpg"
```

**Response** `201 Created`:
```json
{
  "id": 1,
  "name": "ASL T-Shirt",
  "price": 29.99,
  "description": "High quality cotton T-Shirt",
  "imageURL": "/uploads/uuid.jpg",
  "category": { "id": 1, "name": "Clothing" },
  "stock": 100
}
```

---

### PUT `/api/products/{id}` — Update a product 🔑 ADMIN / SELLER

**Request:**
```json
{
  "name": "ASL Premium T-Shirt",
  "price": 39.99,
  "description": "Updated description",
  "category": { "id": 1, "name": "Clothing" },
  "stock": 80
}
```

**Response** `200 OK`: Updated `ProductResponseDTO`.

---

### PATCH `/api/products/{id}/stock?stock={qty}` — Update stock 🔑 ADMIN / SELLER

**cURL:**
```bash
curl -X PATCH "http://localhost:8081/api/products/1/stock?stock=150" \
  -b cookies.txt
```

**Response** `200 OK`: Updated `ProductResponseDTO`.

---

### POST `/api/products/{id}/update-image` — Upload product image 🔑 ADMIN / SELLER

**Request:** `multipart/form-data` with field `file`

**Response** `200 OK`:
```
Image uploaded successfully. New URL: /uploads/uuid.jpg
```

---

## 4. Categories (`/api/category`)

### GET `/api/category/all` — List all categories (paginated) 🔐

**Query Params:** `page` (0), `size` (10), `sortBy` (id), `direction` (asc)

**Response** `200 OK`:
```json
{
  "content": [
    { "id": 1, "name": "Electronics" },
    { "id": 2, "name": "Clothing" }
  ],
  "totalElements": 2,
  "totalPages": 1,
  "number": 0
}
```

---

### POST `/api/category` — Create a category 🔑 ADMIN

**Request:**
```json
{
  "name": "Electronics"
}
```

**Response** `201 Created`:
```json
{
  "id": 1,
  "name": "Electronics"
}
```

---

### PATCH `/api/category/{id}` — Update a category 🔑 ADMIN

**Request:**
```json
{
  "name": "Home & Garden"
}
```

**Response** `200 OK`: Updated `CategoryDTO`.

---

### DELETE `/api/category/{id}` — Delete a category 🔑 ADMIN

**Response** `200 OK`:
```
Deletes a category by its ID
```

---

## 5. Cart (`/api/cart`)

### POST `/api/cart/add?idProduct={id}&quantity={qty}` — Add item to cart 🔐

**cURL:**
```bash
curl -X POST "http://localhost:8081/api/cart/add?idProduct=1&quantity=2" \
  -b cookies.txt
```

**Response** `200 OK`:
```json
{
  "id": 1,
  "totalPrice": 59.98,
  "totalItems": 2,
  "items": [
    {
      "id": 1,
      "quantity": 2,
      "unitPrice": 29.99,
      "productId": 1,
      "productName": "ASL T-Shirt",
      "productImage": "/uploads/uuid.jpg",
      "subtotal": 59.98
    }
  ]
}
```

---

### GET `/api/cart` — View my cart 🔐

**Response** `200 OK`: `CartResponseDTO` (same structure as above).

---

### DELETE `/api/cart/remove/{itemId}` — Remove item from cart 🔐

**Response** `200 OK`: Updated `CartResponseDTO`.

---

## 6. Orders (`/api/orders`)

### POST `/api/orders/place` — Place an order 🔐

Converts the user's cart into an order. Validates stock availability and decreases product stock.

**Request:**
```json
{
  "shippingAddressId": 1,
  "paymentMethod": "CASH_ON_DELIVERY"
}
```

Valid payment methods: `CREDIT_CARD` · `PAYPAL` · `BANK_TRANSFER` · `CASH_ON_DELIVERY`

**Response** `201 Created`:
```json
{
  "id": 1,
  "orderNumber": "ORD-20260221-00001",
  "totalAmount": 59.98,
  "status": "NEW",
  "paymentMethod": "CASH_ON_DELIVERY",
  "shippingAddress": {
    "id": 1,
    "street": "123 Rue Didouche Mourad",
    "wilaya": "Alger",
    "commune": "Bab El Oued",
    "codePostal": "16000"
  },
  "items": [
    {
      "id": 1,
      "productId": 1,
      "productName": "ASL T-Shirt",
      "quantity": 2,
      "unitPrice": 29.99,
      "subtotal": 59.98
    }
  ],
  "createdAt": "2026-02-21T10:30:00Z",
  "updatedAt": "2026-02-21T10:30:00Z"
}
```

---

### GET `/api/orders` — List my orders (paginated) 🔐

**Query Params:** `page` (0), `size` (10), `sortBy` (createdAt), `direction` (desc)

**Response** `200 OK`: Paginated `OrderResponseDTO` objects.

---

### GET `/api/orders/{id}` — Get order details 🔐

Returns order details only if the authenticated user is the owner.

**Response** `200 OK`: `OrderResponseDTO`.

---

### PUT `/api/orders/admin/{id}/status` — Update order status 🔑 ADMIN

**Request:**
```json
{
  "status": "SHIPPED"
}
```

Valid statuses: `NEW` · `PROCESSING` · `SHIPPED` · `DELIVERED` · `CANCELLED`

**Response** `200 OK`: Updated `OrderResponseDTO`.

---

## Error Response Format

All errors follow a consistent JSON structure via the `GlobalExceptionHandler`.

### Business Error (e.g., 404, 409)
```json
{
  "status": 404,
  "message": "Product not found"
}
```

### Validation Error (400)
```json
{
  "status": 400,
  "message": "Validation failed",
  "errors": {
    "email": "must not be blank",
    "password": "size must be between 0 and 120"
  }
}
```

### Authentication Error (401)
```json
{
  "status": 401,
  "message": "Unauthorized"
}
```

### Internal Server Error (500)
```json
{
  "status": 500,
  "message": "Internal server error"
}
```
