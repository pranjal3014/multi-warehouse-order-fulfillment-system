# 🚀 Multi-Warehouse Order Fulfillment System

**A production-inspired Order Fulfillment Platform built with Java Spring Boot Microservices**

Modeled on the backend fulfillment architecture used by companies like Amazon, Flipkart, Blinkit, and Myntra.

`Java 21+` · `Spring Boot 4.1` · `GraphQL` · `gRPC` · `Kafka` · `Redis` · `PostgreSQL` · `Docker` · `Kubernetes`

---

## Table of Contents

1. [Overview](#overview)
2. [Features](#features)
3. [System Architecture](#system-architecture)
4. [High-Level Request Flow](#high-level-request-flow)
5. [Microservices & Ports](#microservices--ports)
6. [Roles & Permissions](#roles--permissions)
7. [Technology Stack](#technology-stack)
8. [Service Responsibilities](#service-responsibilities)
9. [Database Design](#database-design)
10. [Complete Order Flow](#complete-order-flow)
11. [Authentication & Authorization](#authentication--authorization)
12. [GraphQL APIs](#graphql-apis)
13. [gRPC Communication](#grpc-communication)
14. [Kafka Topics](#kafka-topics)
15. [Redis Cache](#redis-cache)
16. [Monitoring & Observability](#monitoring--observability)
17. [Docker](#docker)
18. [Kubernetes](#kubernetes)
19. [Project Structure](#project-structure)
20. [Running the Project](#running-the-project)
21. [Future Enhancements](#future-enhancements)
22. [Author](#author)

---

## Overview

This project demonstrates a realistic, production-grade microservices backend for order fulfillment — covering inventory reservation, multi-warehouse allocation, order lifecycle management, shipment tracking, and event-driven communication between services.

It is a **fulfillment-domain** system, not a full ecommerce platform — payments, refunds, and returns are intentionally out of scope (see [Future Enhancements](#future-enhancements)).

---

## Features

| Category | Capabilities |
|---|---|
| API | GraphQL APIs per service, federated behind an API Gateway |
| Communication | gRPC for internal service-to-service calls |
| Data | PostgreSQL (one database per service), Redis caching |
| Messaging | Kafka event streaming for async workflows |
| Security | Spring Security + JWT, Role-Based Access Control (RBAC) |
| Core Domain | Multi-warehouse inventory allocation, inventory reservation, shipment tracking |
| Ops | Docker & Docker Compose, Kubernetes manifests, Prometheus + Actuator, structured logging |

---

## System Architecture

```
                                   ┌────────────────────────┐
                                   │      Client Apps         │
                                   │   (Postman / GraphQL)    │
                                   └────────────┬─────────────┘
                                                │  JWT Token
                                                ▼
                                   ┌────────────────────────┐
                     ┌────────────▶│      API Gateway         │◀───────────┐
                     │             │         :8080             │            │
                     │             └────────────┬─────────────┘            │
                     │                          │                          │
              register/discover                 │                   register/discover
                     │                          ▼                          │
                     │             ┌────────────────────────┐             │
                     └─────────────│   Service Registry        │─────────────┘
                                   │   (Eureka) :8761          │
                                   └────────────────────────────┘

        ┌───────────────┬────────────────┬───────────────┬────────────────┐
        ▼               ▼                ▼               ▼                ▼
 ┌─────────────┐ ┌─────────────┐  ┌─────────────┐ ┌─────────────┐ ┌─────────────────┐
 │   Product   │ │    User     │  │    Cart     │ │    Order    │ │    Shipment      │
 │  Service    │ │  Service    │  │  Service    │ │  Service    │ │    Service       │
 │   :8081     │ │   :8083     │  │   :8085     │ │   :8086     │ │     :8087        │
 └──────┬──────┘ └──────┬──────┘  └──────┬──────┘ └──────┬──────┘ └────────┬─────────┘
        │               │                │               │  gRPC          │
        │               │                │               ├────────┬───────┘
        │               │                │               ▼        ▼
        │               │                │        ┌─────────────┐ ┌─────────────┐
        │               │                │        │  Inventory  │ │   Pricing   │
        │               │                │        │  Service    │ │   Service   │
        │               │                │        │   :8082     │ │   :8084     │
        │               │                │        └──────┬──────┘ └──────┬──────┘
        │               │                │               │               │
        ▼               ▼                ▼                ▼               ▼
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │                              PostgreSQL (per-service DB)                       │
 └─────────────────────────────────────────────────────────────────────────────┘

        Product Service ──┐
        Inventory Service ┴──▶  ⚡ Redis Cache  (catalog + inventory snapshots)

        Order Service ─────┐
        Inventory Service ─┼──▶  📬 Kafka Topics  ──▶  Notification Service (:8088) ──▶ Email / Future SMS
        Shipment Service ──┘                      └──▶  Shipment Service (order-created)
```

---

## High-Level Request Flow

```
Client
  │
  ▼
API Gateway  ──(JWT validated)──▶  GraphQL Layer
  │
  ├──▶ Product Service
  ├──▶ Cart Service
  ├──▶ Order Service
  └──▶ Shipment Service
              │
              ▼
        PostgreSQL ──▶ Redis Cache ──▶ Kafka ──▶ Notification Service
```

**Internal (synchronous) communication:**

```
Order Service ──gRPC──▶ Inventory Service   (CheckInventory)
Order Service ──gRPC──▶ Pricing Service     (GetPriceByProductId)
```

---

## Microservices & Ports

| # | Service | Port | Responsibility |
|---|---|---|---|
| 1 | Service Registry | 8761 | Eureka service discovery |
| 2 | API Gateway | 8080 | Entry point, routing, JWT authentication |
| 3 | Product Service | 8081 | Product catalog CRUD & search |
| 4 | Inventory Service | 8082 | Warehouse & inventory management, reservation |
| 5 | User Service | 8083 | Registration, login, roles |
| 6 | Pricing Service | 8084 | Price, discount & tax calculation |
| 7 | Cart Service | 8085 | Shopping cart management |
| 8 | Order Service | 8086 | Order orchestration & lifecycle |
| 9 | Shipment Service | 8087 | Shipment creation, allocation, tracking |
| 10 | Notification Service | 8088 | Kafka event consumption, notifications |

---

## Roles & Permissions

| Role | Permissions |
|---|---|
| **CUSTOMER** | Register · Login · Browse Products · Add/Update/Remove Cart Items · Place Order · Cancel Order · View Orders · Track Shipment |
| **WAREHOUSE_MANAGER** | View Inventory · Add Inventory · Update Inventory · Process Orders · Update Shipment Status |
| **ADMIN** | Manage Products · Manage Warehouses · Manage Users · Assign Warehouse Managers · View Inventory · View Orders |

**Shipment Status Lifecycle** (managed by Warehouse Manager):

```
ALLOCATED  ─▶  PACKED  ─▶  SHIPPED  ─▶  IN_TRANSIT  ─▶  DELIVERED
```

---

## Technology Stack

| Layer | Technology |
|---|---|
| Language | Java 21+ |
| Framework | Spring Boot 4.1 |
| API Layer | GraphQL |
| Internal Communication | gRPC |
| Database | PostgreSQL |
| Caching | Redis |
| Messaging | Kafka |
| Security | Spring Security + JWT |
| Service Discovery | Eureka |
| Containerization | Docker |
| Orchestration | Kubernetes |
| Monitoring | Prometheus + Spring Boot Actuator |

---

## Service Responsibilities

| Service | Key Responsibilities |
|---|---|
| Product Service | Product CRUD · Product search · GraphQL APIs · Redis cache |
| User Service | Registration · Login · JWT issuance · Role management · Warehouse manager approval |
| Inventory Service | Warehouse management · Inventory management · Reservation/release · Multi-warehouse allocation |
| Pricing Service | Product pricing · Discount calculation · Tax calculation |
| Cart Service | Add/remove product · Update quantity · View cart |
| Order Service | Create/cancel order · Reserve inventory · Calculate price · Publish Kafka events |
| Shipment Service | Shipment creation · Warehouse allocation · Tracking · Status updates |
| Notification Service | Consume Kafka events · Email notifications · Future SMS support |

---

## Database Design

Each microservice owns its own PostgreSQL database (database-per-service pattern):

| Domain | Tables |
|---|---|
| Users | `users` |
| Catalog | `products`, `pricing` |
| Inventory | `warehouses`, `inventory`, `inventory_reservations` |
| Cart | `carts`, `cart_items` |
| Orders | `orders`, `order_items` |
| Shipments | `shipments`, `shipment_tracking` |

---

## Complete Order Flow

```
 1. Customer logs in                          → JWT token generated
 2. Customer browses products                 → Product Service (Redis cache-first, PostgreSQL on miss)
 3. Customer adds items to cart                → Cart Service
 4. Customer places order                      → Order Service
 5. Order Service reserves inventory           → gRPC → Inventory Service
 6. Order Service calculates final price       → gRPC → Pricing Service
 7. Order created, status = CONFIRMED          → PostgreSQL transaction commit
 8. Order Service publishes "order-created"    → Kafka
 9. Notification Service consumes event        → Sends confirmation email
10. Shipment Service consumes event            → Creates shipment, allocates warehouse(s),
                                                   generates tracking number (e.g. TRK-ABC123)
11. Warehouse Manager updates shipment status  → ALLOCATED → PACKED → SHIPPED → IN_TRANSIT → DELIVERED
12. Customer tracks shipment                   → GraphQL query returns tracking details
13. (Optional) Customer cancels order          → Status = CANCELLED, Inventory Service releases
                                                   reservation, "order-cancelled" event published
```

---

## Authentication & Authorization

- **Spring Security** + **JWT** for stateless authentication
- **Role-Based Access Control (RBAC)** with three roles: `CUSTOMER`, `WAREHOUSE_MANAGER`, `ADMIN`
- API Gateway validates the JWT before routing requests downstream

---

## GraphQL APIs

| Service | Queries | Mutations |
|---|---|---|
| Product | `products`, `productById`, `productBySku` | `createProduct`, `updateProduct`, `deleteProduct` |
| Cart | `getCart` | `addToCart`, `updateCart`, `removeFromCart`, `clearCart` |
| Order | `orders`, `orderById`, `ordersByUser` | `placeOrder`, `cancelOrder` |
| Inventory | `warehouses`, `inventoryById`, `inventoryByProduct` | `createWarehouse`, `createInventory`, `updateInventory`, `deleteInventory` |

---

## gRPC Communication

| Caller | Callee | RPC Method |
|---|---|---|
| Order Service | Inventory Service | `CheckInventory` |
| Order Service | Pricing Service | `GetPriceByProductId` |

---

## Kafka Topics

| Topic | Producer | Consumer(s) |
|---|---|---|
| `order-created` | Order Service | Notification Service, Shipment Service |
| `order-cancelled` | Order Service | Notification Service, Inventory Service |
| `inventory-reserved` | Inventory Service | Notification Service |
| `inventory-released` | Inventory Service | Notification Service |
| `shipment-created` | Shipment Service | Notification Service |
| `shipment-delivered` | Shipment Service | Notification Service |

---

## Redis Cache

| Purpose | Key Pattern | Example |
|---|---|---|
| Product Catalog | `product::{productId}` | `product::1024` |
| Inventory Snapshot | `inventory::{productId}` | `inventory::1024` |

---

## Monitoring & Observability

| Endpoint | Purpose |
|---|---|
| `/actuator/health` | Service health check |
| `/actuator/prometheus` | Prometheus metrics scrape endpoint |

Structured logging and Prometheus metrics are enabled across all services.

---

## Docker

Each service ships its own `Dockerfile`. Start the full stack with:

```bash
docker-compose up --build
```

---

## Kubernetes

Each service includes:

| Manifest | Purpose |
|---|---|
| `Deployment` | Pod spec & replica management |
| `Service` | Internal networking |
| `ConfigMap` | Non-secret configuration |
| `Secret` | Credentials & sensitive config |

---

## Project Structure

```
Multi-Warehouse-Order-Fulfillment-System/
│
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
│
├── docker/
├── kubernetes/
├── monitoring/
├── docker-compose.yml
└── README.md
```

---

## Running the Project

**1. Clone the repository**

```bash
git clone <repository-url>
cd Multi-Warehouse-Order-Fulfillment-System
```

**2. Build**

```bash
mvn clean install
```

**3. Run with Docker**

```bash
docker-compose up --build
```

**4. Access the services**

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

## Future Enhancements

| Feature | Status |
|---|---|
| Payment Integration | Planned |
| SMS Notifications | Planned |
| Elasticsearch | Planned |
| Distributed Tracing | Planned |
| Saga Pattern | Planned |
| CI/CD Pipeline | Planned |
| OpenTelemetry | Planned |
| Grafana Dashboards | Planned |

---

## Project Highlights

- Production-inspired microservices architecture
- GraphQL API layer with per-service schemas
- gRPC for internal synchronous communication
- Kafka-based event-driven design
- Redis caching for hot-path reads
- JWT authentication with RBAC
- Multi-warehouse inventory allocation logic
- End-to-end shipment tracking
- Docker & Kubernetes-ready deployment
- Prometheus + Actuator monitoring

---

## Author

**Pranjal Tiwari**
Backend Developer

Java · Spring Boot · GraphQL · gRPC · PostgreSQL · Redis · Kafka · Docker · Kubernetes