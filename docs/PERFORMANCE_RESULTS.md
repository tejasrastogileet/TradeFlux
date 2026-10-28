# 🚀 TradeFlux Performance Test Results

## Overview

This document tracks the load testing and stress testing results of TradeFlux microservices.

### Test Environment

* Spring Boot Microservices
* PostgreSQL
* Kafka
* Eureka Service Discovery
* Spring Cloud Gateway
* Docker Desktop
* k6 Load Testing
* Local Development Environment

---

# 1. Auth Service

## Login API

### Load Test

| Metric           | Result       |
| ---------------- | ------------ |
| Concurrent Users | 250          |
| Duration         | 30s          |
| Requests         | ~7,500       |
| Throughput       | ~240 req/sec |
| Failure Rate     | 0%           |
| P95 Latency      | < 500ms      |

### Observations

* BCrypt password hashing is the primary latency contributor.
* Refresh token persistence adds additional database overhead.
* Authentication remained stable under sustained load.

---

# 2. User Service

## Search Users API

Endpoint:

```http
GET /api/v1/users/search
```

### Load Test

| Metric           | Result       |
| ---------------- | ------------ |
| Concurrent Users | 250          |
| Duration         | 30s          |
| Requests         | 7,500        |
| Throughput       | ~239 req/sec |
| Failure Rate     | 0%           |
| P95 Latency      | 47.95ms      |

### Optimizations

* Pagination pushed to PostgreSQL.
* Filtering performed at database level.
* Avoided in-memory filtering.

### Observations

* Read-heavy APIs scaled extremely well.
* Very low latency under concurrent traffic.

---

# 3. Order Service

## Place Order API

Endpoint:

```http
POST /api/v1/orders
```

### Load Test

| Metric           | Result       |
| ---------------- | ------------ |
| Concurrent Users | 250          |
| Duration         | 30s          |
| Requests         | 7,500        |
| Throughput       | ~232 req/sec |
| Failure Rate     | 0%           |
| P95 Latency      | 32.29ms      |

### Stress Test

Configuration:

* 250 VUs
* sleep(1) removed

| Metric          | Result         |
| --------------- | -------------- |
| Requests        | 64,670         |
| Throughput      | ~2,038 req/sec |
| Failure Rate    | 0%             |
| Average Latency | 121ms          |
| P95 Latency     | 190ms          |

### Observations

* Kafka event publishing remained stable.
* Database writes handled sustained load.
* No request failures under stress conditions.

---

# 4. Portfolio Service

## Get Holdings API

Endpoint:

```http
GET /api/v1/portfolio/my
```

---

### 50 Concurrent Users

| Metric          | Result      |
| --------------- | ----------- |
| Throughput      | ~46 req/sec |
| Failure Rate    | 0%          |
| Average Latency | 24.43ms     |
| P95 Latency     | 25.46ms     |

---

### 100 Concurrent Users

| Metric          | Result      |
| --------------- | ----------- |
| Throughput      | ~95 req/sec |
| Failure Rate    | 0%          |
| Average Latency | 6.34ms      |
| P95 Latency     | 12.58ms     |

---

### 250 Concurrent Users

| Metric          | Result       |
| --------------- | ------------ |
| Throughput      | ~235 req/sec |
| Failure Rate    | 0%           |
| Average Latency | 25.76ms      |
| P95 Latency     | 47.82ms      |

---

### Stress Test

Configuration:

* 250 VUs
* sleep(1) removed

| Metric          | Result         |
| --------------- | -------------- |
| Requests        | 76,354         |
| Throughput      | ~2,418 req/sec |
| Failure Rate    | 0%             |
| Average Latency | 102ms          |
| P95 Latency     | 163ms          |

### Observations

* Fastest service in TradeFlux so far.
* Read-heavy workload scales exceptionally well.
* Indexed queries and pagination provide consistent performance.
* Zero failures under sustained stress.

---

# Service Performance Comparison

| Service           | Peak Throughput |
| ----------------- | --------------- |
| Auth Service      | ~240 req/sec    |
| User Service      | ~239 req/sec    |
| Order Service     | ~2,038 req/sec  |
| Portfolio Service | ~2,418 req/sec  |

