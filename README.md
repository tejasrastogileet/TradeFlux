# 🚀 TradeFlux

TradeFlux is a cloud-native distributed trading infrastructure platform built to simulate real-world stock trading workflows using microservices, event-driven architecture, observability, and container orchestration.

The project focuses on designing production-grade backend systems by combining synchronous and asynchronous communication patterns, service discovery, distributed tracing, monitoring, and Docker-based deployment.

---

# 🎯 Project Goals

TradeFlux was built to explore:

* Distributed Systems Design
* Event-Driven Architecture
* Cloud-Native Development
* Microservices Communication
* Observability & Monitoring
* Containerization & Orchestration
* Production-Grade Backend Engineering

---

# 🏗️ System Architecture

```text
                         +------------------+
                         |    API Gateway   |
                         +---------+--------+
                                   |
      ---------------------------------------------------------
      |                |                 |                    |
      ▼                ▼                 ▼                    ▼

+-------------+ +-------------+ +-------------+ +-------------+
| Auth Service| | User Service| |Order Service| |WalletService|
+-------------+ +-------------+ +------+------| +------+------+
                                      |               |
                                      |               |
                                      ▼               ▼

                               +-------------+  +-------------+
                               |    Kafka    |  | PostgreSQL  |
                               +------+------+  +------+------+
                                      |
                                      |
                                      ▼

                              +---------------+
                              |PortfolioService|
                              +---------------+

```

---

# ⚡ Core Business Workflow

## Order Placement Flow

```text
Client
   │
   ▼

API Gateway
   │
   ▼

Order Service
   │
   ├── Reserve Funds (Wallet Service)
   │
   └── Publish OrderPlacedEvent
            │
            ▼
          Kafka
```

## Order Execution Flow

```text
Order Service
      │
      ▼

OrderExecutedEvent
      │
      ▼

Kafka
      │
      ├── Wallet Service
      │       └── Consume Funds
      │
      └── Portfolio Service
              └── Update Holdings
```

---

# 🧩 Microservices

## API Gateway

Responsibilities:

* Single Entry Point
* JWT Authentication
* Request Routing
* Route Protection

---

## Auth Service

Responsibilities:

* User Registration
* User Authentication
* JWT Generation
* JWT Validation

---

## User Service

Responsibilities:

* User Profile Management
* User Registration Event Handling

---

## Wallet Service

Responsibilities:

* Wallet Creation
* Fund Management
* Reserve Funds
* Release Funds
* Consume Funds

---

## Order Service

Responsibilities:

* Buy Order Placement
* Order Lifecycle Management
* Kafka Event Publishing
* Wallet Integration

---

## Portfolio Service

Responsibilities:

* Portfolio Holdings
* Position Tracking
* Event-Based Updates

---

# 📡 Communication Patterns

## Synchronous Communication

```text
OpenFeign
```

Used for:

```text
Order Service → Wallet Service
```

---

## Asynchronous Communication

```text
Apache Kafka
```

Events:

* UserRegisteredEvent
* OrderPlacedEvent
* OrderCancelledEvent
* OrderExecutedEvent

---

# 🔍 Observability

## Distributed Tracing

Implemented using:

* Micrometer Tracing
* Brave
* Zipkin

Trace Flow:

```text
Gateway
   ↓
Order Service
   ↓
Wallet Service
   ↓
Portfolio Service
```

---

## Metrics

Implemented using:

* Spring Boot Actuator
* Micrometer
* Prometheus

Available Metrics:

* JVM Metrics
* Memory Usage
* CPU Usage
* HTTP Requests
* Response Time
* Application Health

---

## Monitoring Dashboard

Implemented using:

* Grafana

Dashboards:

* Service Health
* Request Throughput
* JVM Monitoring
* Application Metrics

---

# 🐳 Containerization

All services are fully containerized using Docker.

Containerized Components:

* API Gateway
* Auth Service
* User Service
* Wallet Service
* Portfolio Service
* Order Service
* Discovery Server
* PostgreSQL
* Kafka
* Zipkin

---

# 🚀 Docker Compose Deployment

Start the complete platform:

```bash
docker compose up -d
```

Stop the platform:

```bash
docker compose down
```

Check running containers:

```bash
docker ps
```

---

# 🛠️ Technology Stack

### Backend

* Java 21
* Spring Boot 3
* Spring Cloud

### Microservices

* Eureka Discovery Server
* Spring Cloud Gateway
* OpenFeign

### Messaging

* Apache Kafka

### Database

* PostgreSQL

### Observability

* Prometheus
* Grafana
* Zipkin
* Micrometer
* Spring Actuator

### Containerization

* Docker
* Docker Compose

---

# 📈 Current Features

* JWT Authentication
* Service Discovery
* API Gateway Routing
* Event-Driven Communication
* Portfolio Management
* Wallet Management
* Order Lifecycle Management
* Distributed Tracing
* Metrics Collection
* Dockerized Infrastructure
* Docker Compose Deployment

---

# 🔜 Next Roadmap

* Kubernetes Deployment
* ConfigMaps & Secrets
* Ingress Controller
* Horizontal Pod Autoscaler
* Minikube Deployment
* AWS EKS Deployment
* CI/CD Pipeline
* GitHub Actions
* Centralized Logging

---

# ⭐ Key Learnings

* Designing Event-Driven Systems
* Distributed Transaction Handling
* Service-to-Service Communication
* Cloud-Native Architecture
* Container Orchestration
* Production Observability
* Microservice Deployment Strategies
