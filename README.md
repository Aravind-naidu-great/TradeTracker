# TradeTracker

TradeTracker is a customer-centric trade intelligence platform built with Spring Boot.

## Tech Stack

- Java 21
- Spring Boot 3.3.5
- Spring Web
- Spring Data JPA
- Spring Security
- Spring Validation
- H2 Database
- Lombok
- Maven

## Project Structure

```text
src/main/java/com/tradetracker
├── TradeTrackerApplication.java
└── domain
    ├── AppUser.java
    ├── BusinessProfile.java
    └── UserRole.java
```

## Prerequisites

- Java 21 or newer
- Maven 3.9 or newer

## Getting Started

Clone the repository and start the Spring Boot application:

```bash
mvn spring-boot:run
```

The application starts on the default Spring Boot port:

```text
http://localhost:8080
```

## Run Tests

```bash
mvn test
```

## Build

```bash
mvn clean package
```

The packaged application will be available in the `target` directory.

