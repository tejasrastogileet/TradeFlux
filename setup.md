# TradeFlux – Setup, Demo & Transaction Testing Guide

## Purpose

This document is a quick reference for setting up, running, and demonstrating the TradeFlux microservices project during an interview or technical evaluation.

It contains:

- Project setup instructions
- Docker and Maven commands
- Service startup and verification
- Authentication and JWT testing
- Wallet creation and funding
- Complete order execution and portfolio verification
- Previously encountered setup problems and their fixes
- A quick GPT prompt for running the demo

> **Important:** This guide documents the workflow that was previously tested. Before executing commands, verify the current `docker-compose.yaml`, service names, port mappings, and API routes. Do not assume that a command succeeded without checking its output.

---

# 1. Project Structure

The project repository is located in the `Quant` directory.

```text
Quant/
├── .github/
├── .git/
├── .gitignore
├── docker-compose.yaml
├── docs/
├── infrastructure/
├── k8s/
├── observability/
├── performance-tests/
├── pom.xml
├── README.md
├── services/
└── shared/
```

The project uses a microservices architecture with components such as:

- Auth Service
- User Service
- Wallet Service
- Portfolio Service
- Order Service
- API Gateway
- Eureka Service Discovery
- PostgreSQL
- Kafka
- Zipkin

---

# 2. Prerequisites

Check that the following tools are installed:

| Tool | Purpose |
|---|---|
| Java 21 | Run Java/Spring Boot services |
| Maven | Build the project |
| Docker Desktop | Run containers |
| Docker Compose | Start the microservices stack |
| Git | Repository management |
| Postman or curl | Test REST APIs |

Check versions:

```bash
java -version
mvn -version
docker --version
docker compose version
git --version
```

Make sure Docker Desktop is running before executing Docker commands.

---

# 3. Open the Project

Using Git Bash:

```bash
cd ~/OneDrive/Desktop/Quant
```

Verify the current directory:

```bash
pwd
```

Check the project files:

```bash
ls -la
```

Check the Git repository:

```bash
git status
```

---

# 4. Build the Project

Run the Maven build:

```bash
mvn clean package -DskipTests
```

### Expected Result

```text
BUILD SUCCESS
```

The build was previously verified using Java 21 and Maven.

If the build fails:

```bash
mvn clean package -DskipTests
```

Read the first actual error in the Maven output before attempting a fix.

---

# 5. Start the Microservices

Start the Docker Compose stack:

```bash
docker compose up -d
```

Check the running containers:

```bash
docker ps
```

Check the status of all Compose services:

```bash
docker compose ps
```

If a service is not running, inspect its logs:

```bash
docker compose logs <service-name>
```

Follow the logs continuously:

```bash
docker compose logs -f <service-name>
```

For the Auth Service:

```bash
docker compose logs auth-service
```

The Auth Service was previously verified to start successfully and register with Eureka.

---

# 6. Important Port Configuration

The Auth Service originally had a host port conflict.

Host port `8081` was already occupied by the `festpass-frontend` container.

The Auth Service port mapping was changed to:

```yaml
ports:
  - "8086:8081"
```

This means:

| Location | Port |
|---|---|
| Host machine | 8086 |
| Auth Service container | 8081 |

After changing the mapping, recreate the Auth Service:

```bash
docker compose up -d --force-recreate auth-service
```

Verify:

```bash
docker ps
```

Check the logs:

```bash
docker compose logs auth-service
```

Previously verified API ports:

| Component | Host Port |
|---|---|
| Auth Service | 8086 |
| API Gateway | 7080 |

Other service ports must be verified from the current Docker Compose configuration.

---

# 7. Authentication Testing

## Register a User

Previously tested endpoint:

```http
POST http://localhost:8086/api/v1/auth/register
```

Use the request body expected by the current Auth Service DTO.

Example structure:

```json
{
  "username": "demo_user",
  "email": "demo@example.com",
  "password": "Use-A-Temporary-Password"
}
```

> The exact registration DTO must be checked in the current source code. Do not assume that these example field names match the application.

Previously, registration returned:

```text
HTTP 201 Created
```

## Login

Inspect the Auth Service controller to identify the exact login endpoint and request DTO.

Use the registered user's credentials.

Save the access token from the login response for protected API requests.

