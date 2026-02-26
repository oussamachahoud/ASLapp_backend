[← Back to README](../README.md)

# 🏗 Architecture & Tech Stack

## System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT (Browser / Mobile)                │
│                   Sends HttpOnly Cookie on each request         │
└──────────────────────────────┬──────────────────────────────────┘
                               │  HTTPS
                               ▼
┌──────────────────────────────────────────────────────────────────┐
│                      SPRING BOOT APPLICATION                     │
│                                                                  │
│  ┌────────────────┐   ┌──────────────┐   ┌───────────────────┐  │
│  │  JWT Filter    │──▶│  Controllers │──▶│     Services       │  │
│  │  (Cookie-based)│   │  (REST API)  │   │  (Business Logic)  │  │
│  └────────────────┘   └──────────────┘   └─────────┬─────────┘  │
│                                                     │            │
│                              ┌───────────────────────┤            │
│                              │                       │            │
│                              ▼                       ▼            │
│                     ┌──────────────┐       ┌──────────────────┐  │
│                     │ Repositories │       │  Storage Service  │  │
│                     │    (JPA)     │       │ Local/Azure/AWS   │  │
│                     └──────┬───────┘       └──────────────────┘  │
│                            │                                     │
└────────────────────────────┼─────────────────────────────────────┘
                             │
              ┌──────────────┼──────────────┐
              ▼              ▼              ▼
     ┌──────────────┐ ┌──────────┐  ┌─────────────┐
     │  PostgreSQL   │ │  Redis   │  │ SMTP (Brevo)│
     │  (Primary DB) │ │  (Cache) │  │  (Email)    │
     └──────────────┘ └──────────┘  └─────────────┘
