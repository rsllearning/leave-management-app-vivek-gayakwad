# Leave API

A Spring Boot assignment project for managing employee leave requests. The application uses an in-memory repository and exposes REST endpoints for creating, retrieving, updating, and deleting leave records.

## Project overview

This project demonstrates a simple leave management API with:

- REST controller for leave operations
- Service layer for business validation
- DTOs for request/response payloads
- In-memory storage for assignment purposes
- Validation for required leave fields and date logic
- MockMvc tests for controller API verification

The application is built with Spring Boot 4.1.1 and Java 25.

## Build and run instructions

### Prerequisites

- Java 25
- Maven or Maven Wrapper
- Internet access to download Maven dependencies

### Build the project

```bash
./mvnw clean install
```

### Run the application

```bash
./mvnw spring-boot:run
```

The app starts on:

```text
http://localhost:8090
```

If you are using a packaged JAR:

```bash
./mvnw package
java -jar target/leave-api-0.0.1-SNAPSHOT.jar
```

## API endpoint summary

Base URL: `http://localhost:8090`

### 1) Get all leaves

```http
GET /leaves
```

Returns a list of all leave records.

### 2) Get leave by ID

```http
GET /leaves/{id}
```

Returns a single leave record for the given UUID.

### 3) Create leave

```http
POST /leaves
Content-Type: application/json
```

Example request body:

```json
{
  "employeeId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "leaveType": "PAID",
  "startDate": "2026-10-10T09:00:00",
  "endDate": "2026-10-12T17:00:00",
  "reason": "Family trip",
  "status": "PENDING"
}
```

### 4) Update leave

```http
PUT /leaves/{id}
Content-Type: application/json
```

Updates an existing leave request using the same payload structure as create.

### 5) Delete leave

```http
DELETE /leaves/{id}
```

Deletes the leave record for the supplied ID.

## Testing instructions

### Run all tests

```bash
./mvnw test
```

### Run a specific test class

```bash
./mvnw -Dtest=LeavesControllerTest test
```

### Run service-level tests

```bash
./mvnw -Dtest=LeavesServiceTest test
```

This project includes both service-layer tests and MockMvc-based controller tests to validate the API behavior.

## Notes

- This is an assignment-oriented implementation; it is not designed as a production-grade enterprise solution.
- Storage is in-memory and is intended for learning and testing purposes.
- Validation is enforced for required leave details and date ordering.
