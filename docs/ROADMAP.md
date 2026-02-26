[← Back to README](../README.md)

# 🔮 Roadmap

Planned features and improvements for the ASLapp Backend, ordered by priority.

---

## 🔴 High Priority

| Feature | Description | Status |
|---------|-------------|--------|
| **Payment Gateway** | Integrate Stripe, CCP (Algérie Poste), or BaridiMob for real payment processing | 📋 Planned |
| **Rate Limiting** | Redis-based request throttling per user / IP to prevent abuse | 📋 Planned |
| **Production CORS** | Restrict `allowedOrigins` to the actual frontend domain | 📋 Planned |
| **Secrets Management** | Move sensitive values (JWT secret, SMTP password) to a vault or `.env` file excluded from VCS | 📋 Planned |

---

## 🟠 Medium Priority

| Feature | Description | Status |
|---------|-------------|--------|
| **Product Reviews & Ratings** | Let users rate and review purchased products (1–5 stars + comment) | 📋 Planned |
| **Wishlist** | Users can save products for later and receive price-drop alerts | 📋 Planned |
| **Order Notifications** | Real-time order status updates via WebSocket or Server-Sent Events | 📋 Planned |
| **Shipping Integration** | Integrate with Algerian delivery services (Yalidine, ZR Express, Maystro) | 📋 Planned |
| **Coupon & Discount System** | Percentage / fixed-amount coupons with usage limits and expiry dates | 📋 Planned |
| **Order Cancellation** | Allow users to cancel orders within a configurable time window | 📋 Planned |

---

## 🟡 Low Priority

| Feature | Description | Status |
|---------|-------------|--------|
| **Full-Text Search** | Replace LIKE queries with PostgreSQL `tsvector` or Elasticsearch | 📋 Planned |
| **Inventory Alerts** | Notify sellers when stock drops below a configurable threshold | 📋 Planned |
| **Audit Log** | Track admin actions (role changes, order status updates, user deletions) | 📋 Planned |
| **CI/CD Pipeline** | GitHub Actions workflow for automated testing, building, and deploying | 📋 Planned |
| **API Versioning** | Version endpoints (`/api/v1/`, `/api/v2/`) for backward compatibility | 📋 Planned |
| **Export Reports** | Admin endpoint to export orders / sales data as CSV or PDF | 📋 Planned |

---

## 🟢 Nice to Have

| Feature | Description | Status |
|---------|-------------|--------|
| **i18n** | Multi-language support for API messages (Arabic, French, English) | 💡 Idea |
| **Responsive Email Templates** | Dark-mode-aware HTML email templates for verification & order confirmation | 💡 Idea |
| **Social Login** | OAuth2 sign-in with Google, Facebook, or Apple | 💡 Idea |
| **Admin Dashboard API** | Aggregate endpoints for sales stats, top products, user growth charts | 💡 Idea |
| **Image Optimisation** | Auto-resize and compress uploaded images (WebP conversion) | 💡 Idea |

---

## ✅ Completed

| Feature | Description |
|---------|-------------|
| JWT Cookie Authentication | Stateless auth with HttpOnly cookies, refresh rotation, Redis blacklisting |
| Role-Based Access Control | `ROLE_USER`, `ROLE_SELLER`, `ROLE_ADMIN` with method-level security |
| Product Catalogue | CRUD, search, category filtering, pagination, sorting |
| Shopping Cart | Add, remove, view with auto-recalculated totals |
| Order Management | Cart-to-order conversion, stock validation, status lifecycle |
| Email Verification | Async verification email on signup via Brevo SMTP |
| Redis Caching | Cache manager + RedisTemplate for manual & annotation-based caching |
| Multi-Provider Storage | Pluggable file storage: Local FS, Azure Blob, AWS S3 |
| Swagger / OpenAPI | Interactive docs at `/swagger-ui` |
| Global Exception Handler | Consistent error JSON across all endpoints |
| Address Management | Algerian-specific addresses (wilaya, commune, code postal) |
| JPA Auditing | Automatic `createdAt` / `modifiedAt` timestamps |
