# Formula 1 Betting System

A high-performance, resilient Spring Boot application built with Hexagonal Architecture. This system integrates with the OpenF1 API to provide real-time race sessions, allowing users to place bets and simulating race outcomes with automatic prize distribution.

## 🏗️ Current Architecture: Hexagonal (Ports & Adapters)

The project follows the Hexagonal Architecture pattern to ensure that the core business logic is completely decoupled from external technologies (Databases, APIs, Frameworks).

### Layer Breakdown

- **Domain Layer**: Contains the core business logic (Models, Exceptions, Enums). It has zero dependencies on Spring or JPA.

- **Application Layer**:
  - **Input Ports**: Interfaces defining what the system can do (PlaceBetUseCase).
  - **Output Ports**: Interfaces defining what the system needs from the outside (F1DataProvider).
  - **Services**: Implementations of Use Cases that orchestrate business rules.

- **Adapter Layer**:
  - **Inbound (Web)**: REST Controllers that adapt HTTP requests to Use Case calls.
  - **Outbound (Persistence/External)**: Implementation of technical details like JPA Repositories and WebClient for external API calls.

## 🔌 External API Decoupling (Future-Proofing)

The system is designed to support multiple F1 Data Providers (OpenF1, Ergast, Sportradar, etc.) without modifying the core business logic. This is achieved through the **Dependency Inversion Principle**:

1. **The SPI (Service Provider Interface)**: Inside `application.port.out`, we defined the `F1DataProvider` interface. This is the "Contract" the application expects.
2. **The Adapter**: The `OpenF1Adapter` is merely one implementation of this contract.
3. **Seamless Switching**: If a new API provider is added in the future:
   - We create a new class (e.g., `SportradarAdapter`) in the `adapter.out.external` package.
   - We implement the `F1DataProvider` interface.
   - The `BettingService` remains **completely untouched**, ensuring zero regression risks to the betting logic.

This architecture ensures that the "Source of Truth" for race data is swappable, making the system resilient to third-party API deprecations or contract changes.


## 🛠️ Setup & Installation

### 1. Folder Structure

If you have received this project as a ZIP, ensure the directory structure matches the following (Maven standard):

```
src/main/java/com/sporty/f1betting/
├── adapter/
│   ├── in/web/            <-- REST Controllers & Request DTOs
│   └── out/
│       ├── persistence/    <-- JPA Entities & Repositories
│       └── external/       <-- WebClient / API Implementations
├── application/
│   ├── port/
│   │   ├── in/             <-- Use Case Interfaces
│   │   └── out/            <-- SPI / Provider Interfaces
│   └── service/            <-- Core Orchestration
└── domain/
    ├── model/              <-- Pure Java POJOs / Records
    └── exception/          <-- Business-specific exceptions
```

### 2. Prerequisites

- Java 17+
- Maven 3.6+

### 3. Running the Application

1. Extract the ZIP to your workspace.
2. Open a terminal in the root folder.(Go the folder where mvnw is present it is present in the root folder)
3. Build and Run:

```bash
mvnw clean spring-boot:run
```

### Access H2 Console

- **URL**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **User**: `sa`
- **Password**: (leave empty)

## 🧪 Testing the Implementation

### Endpoints

| Action | Method | URL |
|--------|--------|-----|
| Fetch Sessions | GET | `/api/v1/events?country=Belgium` |
| Place Bet | POST | `/api/v1/bets` |
| Simulate Win | POST | `/api/v1/simulate-outcome` |

### Sample Bet Payload

```json
{
    "userId": 1,
    "sessionId": 9140,
    "driverId": 1,
    "amount": 25.00,
    "odds": 3
}
```

### Sample Simulate Win Payload

```json
{
    "sessionId": 9158,
    "winnerId": 1
}
```

## 🔄 Testing the 5-Point Workflow

To verify the complete lifecycle of a bet and outcome, follow these steps using Postman or cURL.

### Step 1: Check Initial Balance (Requirement Check)

