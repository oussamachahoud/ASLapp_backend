[← Back to README](../README.md)

# 🗄 Database Design

## Entity-Relationship Overview

```
┌──────────┐       ┌──────────┐
│  roles   │◀─────▶│  users   │  (Many-to-Many via user_roles)
└──────────┘       └────┬─────┘
                        │ 1
           ┌────────────┼────────────┐
           │            │            │
           ▼ *          ▼ 1          ▼ *
     ┌──────────┐  ┌─────────┐  ┌──────────┐
     │ addresses │  │  cart   │  │  orders  │
     └──────────┘  └────┬────┘  └────┬─────┘
                        │ 1          │ 1
                        ▼ *          ▼ *
                  ┌───────────┐  ┌────────────┐
                  │ cart_items│  │ order_items │
                  └─────┬─────┘  └──────┬─────┘
                        │               │
                        ▼ *             ▼ *
                   ┌───────────┐   ┌───────────┐
                   │ products  │   │ products  │
                   └─────┬─────┘   └───────────┘
                         │ *
                         ▼ 1
                   ┌────────────┐
                   │ categories │
                   └────────────┘
```

---

## Tables

### `users`

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id` | `BIGINT` | PK, auto-increment | — |
| `username` | `VARCHAR(20)` | UNIQUE, NOT NULL | — |
| `email` | `VARCHAR(50)` | UNIQUE, NOT NULL | Validated with `@Email` |
| `password` | `VARCHAR(255)` | NOT NULL | BCrypt hashed |
| `age` | `INT` | ≥ 0 | — |
| `image_url` | `VARCHAR(255)` | nullable | Profile picture path |
| `enabled` | `BOOLEAN` | NOT NULL | `false` until email verified |
| `created_at` | `TIMESTAMP` | NOT NULL, immutable | JPA auditing |
| `modified_at` | `TIMESTAMP` | NOT NULL | JPA auditing |

Implements Spring Security `UserDetails`. Authorities derived from the `roles` relationship.

---

### `roles`

| Column | Type | Constraints |
|--------|------|-------------|
| `id` | `BIGINT` | PK, auto-increment |
| `name` | `VARCHAR` | UNIQUE, NOT NULL, ENUM |

---

### `user_roles` (join table)

| Column | Type | Constraints |
|--------|------|-------------|
| `user_id` | `BIGINT` | FK → `users.id` |
| `role_id` | `BIGINT` | FK → `roles.id` |

---

### `addresses`

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id` | `BIGINT` | PK, auto-increment | — |
| `street` | `VARCHAR` | NOT NULL | — |
| `wilaya` | `VARCHAR(100)` | NOT NULL | Algerian province |
| `commune` | `VARCHAR(100)` | NOT NULL | — |
| `code_postal` | `VARCHAR(10)` | NOT NULL | — |
| `user_id` | `BIGINT` | FK → `users.id`, NOT NULL | — |
| `order_id` | `BIGINT` | FK → `orders.id`, nullable | Shipping address reference |

---

### `products`

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id` | `BIGINT` | PK, auto-increment | — |
| `name` | `VARCHAR(125)` | NOT NULL | — |
| `price` | `DOUBLE` | NOT NULL | — |
| `description` | `TEXT` | nullable | — |
| `image_url` | `VARCHAR(255)` | nullable | — |
| `stock` | `INT` | NOT NULL, ≥ 0 | Validated on order placement |
| `category_id` | `BIGINT` | FK → `categories.id` | — |
| `created_at` | `TIMESTAMP` | NOT NULL, immutable | JPA auditing |
| `modified_at` | `TIMESTAMP` | NOT NULL | JPA auditing |

---

### `categories`

| Column | Type | Constraints |
|--------|------|-------------|
| `id` | `BIGINT` | PK, auto-generated |
| `name` | `VARCHAR` | nullable |
| `created_at` | `TIMESTAMP` | NOT NULL, immutable |
| `modified_at` | `TIMESTAMP` | NOT NULL |

---

### `cart`

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id` | `BIGINT` | PK, auto-increment | — |
| `total_price` | `DECIMAL(12,2)` | NOT NULL, ≥ 0 | Auto-recalculated |
| `total_items` | `INT` | NOT NULL, ≥ 0 | Auto-recalculated |
| `user_id` | `BIGINT` | FK → `users.id`, UNIQUE | One-to-one |
| `created_at` | `TIMESTAMP` | NOT NULL, immutable | JPA auditing |
| `modified_at` | `TIMESTAMP` | NOT NULL | JPA auditing |

---

### `cart_items`

| Column | Type | Constraints |
|--------|------|-------------|
| `id` | `BIGINT` | PK, auto-increment |
| `quantity` | `INT` | NOT NULL, ≥ 0 |
| `unit_price` | `DOUBLE` | NOT NULL, ≥ 0 |
| `product_id` | `BIGINT` | FK → `products.id` |
| `cart_id` | `BIGINT` | FK → `cart.id` |

Orphan removal enabled — removing from the cart collection deletes the row.

---

### `orders`

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id` | `BIGINT` | PK, auto-generated | — |
| `order_number` | `VARCHAR(50)` | UNIQUE, NOT NULL | e.g. `ORD-20260221-00001` |
| `total_amount` | `DECIMAL(19,2)` | NOT NULL | Auto-recalculated from items |
| `oder_status` | `VARCHAR` | NOT NULL, ENUM | See enumerations below |
| `payment_method` | `VARCHAR(50)` | NOT NULL, ENUM | See enumerations below |
| `user_id` | `BIGINT` | FK → `users.id` | — |
| `shipping_address_id` | `BIGINT` | FK → `addresses.id`, NOT NULL | — |
| `created_at` | `TIMESTAMP` | NOT NULL, immutable | JPA auditing |
| `modified_at` | `TIMESTAMP` | NOT NULL | JPA auditing |

---

### `order_items`

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id` | `BIGINT` | PK, auto-increment | — |
| `quantity` | `INT` | NOT NULL, ≥ 0 | — |
| `unitprice` | `DOUBLE` | NOT NULL, ≥ 0 | Price snapshot at order time |
| `product_id` | `BIGINT` | FK → `products.id` | — |
| `order_id` | `BIGINT` | FK → `orders.id` | — |

---

## Enumerations

### `ERole`

| Value |
|-------|
| `ROLE_USER` |
| `ROLE_SELLER` |
| `ROLE_ADMIN` |

### `OderStatus`

| Value | Description |
|-------|-------------|
| `NEW` | Order just placed |
| `PROCESSING` | Payment confirmed, preparing shipment |
| `SHIPPED` | Handed to delivery partner |
| `DELIVERED` | Received by customer |
| `CANCELLED` | Cancelled by customer or admin |

### `paymentMethod`

| Value |
|-------|
| `CREDIT_CARD` |
| `PAYPAL` |
| `BANK_TRANSFER` |
| `CASH_ON_DELIVERY` |

---

## Auditing

All entities extending `BaseEntity` automatically receive:

- `createdAt` — set once on insert (`@CreatedDate`)
- `modifiedAt` — updated on every save (`@LastModifiedDate`)

Enabled via `@EnableJpaAuditing` on the application class.