Do not store real tokens or passwords in this document.

## Test a Protected API

Previously tested endpoint:

```http
GET http://localhost:7080/api/v1/users/me
```

Add the access token in the request header:

```http
Authorization: Bearer <ACCESS_TOKEN>
```

Previously, the protected endpoint returned:

```text
HTTP 200 OK
```

This verified JWT authentication and API Gateway routing.

---

# 8. Complete Wallet and Trading Transaction

This is the main workflow to demonstrate during the interview.

## Step 1: Create a Wallet

Previously tested endpoint:

```http
POST http://localhost:7080/api/v1/wallet/create
```

The wallet was created successfully.

Previously observed wallet ID:

```text
1
```

> Confirm the current API Gateway routing and wallet controller mappings before running the request.

## Step 2: Add Funds

Previously tested endpoint:

```http
POST http://localhost:7080/api/v1/wallet/add-funds
```

Request body:

```json
{
  "amount": 1000
}
```

Previously observed result:

```text
Wallet balance: 1000
```

The wallet transaction history showed a successful CREDIT transaction with transaction type `ADD_FUNDS`.

## Step 3: Place a BUY Order

Previously tested endpoint:

```http
POST http://localhost:7080/api/v1/orders
```

Request body:

```json
{
  "symbol": "RELIANCE",
  "assetType": "STOCK",
  "orderType": "MARKET",
  "orderSide": "BUY",
  "quantity": 1,
  "price": 100
}
```

Previously, the order was created with a pending status.

The order ID observed during the test was:

```text
1
```

> Order IDs may change when the database is reset or a new order is created. Always use the ID returned by the current API response.

## Step 4: Execute the Order

Previously tested endpoint:

```http
POST http://localhost:7080/api/v1/orders/1/execute
```

Previously observed response:

```text
HTTP 200 OK
```

Order status:

```text
EXECUTED
```

## Step 5: Verify Order Status

Previously tested endpoint:

```http
GET http://localhost:7080/api/v1/orders/1/status
```

Expected result for the previously tested order:

```text
EXECUTED
```

## Step 6: Verify Wallet Balance

After the order was executed, the wallet balance was:

```text
900
```

The reserved balance was:

```text
0
```

Calculation:

```text
Initial Balance = 1000
Order Value     = 1 × 100 = 100
Final Balance   = 1000 - 100 = 900
```

## Step 7: Verify Portfolio

Previously tested endpoints:

```http
GET http://localhost:7080/api/v1/portfolio/my
```

```http
GET http://localhost:7080/api/v1/portfolio/summary
```

Previously observed holding:

| Field | Value |
|---|---|
| Symbol | RELIANCE |
| Quantity | 1 |
| Average Buy Price | 100 |
| Total Invested Amount | 100 |

This verified the basic wallet-to-order-to-portfolio workflow.

---

# 9. Previously Encountered Problems

## Problem 1: Auth Service Port Conflict

**Issue:** Host port `8081` was occupied by `festpass-frontend`.

**Fix:** Changed the Auth Service host port mapping to `8086:8081`.

**Command:**

```bash
docker compose up -d --force-recreate auth-service
```

**Verification:**

```bash
docker ps
docker compose logs auth-service
```

## Problem 2: Permission Error While Moving `.github`

**Issue:** The initial attempt to move `.github` returned a permission-denied error.

**Successful command:**

```bash
mv -v TradeFlux/.github .
```

The directory was successfully moved into the repository root.

The exact underlying cause of the initial permission error was not confirmed.

## Problem 3: Project Folder Restructuring

**Issue:** Project contents were moved from `Quant/TradeFlux` into `Quant`.

**Verification:**

```bash
ls -la
git status
```

The existing `.git` directory was retained in `Quant`.

The `TradeFlux` directory was empty after the move and could be removed:

```bash
rmdir TradeFlux
```

## Problem 4: Maven Build

**Verification command:**

```bash
mvn clean package -DskipTests
```

**Previously observed result:**

```text
BUILD SUCCESS
```

## Problem 5: Authentication and Protected APIs

Registration and protected API access were verified.

- Registration returned HTTP 201.
- Protected user endpoint returned HTTP 200.
- JWT-based authorization was verified.

## Problem 6: Wallet and Order Integration

