# Claims Microservices

> An **event-driven insurance claims platform** built as Spring Boot microservices — a claims
> service exposing **REST + GraphQL** APIs that publishes domain events, and a notification service
> that consumes them. Pluggable event transport: **in-process** (zero infra) or **Apache Kafka**.

[![CI](https://github.com/akinifecode/claims-microservices/actions/workflows/ci.yml/badge.svg)](https://github.com/akinifecode/claims-microservices/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/java-17-orange)
![Spring Boot](https://img.shields.io/badge/spring%20boot-3.2-brightgreen)
![License](https://img.shields.io/badge/license-MIT-green)

---

## What this demonstrates

A production-shaped **Java / Spring Boot microservices** system with an **event-driven
architecture**. It showcases:

- **Microservices** with a shared `common` module (DTOs + domain events) — `claims-service` and
  `notification-service`.
- **REST** APIs (Spring MVC) **and a GraphQL** read API over the same domain.
- **Event-driven** design: claim lifecycle changes publish domain events behind an
  `EventPublisher` abstraction, delivered **in-process** (Spring events) or over **Kafka** by
  profile.
- Clean layering, constructor injection, **Bean Validation**, and a `@RestControllerAdvice`
  exception handler returning structured errors.
- **JPA/H2** persistence, JUnit 5 / Mockito / MockMvc tests, Docker, docker-compose, and CI.

---

## Architecture

```
                 REST  ┌────────────────────┐   ClaimSubmitted / Approved / Rejected
   client ───────────► │   claims-service   │ ──────────────┐
                 GraphQL│  (REST + GraphQL)  │               │   EventPublisher
                        │   JPA / H2         │               ▼
                        └────────────────────┘     ┌───────────────────────┐
                                                    │   in-process (default) │
                                                    │         OR             │
                                                    │   Kafka  (kafka profile)│
                                                    └───────────┬───────────┘
                                                                ▼
                                                   ┌────────────────────────┐
                                                   │  notification-service   │
                                                   │  records notifications  │
                                                   └────────────────────────┘
```

### Modules

| Module | Responsibility |
|--------|----------------|
| `common` | Shared domain enums (`ClaimType`, `ClaimStatus`), immutable event records, `ApiError` |
| `claims-service` | REST + GraphQL API, claim lifecycle, event publishing (port 8080) |
| `notification-service` | Consumes claim events, records notifications (port 8081) |

### Event transport — two profiles

| Profile | Transport | Use it for |
|---------|-----------|-----------|
| *default* (`local`) | Spring `ApplicationEventPublisher` (in-process) | Zero-infra local dev and the test suite |
| `kafka` | Apache Kafka (`spring-kafka`) | The real cross-service flow, run via docker-compose |

The same `EventPublisher` interface and the same event records back both — only the wiring changes,
selected by `@Profile`. (In-process events are delivered within a single JVM; the genuine
cross-service `claims-service → notification-service` flow runs on the `kafka` profile.)

---

## Tech stack

**Java 17** · **Spring Boot 3.2** (Web, Data JPA, Validation, GraphQL) · **Spring for Apache
Kafka** · **H2** · **Maven** (multi-module) · **JUnit 5 / Mockito / MockMvc** · **Docker** ·
**GitHub Actions**

---

## Build & run

### Prerequisites
JDK 17+ and Maven (or use the bundled Docker flow).

```bash
mvn verify                # build + run all tests across modules
```

### Run locally (in-process events, no Kafka needed)

```bash
# Terminal 1 — claims service (REST + GraphQL) on :8080
mvn -pl claims-service spring-boot:run

# Terminal 2 — notification service on :8081
mvn -pl notification-service spring-boot:run
```

### Run the full event-driven stack with Kafka

```bash
docker compose up --build     # zookeeper + kafka + both services on the kafka profile
```

---

## REST API (claims-service)

| Method | Path | Description | Codes |
|--------|------|-------------|-------|
| `POST` | `/api/claims` | Submit a claim | 201, 400 |
| `GET`  | `/api/claims` | List claims (`?status=SUBMITTED\|APPROVED\|REJECTED`) | 200 |
| `GET`  | `/api/claims/{id}` | Get a claim | 200, 404 |
| `POST` | `/api/claims/{id}/approve` | Approve a submitted claim | 200, 404, 409 |
| `POST` | `/api/claims/{id}/reject` | Reject a submitted claim `{reason}` | 200, 404, 409 |

```bash
# Submit
curl -s -XPOST localhost:8080/api/claims -H 'content-type: application/json' -d '{
  "policyNumber": "POL-44218",
  "claimantName": "Jane Doe",
  "type": "AUTO",
  "description": "Rear-end collision on I-85",
  "amount": 4200.00
}' | jq

# Approve
curl -s -XPOST localhost:8080/api/claims/1/approve | jq

# Reject
curl -s -XPOST localhost:8080/api/claims/1/reject -H 'content-type: application/json' \
  -d '{"reason": "Missing police report"}' | jq
```

Validation and domain errors return a structured `ApiError`:

```json
{ "timestamp": "2026-01-01T12:00:00Z", "status": 409, "error": "Conflict",
  "message": "Cannot approve claim CLM-1A2B3C4D in status APPROVED", "path": "/api/claims/1/approve" }
```

### GraphQL API

`POST /graphql` (GraphiQL UI at `/graphiql`):

```graphql
query {
  claims {
    id
    claimNumber
    claimantName
    type
    amount
    status
  }
  claim(id: 1) {
    claimNumber
    status
    rejectionReason
  }
}
```

## Notification service

```bash
curl -s localhost:8081/api/notifications | jq
# [{ "type": "CLAIM_SUBMITTED", "claimNumber": "CLM-1A2B3C4D",
#    "message": "Claim CLM-1A2B3C4D submitted by Jane Doe", "receivedAt": "..." }]
```

---

## Project structure

```
claims-microservices/
├── pom.xml                       # parent (multi-module reactor)
├── common/                       # shared DTOs, events, ApiError
├── claims-service/
│   └── src/main/java/com/akinluyi/claims/
│       ├── domain/ repository/   # Claim entity + Spring Data JPA
│       ├── dto/                  # validated request/response records
│       ├── service/              # ClaimService (lifecycle + events)
│       ├── events/               # EventPublisher: Local + Kafka impls
│       ├── web/                  # REST controller + exception handler
│       └── graphql/              # GraphQL query resolver
├── notification-service/         # event consumer (Local + Kafka listeners)
├── docker-compose.yml            # zookeeper + kafka + both services
└── .github/workflows/ci.yml
```

---

## Testing

```bash
mvn verify
```

- **claims-service** — `ClaimService` unit tests (Mockito), `ClaimController` slice tests
  (`@WebMvcTest` + MockMvc), and a full context-load test.
- **notification-service** — an end-to-end in-process event test (publish → listener → store) plus
  a store unit test.

All tests run on the default profile with **no Kafka required**. CI runs `mvn -B verify` on JDK 17.

---

## Future work

- Schema migrations (Flyway) and a real database (PostgreSQL).
- A Testcontainers-based integration test for the Kafka path.
- An API gateway (Spring Cloud Gateway) and service discovery.
- Idempotent consumers + a dead-letter topic; outbox pattern for reliable publishing.

---


