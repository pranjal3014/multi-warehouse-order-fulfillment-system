# End-to-End Testing Report

Date: 2026-07-24

## Result

**RUNTIME VERIFICATION PENDING.** The previously identified Order, Payment, Shipment, Kafka, and Notification contract blockers have been fixed. Live integration tests remain unexecuted because the repository has no Docker Compose/runtime topology, no configured PostgreSQL databases or Kafka broker, and the Maven wrappers terminate before Maven starts.

## Latest E2E execution attempt

Date: 2026-07-24

| Check | Result | Evidence |
| --- | --- | --- |
| Source whitespace validation | Passed | `git diff --check` completed successfully. |
| Running service endpoints | Blocked | No listener exists on the HTTP, gRPC, Kafka, or Eureka ports. |
| Maven compile | Blocked | Every Maven wrapper exits with `Cannot start maven from wrapper` before invoking Maven. |
| WSL Maven fallback | Blocked | No WSL distribution is installed. |
| Docker runtime | Blocked | Docker daemon is not running; `docker_engine` pipe is unavailable. |
| PostgreSQL and Kafka provisioning | Blocked | No Compose or SQL provisioning artifacts are present. |
| GraphQL, gRPC, Kafka, and database assertions | Not run | They require the unavailable runtime dependencies above. |

The Postman collection remains ready to execute after the required services are started.

## Passed static checks

- GraphQL schemas exist for Product, Inventory, Pricing, Cart, Order, Payment, and Shipment.
- Cart, Inventory, and Pricing expose the gRPC methods that Order invokes.
- Shipment supports allocation, tracking numbers, and legal status transitions.
- Notification Service has Kafka consumers, a notification table, JSON event DTOs, and topic declarations.
- `git diff --check` passed for the Notification implementation.

## Resolved integration blockers

| ID | Root cause | Affected flow | Evidence |
| --- | --- | --- | --- |
| E2E-01 | Order now accepts and forwards `paymentMethod`. | Payment GraphQL input is satisfied. | `PlaceOrderRequest` and the Order schema include `PaymentMethod`. |
| E2E-02 | Order now uses `PAID`. | Successful payments confirm orders. | Order and Payment share `PENDING`, `PAID`, `FAILED`, and `REFUNDED`. |
| E2E-03 | Order now passes the stored `paymentId`. | Refund GraphQL input is satisfied. | Internal Order refund request matches Payment’s `RefundRequest`. |
| E2E-04 | Resolver now returns a Boolean. | Cancellation response matches the schema. | `cancelOrder` returns whether the service completed. |
| E2E-06 | Order creates shipments after confirmation. | One shipment is created for each allocated warehouse. | `ShipmentClient` is called after Order confirmation. |
| E2E-07 | Order, Payment, and Shipment now publish events. | Notification can consume business events. | Producers publish JSON to the three configured topics. |
| E2E-08 | Order gRPC moved to `9094`. | Cart retains `9093` without collision. | Service gRPC ports are distinct. |

## Warnings

- Inventory remains check-only by explicit instruction; reservation and release behavior was not redesigned.
- The `orderByStatus` field exists in the Order GraphQL schema but no resolver/service method implements it.
- Order invokes Payment through an HTTP GraphQL client, not gRPC; this is acceptable only if the documented contract permits it.
- There is no API to query Notification records, so persistence can only be verified directly in its database.
- Notification Kafka consumers require every shared-topic JSON payload to contain `eventType`; no producer currently establishes that contract.
- No Docker Compose, Kubernetes manifests, database migrations, Kafka provisioning, or runnable environment variables are present.

## Required environment setup before live E2E execution

1. Provide PostgreSQL databases and `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` values for every service.
2. Start Kafka and set `KAFKA_BOOTSTRAP_SERVERS` when it is not running at `localhost:9092`.
3. Start the services in dependency order, then run the Postman collection.
4. Keep the existing inventory check-only design unless reservation requirements are explicitly reopened.

## Test coverage catalogue

The companion Postman collection covers product creation/query, warehouse and inventory creation/query, pricing creation/query, cart add/query, order placement and missing-order handling, shipment creation/status tracking, and invalid tracking numbers. The order-placement request is intentionally marked as an expected failure until E2E-01 and E2E-02 are fixed.

### Expected database changes after blockers are fixed

1. Product row is created.
2. Warehouse and inventory rows are created; reservation increments `reservedQty` and reduces available stock according to the final reservation design.
3. Pricing row is created.
4. Cart item is created, then removed when order confirmation succeeds.
5. Order is saved as `CONFIRMED` with payment and transaction IDs.
6. Payment is saved as `PAID`, then `REFUNDED` on cancellation.
7. Shipment is saved as `ALLOCATED`, then progresses to `DELIVERED`.
8. Notification records are saved with `SENT` status for each consumed Kafka event.

### Expected Kafka payload contract

```json
{
  "eventType": "SHIPMENT_SHIPPED",
  "orderId": 101,
  "userId": 501,
  "trackingNumber": "SHP-..."
}
```

Expected notification log:

```text
Notification sent. Event Type : SHIPMENT_SHIPPED, Order Id : 101, User Id : 501, Message : Order #101 has been shipped.
```