1. Open the H2 Console at http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:testdb`)
2. Query: `SELECT * FROM users WHERE id = 1;`
3. **Expectation**: Balance should be 100.00.

### Step 2: Place a Bet

Place a bet on Driver 1 for Session 9140.

- **URL**: `POST /api/v1/bets`
- **Payload**:

```json
{
    "userId": 1,
    "sessionId": 9140,
    "driverId": 1,
    "amount": 20.00,
    "odds": 3
}
```

**Verification**: 
- Check H2. `users` balance should now be 80.00. 
- `bets` table should show 1 record with status `PENDING`.

### Step 3: Process Outcome (Covers all 5 Requirements)

Simulate that Driver 1 won Session 9140.

- **URL**: `POST /api/v1/simulate-outcome`
- **Payload**:

```json
{
    "sessionId": 9140,
    "winnerId": 1
}
```

### Step 4: Final Validation

Verify the prize calculation (20 \times 3 = 60) and balance update (80 + 60 = 140).

- **Query**: `SELECT * FROM users;` → **Result**: 140.00
- **Query**: `SELECT * FROM bets;` → **Result**: Status is now `WON`.

---

### 1. Test Categories
* **Unit Tests (Service Layer)**: `BettingServiceTest` validates the 5-point requirement check for race outcomes. It ensures that prizes are calculated correctly ($Amount \times Odds$) and that user balances are updated atomically.
* **Adapter Tests (External Layer)**: `OpenF1AdapterTest` verifies the simulation logic. It ensures that the system correctly filters race data by year, country, and type before mapping it to the Domain model.

### 2. Modern Java Standards
* **Java 17 Records**: The test suite utilizes the native accessor syntax for Records (e.g., `session.sessionId()`), ensuring compatibility with modern, immutable data structures.
* **Mocking Strategy**: We use Mockito to decouple the service from the database and API. This allows for "Shift-Left" testing where business rules are verified without requiring an active internet connection or a live database.

### 3. How to Run the Tests
You can execute the entire test suite using the Maven Wrapper:

```bash
mvnw clean test


**Built with clean architecture principles for maintainability, testability, and scalability.**

## 💰 Financial Integrity & Currency Handling

As per the requirement to handle bets specifically in **EUR**, the system implements the following professional standards:

* **Fixed-Point Arithmetic**: The system strictly uses `BigDecimal` for all balance deductions and prize calculations. This prevents the "Floating Point Inaccuracy" bug inherent in `Double` or `Float` types, ensuring user balances are accurate to the decimal.
* **Atomic Reconciliation**: The balance deduction (Stake) and balance addition (Prize) are executed within **ACID-compliant transactions**. If a prize calculation fails, the database state remains unchanged, preventing "ghost money" or balance leakage.
* **Scalability**: While currently optimized for EUR, the Domain Layer is designed to support multi-currency expansion by abstracting the currency as a value object in future iterations.


## 🚀 Production Roadmap: Scalability & Resilience

While the current version uses an in-memory H2 database for demonstration, a production-grade system would evolve with the following strategy:

### 1. Persistence Tier (Oracle/PostgreSQL)

**Transition**: Replace H2 with PostgreSQL or Oracle for ACID compliance and partitioning.

**Concurrency Control**: Implement Optimistic Locking (`@Version`) on the User entity to prevent race conditions when thousands of bets are processed simultaneously.

### 2. Event-Driven Outcome Processing (Apache Kafka)

In a high-load environment, processing race outcomes synchronously is a bottleneck.

**The Workflow**: A `RaceResultService` would consume a `RaceFinished` event from a Kafka Topic.

**Scaling**: Multiple instances of the `BettingService` can consume these events in parallel to distribute the payout calculation load.

### 3. Resilience: DLQ & Retry Strategy

To ensure that "no user loses money due to a timeout," we would implement a multi-stage retry strategy:

- **Retry Policy**: Use Spring Retries with Exponential Backoff for transient database failures.
- **Dead Letter Queue (DLQ)**: If a payout fails after 3 retries (e.g., a specific user account is locked), the message is moved to a `payout-dlq`.
- **Alerting**: Monitor the DLQ via Prometheus/Grafana. Operations can manually reprocess or investigate these failed financial transactions without losing data.

### 4. API Resilience

**Circuit Breaker**: Use Resilience4j on the `OpenF1Adapter`. If the F1 API is down, the system should serve cached race data or a "Service Temporarily Unavailable" message instead of crashing.