---

# Engineering Learnings

### Authentication Bottlenecks

* BCrypt hashing impacts login throughput.
* Token persistence introduces additional database writes.

### Read-Heavy API Performance

* Pagination at database level significantly improves scalability.
* Indexed queries provide predictable latency under load.

### Event-Driven Architecture

* Kafka producers remained stable during load testing.
* Kafka consumers processed events reliably.
* End-to-end event flow validated:

```text
Order Service
      ↓
Kafka
      ↓
Portfolio Service
      ↓
PostgreSQL
```

### Scalability Validation Strategy

Every service is:

```text
Build
 ↓
Functional Testing
 ↓
Load Testing
 ↓
Stress Testing
 ↓
Move To Next Domain
```

before proceeding to the next microservice.

---

## Wallet Service

The Wallet Service is responsible for managing user balances, wallet creation, fund deposits, withdrawals, and transaction history.

### Endpoints Tested

* `GET /wallet/my`
* `GET /wallet/transactions`

---

## GET /wallet/my

### Concurrency Tests

| Users | Throughput   | P95     | Failures |
| ----- | ------------ | ------- | -------- |
| 50    | ~46 req/sec  | 46.89ms | 0%       |
| 100   | ~93 req/sec  | 13.78ms | 0%       |
| 250   | ~239 req/sec | 18.64ms | 0%       |

### Stress Test

Configuration:

* 250 Virtual Users
* 30 Seconds
* sleep(1) removed

| Metric             | Result         |
| ------------------ | -------------- |
| Requests Processed | 92,130         |
| Throughput         | ~2,888 req/sec |
| Failure Rate       | 0%             |
| Average Latency    | 85.89ms        |
| Median Latency     | 79.69ms        |
| P95 Latency        | 128.44ms       |
| Max Latency        | 1.19s          |

### Observations

* Indexed lookup using `user_id`.
* JWT authentication remained stable under load.
* API Gateway routing showed no degradation.
* PostgreSQL handled concurrent reads efficiently.
* Zero failures during stress testing.
* Fastest service in TradeFlux so far.

---

## GET /wallet/transactions

### Concurrency Tests

| Users | Throughput   | P95     | Failures |
| ----- | ------------ | ------- | -------- |
| 50    | ~46 req/sec  | 18.27ms | 0%       |
| 100   | ~93 req/sec  | 15.32ms | 0%       |
| 250   | ~231 req/sec | 15.74ms | 0%       |

### Stress Test

Configuration:

* 250 Virtual Users
* 30 Seconds
* sleep(1) removed

| Metric             | Result         |
| ------------------ | -------------- |
| Requests Processed | 84,147         |
| Throughput         | ~2,628 req/sec |
| Failure Rate       | 0%             |
| Average Latency    | 94.42ms        |
| Median Latency     | 82.16ms        |
| P95 Latency        | 170.68ms       |
| Max Latency        | 1.29s          |

### Observations

* Pagination remained stable under heavy load.
* Transaction retrieval scales efficiently with concurrent users.
* No authentication or gateway bottlenecks observed.
* Zero failures during stress testing.
* Consistent latency across 50, 100, and 250 concurrent users.
* Suitable for high-frequency transaction history retrieval workloads.

---

## Wallet Service Summary

### Functional Validation

*  Create Wallet
*  Get Wallet
*  Add Funds
*  Withdraw Funds
*  Transaction History

### Performance Validation

| Endpoint                 | Peak Throughput | P95 Latency | Failure Rate |
| ------------------------ | --------------- | ----------- | ------------ |
| GET /wallet/my           | ~2,888 req/sec  | 128.44ms    | 0%           |
| GET /wallet/transactions | ~2,628 req/sec  | 170.68ms    | 0%           |

### Key Takeaways

* Wallet Service successfully handled thousands of requests per second.
* Zero failures across all load and stress tests.
* Indexed database access provided consistently low latency.
* JWT authentication and API Gateway remained stable under sustained load.
* Wallet Service is production-ready from a performance perspective and forms the foundation for Order ↔ Wallet integration workflows.


---

Last Updated: June 2026
Project: TradeFlux