```

**Architecture Style:** Monolith (modular, layered)

---

## Layer Responsibilities

| Layer | Responsibility |
|-------|---------------|
| **Filter** | Extracts JWT from `access_token` cookie, validates it, sets `SecurityContext` |
| **Controller** | HTTP routing, request validation, response shaping |
| **Service** | Business logic, transaction management, caching |
| **Repository** | Spring Data JPA interfaces for database access |
| **Storage** | Pluggable file storage (Local / Azure Blob / AWS S3) |

---

## Tech Stack

| Category | Technology |
|----------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.2.3 |
| Security | Spring Security 6 + JWT (jjwt 0.12.6) |
| Database | PostgreSQL |
| ORM | Hibernate / Spring Data JPA |
| Caching | Redis + Spring Cache |
| Email | Spring Mail + Brevo SMTP |
| Storage | Local FS / Azure Blob Storage / AWS S3 |
| Validation | Jakarta Bean Validation |
| API Docs | SpringDoc OpenAPI 2.5 (Swagger UI) |
| Build Tool | Maven |
| Testing | JUnit 5 + Spring Boot Test + Spring Security Test |
| Utilities | Lombok |

---

## Project Identification

### Problem

Small-to-medium e-commerce businesses in Algeria lack affordable, ready-to-deploy backend solutions that handle Algerian-specific logistics (wilayas, communes, postal codes) while still following modern security and scalability best practices.

### Solution

**ASLapp Backend** provides a fully-featured RESTful API covering user management, product catalogues, shopping carts, order lifecycle management, and email verification — all behind a stateless JWT security layer with refresh-token rotation and Redis-backed session management.

### Goals

| # | Goal | Status |
|---|------|--------|
| 1 | Secure, stateless authentication with HttpOnly cookie JWTs | ✅ |
| 2 | Role-based access control (User / Seller / Admin) | ✅ |
| 3 | Full product CRUD with image upload (Local / Azure Blob / AWS S3) | ✅ |
| 4 | Shopping cart with real-time price recalculation | ✅ |
| 5 | Order placement with stock validation & automatic totals | ✅ |
| 6 | Email verification on signup via Brevo SMTP | ✅ |
| 7 | Redis caching for high-traffic read endpoints | ✅ |
| 8 | Paginated, sortable API responses across all listing endpoints | ✅ |
| 9 | Interactive API documentation via Swagger UI | ✅ |

---

## Folder Structure

```
src/main/java/com/example/aslapp_backend/
│
├── AsLappBackendApplication.java          # Application entry point
│
├── Config/
│   ├── ApplicationConfiguration.java      # AuthenticationProvider, PasswordEncoder, UserDetailsService beans
│   ├── AsyncConfig.java                   # @Async thread pool configuration
│   ├── CacheConfig.java                   # Redis cache manager & RedisTemplate beans
│   ├── EmailConfig.java                   # Mail sender configuration
│   ├── JwtauthenticationFilter.java       # OncePerRequestFilter — extracts JWT from cookie
│   ├── OpenApiConfig.java                 # Swagger/OpenAPI metadata
│   ├── SecurityConfiguration.java         # SecurityFilterChain, CORS, CSRF, session policy
│   └── WebConfig.java                     # Static resource handler for /uploads/**
│
├── controller/
│   ├── authenticationController.java      # Signup, login, verify email, refresh, logout
│   ├── UserController.java               # Profile CRUD, address management, admin user ops
│   ├── ProductController.java            # Product CRUD, image upload, search, pagination
│   ├── CategoryController.java           # Category CRUD (admin only)
│   ├── CartController.java               # Add/view/remove cart items
│   ├── OrderController.java              # Place order, list orders, update status
│   └── GlobalExceptionHandler.java       # Centralised @RestControllerAdvice error handling
│
├── DTOs/
│   ├── modelDTOs/
│   │   └── userDTO.java                  # Lightweight user summary
│   ├── requestDTOs/
│   │   ├── SignupDto.java                # Registration payload
│   │   ├── LoginDto.java                 # Login payload
│   │   ├── ProduitDto.java               # Product create/update payload
│   │   ├── PlaceOrderDTO.java            # Order placement payload
│   │   ├── UpdateOrderStatusDTO.java     # Admin order-status update
│   │   ├── UpdateUserDTO.java            # Partial profile update
│   │   ├── AddressRequestDTO.java        # New address payload
│   │   └── CategoryRequestDTO.java       # Category create payload
│   └── responseDTOs/
│       ├── ProductResponseDTO.java       # Product detail response
│       ├── OrderResponseDTO.java         # Order overview response
│       ├── OrderItemResponseDTO.java     # Order line-item detail
│       ├── CartResponseDTO.java          # Cart overview response
│       ├── CartItemDTO.java              # Cart line-item detail
│       ├── CategoryDTO.java              # Category response
│       ├── AddressResponseDTO.java       # Address detail (record)
│       ├── TokenResponse.java            # Token pair response
│       └── userWithAddressResponseDTO.java # Full user profile with addresses (record)
│
├── event/
│   └── UserRegisteredEvent.java          # Application event fired on signup (async email)
│
├── Exeption/
│   └── BusinessException.java            # Custom runtime exception with HttpStatus
│
├── models/
│   ├── BaseEntity.java                   # Auditing: createdAt, modifiedAt
│   ├── User.java                         # UserDetails implementation
│   ├── Role.java                         # ROLE_USER, ROLE_SELLER, ROLE_ADMIN
│   ├── Product.java                      # Product entity with stock management
│   ├── Category.java                     # Product category
│   ├── Cart.java                         # One-to-one with User, auto-recalculates totals
│   ├── CartItem.java                     # Line item inside a cart
│   ├── Order.java                        # Order with status, payment method, shipping address
│   ├── OrderItem.java                    # Snapshot of product at order time
│   ├── Address.java                      # Algerian address: street, wilaya, commune, code postal
│   └── Enum/
│       ├── ERole.java                    # ROLE_USER | ROLE_SELLER | ROLE_ADMIN
│       ├── OderStatus.java               # NEW | PROCESSING | SHIPPED | DELIVERED | CANCELLED
│       └── paymentMethod.java            # CREDIT_CARD | PAYPAL | BANK_TRANSFER | CASH_ON_DELIVERY
│
├── repositories/                         # Spring Data JPA interfaces
│   ├── UserRepository.java
│   ├── RoleRepository.java
│   ├── ProductRepository.java
│   ├── CategoryRepository.java
│   ├── CartRepository.java
│   ├── CartItemRepository.java
│   ├── OrderRepository.java
│   └── AddressRepository.java
│
└── sevices/                              # Business logic layer
    ├── AuthenticationService.java        # Signup, login, email verification
    ├── JwtService.java                   # Token generation, validation, claim extraction
    ├── RefreshTokenService.java          # Redis-backed refresh-token rotation & blacklisting
    ├── UserService.java                  # User CRUD, address management, role assignment
    ├── ProductService.java               # Product CRUD, search, stock management
    ├── CategoryService.java              # Category CRUD
    ├── CartService.java                  # Cart operations (add, remove, view)
    ├── OrderService.java                 # Order placement, status transitions
    ├── EmailService.java                 # Async email dispatch (verification, notifications)
    ├── StorageService.java               # Storage strategy interface
    ├── LocalStorageService.java          # Local filesystem implementation
    ├── AzureBlobStorageService.java       # Azure Blob Storage implementation
    └── AwsS3StorageService.java          # AWS S3 implementation
```
