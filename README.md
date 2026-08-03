# Multi-Warehouse Order Fulfillment System

**`Java 21`  ·  `Spring Boot 4.1`  ·  `GraphQL`  ·  `gRPC`  ·  `Kafka`  ·  `Redis`  ·  `PostgreSQL`  ·  `Docker`  ·  `Kubernetes`**

A production-grade, event-driven Order Fulfillment Platform built on Java Spring Boot microservices — modeled after the backend fulfillment architecture used by Amazon, Flipkart, Blinkit, and Myntra.

> **Build:** passing &nbsp;|&nbsp; **Services:** 11 &nbsp;|&nbsp; **License:** MIT &nbsp;|&nbsp; **Status:** Active development

---

## Why This Project

Most backend portfolio projects are CRUD apps with a database attached. This one is different — it simulates the actual hard problem fulfillment platforms solve: **reserving inventory correctly under concurrency, splitting a single order across multiple warehouses, and keeping order/inventory/shipment state consistent across independently deployed services** that only talk to each other over gRPC and Kafka, never by sharing a database.

If you're reviewing this as a hiring manager or engineer: start with [Architecture](#architecture), then [Multi-Warehouse Allocation](#multi-warehouse-allocation-logic), then the [Order Processing Sequence](#order-processing-sequence).

---

## Table of Contents

- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Microservices](#microservices)
- [Roles & Permissions](#roles--permissions)
- [Multi-Warehouse Allocation Logic](#multi-warehouse-allocation-logic)
- [Order Processing Sequence](#order-processing-sequence)
- [Redis Strategy](#redis-strategy)
- [Kafka Topics](#kafka-topics)
- [gRPC Contracts](#grpc-contracts)
- [JWT & Security](#jwt--security)
- [Database Design](#database-design)
- [GraphQL Examples](#graphql-examples)
- [Folder Structure](#folder-structure)
- [Getting Started](#getting-started)
- [Kubernetes Deployment](#kubernetes-deployment)
- [Observability](#observability)
- [Features](#features)
- [Roadmap](#roadmap)
- [Resume Highlights](#resume-highlights)
- [License](#license)
- [Author](#author)

---

## Architecture

```
                                   +-------------------------+
                                   |       Client Apps         |
                                   |  Web . Mobile . Postman    |
                                   +-------------+-------------+
                                                 |  HTTPS + JWT
                                                 v
                                   +-------------------------+
                                   |       API Gateway          |<----- Service Registry
                                   |  Routing . JWT Auth         |       (Eureka :8761)
                                   |         :8080                |
                                   +-------------+-------------+
                                                 |
        +---------------+---------------+-------+-------+---------------+---------------+
        v               v               v               v               v               v
  +----------+   +--------------+  +----------+   +----------+   +--------------+  +----------+
  | Product  |   |  Inventory    |  |  User    |   |  Cart    |   |   Order       |  |Shipment  |
  | Service  |   |  Service      |  | Service  |   | Service  |   |   Service     |  | Service  |
  |  :8081   |   |   :8082       |  |  :8083   |   |  :8085   |   |   :8086       |  |  :8087   |
  +----+-----+   +------+-------+  +----+-----+   +----+-----+   +-------+-------+  +----+-----+
       |                |                |              |                 |                |
       |                |                |              |         +-------+--------+       |
       |                |                |              |         |  gRPC (sync)     |       |
       |                |                |              |         |  -> Pricing       |       |
       |                |                |              |         |  -> Inventory     |       |
       |                |                |              |         +----------------+       |
       v                v                v              v                 v                v
  product_db      inventory_db      user_db        cart_db          order_db          shipment_db
       |                |                                                 |                  |
       +-------+--------+                                                 +--------+---------+
               v                                                                   v
        +-------------+                                                    +-------------+
        |  Redis        |                                                    |  Kafka        |
        |  catalog +    |                                                    |  order /      |
        |  stock cache  |                                                    |  inventory /  |
        +-------------+                                                    |  shipment      |
                                                                             +------+------+
                                                                                    v
                                                                       +--------------------------+
                                                                       |  Notification Service      |
                                                                       |  :8088 . notification_db    |
                                                                       +------------+-------------+
                                                                                    v
                                                                          Email . SMS (planned)
```

**Design principles**

| Principle | How it's applied |
|---|---|
| Database per service | Every service owns its schema exclusively — no shared tables, no cross-service joins |
| Sync where correctness matters | Order to Inventory/Pricing use gRPC for immediate, strongly-consistent responses |
| Async where coupling should be loose | Order to Notification/Shipment happen via Kafka so producers never block on consumers |
| Cache what's read far more than it's written | Product catalog & inventory snapshots sit in Redis, invalidated on write |
| Stateless services | JWT carries identity/role; any service instance can serve any request |

---

## Tech Stack

| Layer | Technology | Why |
|---|---|---|
| Language | Java 21 | Records, virtual threads, pattern matching |
| Framework | Spring Boot 4.1 | Mature ecosystem, production-ready defaults |
| API Layer | GraphQL | Clients fetch exactly the fields they need across nested domain objects |
| Internal RPC | gRPC | Low-latency, strongly-typed contracts between Order and Inventory/Pricing |
| Database | PostgreSQL | ACID transactions for inventory reservation correctness |
| Cache | Redis | Sub-millisecond reads for catalog & inventory snapshots |
| Messaging | Kafka | Durable, replayable event log decoupling producers/consumers |
| Security | Spring Security + JWT | Stateless auth, RBAC enforced at the gateway |
| Discovery | Eureka | Dynamic service registration, no hardcoded hosts |
| Monitoring | Prometheus + Actuator | Metrics scraping, health probes for Kubernetes |
| Containers | Docker / Docker Compose | Local parity with production |
| Orchestration | Kubernetes | Deployment, Service, ConfigMap, Secret per service |

---

## Microservices

| Service | Port | Database | Responsibility |
|---|---|---|---|
| API Gateway | 8080 | – | Routing, JWT validation, rate limiting |
| Product Service | 8081 | `product_db` | Product catalog CRUD & search |
| Inventory Service | 8082 | `inventory_db` | Warehouse inventory, reservation, multi-warehouse allocation |
| User Service | 8083 | `user_db` | Registration, login, role management |
| Pricing Service | 8084 | `pricing_db` | Base price, discount, tax, final price |
| Cart Service | 8085 | `cart_db` | Add/remove/update cart items |
| Order Service | 8086 | `order_db` | Order orchestration, the system's core coordinator |
| Shipment Service | 8087 | `shipment_db` | Shipment creation, warehouse allocation, tracking |
| Notification Service | 8088 | `notification_db` | Kafka consumer, email/SMS dispatch |
| Payment Service | 8089 | `payment_db` | Payment processing — planned, not yet implemented |
| Service Registry | 8761 | – | Eureka service discovery |

---

## Roles & Permissions

| Role | Can do |
|---|---|
| CUSTOMER | Register · Login · Browse products · Manage cart · Place/cancel orders · View orders · Track shipments |
| WAREHOUSE_MANAGER | View/add/update inventory · Process assigned orders · Update shipment status |
| ADMIN | Manage products, warehouses, users · Assign warehouse managers · View all orders & inventory system-wide |

**Shipment status lifecycle** (advanced by Warehouse Manager):

```
ALLOCATED -> PACKED -> SHIPPED -> IN_TRANSIT -> DELIVERED
```

---

## Multi-Warehouse Allocation Logic

The core problem this project solves: an order can exceed what any single warehouse holds, so the Inventory Service splits it.

**Example**

| Warehouse | Stock Available |
|---|---|
| Warehouse A | 20 units |
| Warehouse B | 30 units |
| Warehouse C | 50 units |

An order for **60 units** resolves to:

```
Warehouse A -> 20 units
Warehouse B -> 30 units
Warehouse C -> 10 units
------------------------
Total          60 units
```

**Allocation priority:** nearest warehouse → lowest shipping cost → available inventory required → split across warehouses only if no single warehouse can fulfill the order.

**Reservation state machine:**

```
Order Created    ->  available_qty down,  reserved_qty up
Order Cancelled  ->  reserved_qty down,   available_qty up
```

This prevents overselling: `available_qty` is decremented at reservation time, not at shipment time.

---

## Order Processing Sequence

```
1.  Customer logs in                        -> User Service issues JWT
2.  Customer browses products                -> Product Service (Redis cache-first)
3.  Customer adds items to cart              -> Cart Service
4.  Customer places order                    -> Order Service
5.  Order Service reserves inventory         -> gRPC call to Inventory Service
6.  Order Service calculates final price     -> gRPC call to Pricing Service
7.  Order persisted, status = CONFIRMED      -> order_db (transactional write)
8.  Order Service publishes "order-created"  -> Kafka
9.  Notification Service consumes event      -> Sends confirmation email
10. Shipment Service consumes event          -> Allocates warehouse(s), generates tracking number
11. Warehouse Manager updates shipment       -> ALLOCATED -> PACKED -> SHIPPED -> IN_TRANSIT -> DELIVERED
12. Customer tracks shipment                 -> GraphQL query returns tracking details
13. (Optional) Order cancelled               -> Inventory released, "order-cancelled" published
```

---

## Redis Strategy

```
Client -> Product Service -> Redis Cache
                                 |
                     -----------------------
                     |                     |
                    HIT                  MISS
                     |                     |
              Return cached           PostgreSQL
                response                    |
                                             v
                                   Write-through to Redis
                                             |
                                             v
                                     Return response
```

| Purpose | Key Pattern | TTL |
|---|---|---|
| Product catalog | `product::{productId}` | 10 min |
| Inventory snapshot | `inventory::{productId}` | 30 sec (short — stock changes fast) |

Invalidation happens on `updateProduct` / `deleteProduct` and on every inventory reservation/release.

---

## Kafka Topics

| Topic | Producer | Consumer(s) | Purpose |
|---|---|---|---|
| `order-created` | Order Service | Notification, Shipment | Trigger shipment creation + confirmation email |
| `order-cancelled` | Order Service | Notification, Inventory | Release reservation, notify customer |
| `inventory-reserved` | Inventory Service | Notification | Audit trail / customer alert |
| `inventory-released` | Inventory Service | Notification | Audit trail / customer alert |
| `shipment-created` | Shipment Service | Notification | Tracking number email |
| `shipment-delivered` | Shipment Service | Notification | Delivery confirmation |

All consumers are idempotent (dedup by event ID) with retry + dead-letter-queue handling for poison messages.

---

## gRPC Contracts

```protobuf
// Inventory Service
service InventoryService {
  rpc CheckInventory(InventoryRequest) returns (InventoryListResponse);
}

// Pricing Service
service PricingService {
  rpc GetPriceByProductId(PriceRequest) returns (PriceResponse);
}
```

| Caller | Callee | RPC |
|---|---|---|
| Order Service | Inventory Service | `CheckInventory` |
| Order Service | Pricing Service | `GetPriceByProductId` |

---

## JWT & Security

```
Client -> User Service -> JWT Issued (userId + role, signed)
                                |
                                v
                    API Gateway validates
                    signature + expiry + role
                                |
                                v
                Forwarded to downstream service
```

- Stateless auth — no server-side session store
- RBAC enforced centrally at the Gateway, re-checked per-service for defense in depth
- Roles: `CUSTOMER`, `WAREHOUSE_MANAGER`, `ADMIN`

---

## Database Design

Database-per-service — no service ever queries another service's tables directly.

| Database | Owner | Core Tables |
|---|---|---|
| `user_db` | User Service | `users` |
| `product_db` | Product Service | `products` |
| `inventory_db` | Inventory Service | `warehouses`, `inventory`, `inventory_reservations` |
| `pricing_db` | Pricing Service | `pricing` |
| `cart_db` | Cart Service | `carts`, `cart_items` |
| `order_db` | Order Service | `orders`, `order_items` |
| `shipment_db` | Shipment Service | `shipments`, `shipment_tracking` |
| `notification_db` | Notification Service | `notifications` |
| `payment_db` | Payment Service (planned) | `payments`, `payment_transactions` |

Order Service uses row-level locking (`SELECT ... FOR UPDATE`) during inventory reservation to prevent race conditions under concurrent order placement.

---

## GraphQL Examples

**Product Service — create a product**

```graphql
mutation {
  createProduct(product: {
    productSku: "SKU-1001"
    productName: "Wireless Mouse"
    productCategory: "Electronics"
    productPrice: 799.0
  }) {
    productId
    productName
  }
}
```

**Cart Service — add an item**

```graphql
mutation {
  addToCart(request: { userId: 12, productId: 45, quantity: 2 }) {
    cartId
    cartItems { productId quantity }
  }
}
```

**Order Service — place & query an order**

```graphql
mutation {
  placeOrder(request: { userId: 12 }) {
    orderId
    orderStatus
    totalAmount
  }
}

query {
  ordersByUser(userId: 12) {
    orderId
    orderStatus
    orderItems { productId quantity price }
  }
}
```

**Inventory Service — check stock across warehouses**

```graphql
query {
  inventoryByProduct(productId: 45) {
    warehouseName
    availableQyt
    reservedQyt
  }
}
```

---

## Folder Structure

```
Multi-Warehouse-Order-Fulfillment-System/
├── api-gateway/
├── service-registry/
├── user-service/
├── product-service/
├── inventory-service/
├── pricing-service/
├── cart-service/
├── order-service/
├── shipment-service/
├── notification-service/
├── payment-service/          # planned
├── docker/
├── kubernetes/
│   ├── deployments/
│   ├── services/
│   ├── configmaps/
│   └── secrets/
├── monitoring/
│   ├── prometheus/
│   └── grafana/
├── docker-compose.yml
├── LICENSE
└── README.md
```

---

## Getting Started

**Prerequisites**

| Tool | Version |
|---|---|
| Java | 21+ |
| Maven | 3.9+ |
| Docker & Docker Compose | Latest |
| PostgreSQL client (optional) | For manual DB inspection |

**1. Clone & build**

```bash
git clone <repository-url>
cd Multi-Warehouse-Order-Fulfillment-System
mvn clean install
```

**2. Start infrastructure + services**

```bash
docker compose up --build
```

**3. Verify services are up**

```bash
curl http://localhost:8761
curl http://localhost:8080/actuator/health
```

**4. Service endpoints**

| Service | URL |
|---|---|
| Eureka Dashboard | http://localhost:8761 |
| API Gateway | http://localhost:8080 |
| Product Service | http://localhost:8081/graphql |
| Inventory Service | http://localhost:8082/graphql |
| User Service | http://localhost:8083/graphql |
| Pricing Service | http://localhost:8084/graphql |
| Cart Service | http://localhost:8085/graphql |
| Order Service | http://localhost:8086/graphql |

---

## Kubernetes Deployment

```bash
kubectl create namespace fulfillment
kubectl apply -f kubernetes/configmaps/ -n fulfillment
kubectl apply -f kubernetes/secrets/ -n fulfillment
kubectl apply -f kubernetes/deployments/ -n fulfillment
kubectl apply -f kubernetes/services/ -n fulfillment

kubectl get pods -n fulfillment
kubectl rollout status deployment/order-service -n fulfillment
```

Each service ships a `Deployment`, `Service`, `ConfigMap`, and `Secret` manifest.

---

## Observability

| Endpoint | Purpose |
|---|---|
| `/actuator/health` | Liveness/readiness probes for Kubernetes |
| `/actuator/prometheus` | Metrics scrape target |
| Structured JSON logs | Correlation IDs propagated across service calls |

---

## Features

**Platform**

- GraphQL APIs across all services
- Eureka-based service discovery
- Centralized JWT validation at the gateway
- Database-per-service isolation
- Redis caching for catalog & inventory
- Kafka event streaming
- gRPC internal communication
- Role-based access control
- Multi-warehouse order splitting

**Operations**

- Inventory reservation & release
- Overselling prevention
- Shipment tracking with status history
- Auto-generated tracking numbers
- Email notifications (SMS-ready)
- Docker & Docker Compose
- Full Kubernetes manifest set
- Prometheus + Actuator monitoring
- Idempotent Kafka consumers + DLQ strategy

---

## Roadmap

| Feature | Status |
|---|---|
| Payment Service integration | Planned |
| SMS notifications | Planned |
| Elasticsearch-based product search | Planned |
| Distributed tracing (Zipkin/Jaeger) | Planned |
| Saga pattern for cross-service transactions | Planned |
| CI/CD pipeline (GitHub Actions) | Planned |
| OpenTelemetry instrumentation | Planned |
| Grafana dashboards | Planned |
| Return & refund management | Planned |

---

## Resume Highlights

- Designed and built a production-inspired microservices platform across 10+ independently deployable Spring Boot services using GraphQL, gRPC, Kafka, Redis, and PostgreSQL.
- Implemented multi-warehouse inventory allocation logic that splits order quantities across warehouses while preventing overselling through reservation-based concurrency control.
- Built an event-driven notification pipeline with idempotent Kafka consumers, decoupling order, inventory, and shipment workflows from downstream notification handling.
- Designed GraphQL schemas for product, cart, order, and inventory domains alongside gRPC contracts for low-latency Order to Inventory/Pricing communication.
- Secured all APIs with JWT authentication and role-based access control enforced at the API Gateway layer.
- Containerized all services with Docker and authored complete Kubernetes manifest sets (Deployment, Service, ConfigMap, Secret) for production-style deployment.
- Instrumented every service with Spring Boot Actuator and Prometheus for health checks and metrics collection.

---

## Author

**Pranjal Tiwari**
Backend Developer · Java · Spring Boot · GraphQL · gRPC · PostgreSQL · Redis · Kafka · Docker · Kubernetes

| | |
|---|---|
| GitHub | `github.com/pranjal3014` |
| LinkedIn | `linkedin.com/in/pranjal-tiwari-it` |
| Email | `pranjaltiwari7188@gmail.com` |