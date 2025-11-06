# 🏦 Mini Wallet API (Backend)

A Spring Boot REST API for a virtual wallet application that enables users to manage their finances, transfer money, and track transactions.

## 🚀 Features

- JWT-based auth & roles
- Wallet management (create, balance, update)
- Transactions: deposit, transfer, history
- PIN security for transfers
- PostgreSQL + JPA persistence
- Clean RESTful endpoints & validation

## 🛠 Tech Stack

**Java 17** • **Spring Boot 3** • **Spring Security** • **JPA/Hibernate** • **PostgreSQL** • **JWT** • **Maven**

## ⚙️ Setup

**1. Clone repo**

```bash
git clone <repo-url> && cd mini-wallet-backend
```

**2. Create DB**

```sql
CREATE DATABASE mini_wallet;
```

**3. Configure** `src/main/resources/application.properties`

```properties
spring.application.name=mini-wallet-backend

# Server
server.port=8080
server.servlet.context-path=/api

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/mini_wallet
spring.datasource.username=your postgres username
spring.datasource.password=your postgres password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.show-sql=true

# JWT
app.jwt.secret=mySecretKeyForJWTGenerationInMiniWalletApplication2024
app.jwt.expiration=86400000
```

**4. Run**

```bash
mvn spring-boot:run
```

→ API: `http://localhost:8080/api`

## 🧩 Key Endpoints

### Auth

- `POST /api/auth/register`
- `POST /api/auth/login`

### Wallet

- `GET /api/wallet`
- `POST /api/wallet/deposit`
- `POST /api/wallet/transfer`
- `POST /api/wallet/verify-pin`
- `POST /api/wallet/update-pin`

### Transactions

- `GET /api/transactions`
- `GET /api/transactions/{id}`

## 🧱 Structure

```
src/main/java/com/miniwallet/
├── controller/
├── service/
├── model/
├── dto/
├── repository/
└── security/
```

## 🔐 Security

- JWT auth
- BCrypt password/PIN hashing
- Role-based access
- CORS setup

## 🧪 Tests

```bash
mvn test
```

## 📜 License

MIT License © 2025