The following workflow was successfully verified:

```text
Create Wallet
     ↓
Add Funds: 1000
     ↓
Place RELIANCE BUY Order
     ↓
Execute Order: 100
     ↓
Wallet Balance: 900
     ↓
Portfolio Holding: RELIANCE × 1
```

---

# 10. Quick Troubleshooting Commands

Check running containers:

```bash
docker ps
```

Check all Compose services:

```bash
docker compose ps
```

View logs:

```bash
docker compose logs <service-name>
```

Follow logs:

```bash
docker compose logs -f <service-name>
```

Restart a service:

```bash
docker compose restart <service-name>
```

Recreate a service:

```bash
docker compose up -d --force-recreate <service-name>
```

Rebuild and start services:

```bash
docker compose up -d --build
```

Stop the stack:

```bash
docker compose down
```

> Do not use `docker compose down -v` during an interview demo unless you intentionally want to remove the Compose volumes and understand the data-loss consequences.

---

# 11. Five-Minute Interview Demo Checklist

Use this order when the interviewer asks to run and test the project.

### Minute 1 – Project and Build

```bash
cd ~/OneDrive/Desktop/Quant
git status
mvn clean package -DskipTests
```

### Minute 2 – Start Services

```bash
docker compose up -d
docker compose ps
```

### Minute 3 – Authentication

- Register a demo user.
- Login and obtain an access token.
- Call the protected user API through the Gateway.

### Minute 4 – Wallet and Order

- Create a wallet.
- Add funds.
- Place a BUY order.
- Execute the order.

### Minute 5 – Verify Results

- Check order status.
- Check wallet balance.
- Check portfolio holding.
- Explain the transaction flow.

---

# 12. GPT Prompt for Interview Preparation

When preparing for the interview, paste this file into GPT and use the following prompt:

```text
You are my TradeFlux project interview assistant.

Read the complete SETUP.md and PROBLEMS.md files.

I have approximately five minutes to demonstrate the project to an interviewer.

Help me run and test the project in the correct order.

Requirements:

1. First inspect the current project configuration and identify the actual Docker Compose service names, ports, API routes, and request DTOs.
2. Do not invent API endpoints, request fields, credentials, or service names.
3. Give me exact commands that I can copy and paste into Git Bash.
4. Start with checking Docker, Java, Maven, the project directory, and the current container status.
5. Build the project if required.
6. Start the services and verify their health.
7. Test registration and login.
8. Obtain a JWT access token and call a protected API through the API Gateway.
9. Create a wallet and add funds.
10. Place a BUY order, execute it, and verify the order status.
11. Verify the final wallet balance and portfolio holding.
12. Explain the expected output after every important command.
13. If a command fails, inspect the actual error and help me fix it before continuing.
14. Remember the previously encountered Auth Service port conflict:
    host port 8081 was occupied, so the Auth Service was mapped to 8086:8081.
15. Do not print or expose real passwords, JWT secrets, access tokens, or refresh tokens.
16. Keep the demo concise and interview-focused.

Guide me one step at a time. Wait for my terminal output before moving to the next step.

At the end, give me a short explanation of the complete TradeFlux transaction flow that I can speak during the interview.
```

---

# 13. Interview Explanation: Complete Transaction Flow

Use this explanation when the interviewer asks how a transaction works in TradeFlux.

```text
First, the user registers and authenticates through the Auth Service.
After successful login, the user receives a JWT access token.

The client sends requests through the API Gateway.
The Gateway routes the request to the appropriate microservice.

The user creates a wallet and adds funds.
When the user places a BUY order, the Order Service creates the order.

The order is then executed through the order execution endpoint.
After successful execution, the wallet balance is updated and the portfolio reflects the purchased asset.

Finally, the user can check the order status, wallet balance, and portfolio holding through the respective APIs.
```

### Previously Verified Transaction

```text
Wallet Balance Before Order: 1000
Asset: RELIANCE
Order Side: BUY
Quantity: 1
Price: 100
Order Value: 100

Order Status: EXECUTED
Wallet Balance After Order: 900
Reserved Balance: 0
Portfolio Quantity: 1
Average Buy Price: 100
Total Invested Amount: 100
```

This is the previously tested TradeFlux transaction workflow.