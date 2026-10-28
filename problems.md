# TradeFlux – Setup Problems & Solutions

This document records the issues encountered while setting up, building, and testing the TradeFlux microservices project, along with the solutions and verification steps.

## 1. Auth Service Port Conflict

### Problem
The Auth Service was configured to use host port `8081`, but this port was already occupied by the `festpass-frontend` container.

This caused a port-binding conflict when starting the Auth Service.

### Solution
The Auth Service port mapping in `docker-compose.yaml` was changed from:

```yaml
ports:
  - "8081:8081"
```

to:

```yaml
ports:
  - "8086:8081"
```

The host port was changed to `8086`, while the internal container port remained `8081`.

### Commands Used

```bash
docker compose up -d --force-recreate auth-service
```

Verify the running container:

```bash
docker ps
```

Check Auth Service logs:

```bash
docker compose logs auth-service
```

### Verification
The Auth Service started successfully on container port `8081` and became accessible through host port `8086`.

The registration API was tested successfully:

```http
POST http://localhost:8086/api/v1/auth/register
```

The API returned **HTTP 201 Created**.

---

## 2. Project Folder Restructuring

### Problem
The project was initially located inside:

```text
Quant/
└── TradeFlux/
```

The requirement was to move the project contents directly into the `Quant` directory while preserving the existing Git repository.

During the move, the `.github` directory initially returned a permission-denied error.

### Solution
The project contents were moved into the parent directory.

The `.github` directory was moved separately after the initial attempt:

```bash
mv -v TradeFlux/.github .
```

The command completed successfully.

### Verification

```bash
ls -la
```

The project files were present in the `Quant` root directory, including:

```text
.github/
.git/
.gitignore
docker-compose.yaml
docs/
infrastructure/
k8s/
observability/
performance-tests/
pom.xml
README.md
services/
shared/
```

The `TradeFlux` directory was empty afterward and could be removed using:

```bash
rmdir TradeFlux
```

The existing `.git` directory was retained in `Quant`, preserving the repository.

---

## 3. Maven Build Verification

### Problem
Before running the microservices, the project needed to be compiled and packaged to verify that the Java code and Maven configuration were valid.

### Solution
The project was built using Java 21 and Maven.

```bash
mvn clean package -DskipTests
```

### Verification
The Maven build completed successfully:

```text
BUILD SUCCESS
```

This verified that the project could be compiled and packaged with tests skipped.

---

## 4. JWT Authentication and API Gateway Verification

### Problem
After starting the services, it was necessary to verify that authentication worked and that protected APIs could be accessed through the API Gateway.

### Solution
The Auth Service was accessed through host port `8086`, while protected APIs were accessed through the API Gateway on port `7080`.

Registration endpoint:

```http
POST http://localhost:8086/api/v1/auth/register
```

Protected user endpoint:

```http
GET http://localhost:7080/api/v1/users/me
```

The access token obtained during authentication was used to authorize the protected API request.

### Verification
- Registration returned **HTTP 201 Created**.
- The protected user endpoint returned **HTTP 200 OK**.
- JWT-based authentication and API Gateway routing were verified.

**Security Note:** Actual access tokens, refresh tokens, passwords, and JWT secrets are intentionally excluded from this document.

---

## 5. Wallet and Order Execution Verification

### Problem
The wallet, order, and portfolio services needed to be tested together to verify the basic trading workflow.

### Solution
The following workflow was executed:

1. Created a wallet.
2. Added funds to the wallet.
3. Placed a market BUY order for RELIANCE.
4. Executed the order.
5. Checked the order status.
6. Verified the wallet balance and portfolio holding.

### Tested Workflow

#### Create Wallet

```http
POST /api/v1/wallet/create
```

The wallet was created successfully with wallet ID `1`.

#### Add Funds

```http
POST /api/v1/wallet/add-funds
```

Request body:

```json
{
  "amount": 1000
}
```

The wallet balance became `1000`.

#### Place Order

```http
POST /api/v1/orders
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

The order was created successfully and initially had a pending status.

#### Execute Order

```http
POST /api/v1/orders/1/execute
```

The order execution returned **HTTP 200 OK** with status:

```text
EXECUTED
```

#### Verify Order Status

```http
GET /api/v1/orders/1/status
```

The order status was verified as `EXECUTED`.

### Final Verification

After execution:

| Component | Verified Result |
|---|---|
| Wallet balance | 900 |
| Reserved balance | 0 |
| Order status | EXECUTED |
| Portfolio holding | RELIANCE |
| Holding quantity | 1 |
| Average buy price | 100 |
| Total invested amount | 100 |

This verified the basic wallet-to-order-to-portfolio workflow.

---

## 6. Docker Compose Service Verification

### Problem
Since TradeFlux uses multiple microservices and supporting infrastructure, it was necessary to verify that the services started correctly and could register with the service discovery server.

### Verification Commands

Check running containers:

```bash
docker ps
```

Check logs for a specific service:

```bash
docker compose logs <service-name>
```

Follow logs continuously:

```bash
docker compose logs -f <service-name>
```

Check the Auth Service logs:

```bash
docker compose logs auth-service
```

### Verification
The Auth Service logs showed successful startup and registration with Eureka.

The application was also tested through the API Gateway, confirming that the tested authentication, wallet, order, and portfolio endpoints were reachable.

---

## Summary

The following setup and integration checks were completed successfully:

- Resolved the Auth Service host port conflict by changing the host port to `8086`.
- Moved the project contents into the repository root while retaining Git history.
- Successfully built the project using Maven and Java 21.
- Verified registration and JWT-protected API access.
- Verified wallet funding and order execution.
- Confirmed that the executed order was reflected in the portfolio.

These issues and verification steps document the practical setup and debugging experience while working with the TradeFlux microservices project.
