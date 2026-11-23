# Movie Reservation System: REST API

A cinema booking back end built with **Spring Boot**. It exposes a stateless, JWT-secured **REST API** (`/api/v1`) for browsing movies, viewing seat maps and reserving seats, plus an employee-only admin API for managing halls, movies, showtimes and bookings.

> **Looking for the server-rendered Thymeleaf version?** It lives on the `main` branch. This branch is API-only and uses a different database schema.

---

## Table of contents

- [Features](#features)
- [Tech stack](#tech-stack)
- [Architecture](#architecture)
- [Project structure](#project-structure)
- [Getting started](#getting-started)
- [Configuration](#configuration)
- [Authentication and security](#authentication-and-security)
- [API reference](#api-reference)
- [Error format](#error-format)
- [Business rules](#business-rules)
- [Database schema](#database-schema)
- [Testing](#testing)

---

## Features

**Customers**
- Register, log in, refresh and log out with JWT access tokens and rotating refresh tokens
- Browse movies with their upcoming showtimes (paginated)
- View a showtime's seat map (all, reserved and free seats)
- Reserve up to 10 seats in a single booking and receive a booking reference (UUID)
- List own bookings and cancel a booking before the film starts

**Employees (admin)**
- Full CRUD for halls, movies and showtimes
- Showtime overlap detection per hall (based on movie runtime)
- View all bookings and delete any booking by reference

**Platform**
- Role-based access control (`ROLE_CUSTOMER`, `ROLE_EMPLOYEE`)
- Bean Validation on every request body, with a consistent JSON error format (including 401/403 responses)
- Configurable CORS for a separate front-end
- Interactive OpenAPI / Swagger UI documentation
- Unit tests for the service layer with JaCoCo coverage reports

## Tech stack

| Area | Technology |
|---|---|
| Language | Java 25 |
| Framework | Spring Boot 4.1.x (Web MVC, Data JPA, Security, Validation, Actuator) |
| Auth | Spring Security + OAuth2 Resource Server (HS256 JWT), refresh-token rotation |
| Database | MySQL 8+ (JPA / Hibernate) |
| API docs | springdoc-openapi 3.x (Swagger UI) |
| Testing | JUnit 5, Mockito, Spring Security Test, JaCoCo |
| Build | Maven (wrapper included) |

## Architecture

A layered design:

```
Client ──► Controller ──► Service ──► DAO ──► MySQL
           (api/)        (apiservice/) (dao/)
```

- **`api/`**: REST controllers (`@RestController`) and the global exception handler
- **`apiservice/`**: business logic (DTO in, DTO out), token services, refresh-cookie helper
- **`dao/`**: data access via JPA
- **`entity/`**, **`dto/`**: persistence model and immutable request/response records
- **`security/`**: JWT resource-server configuration and JSON 401/403 handlers
- **`validation/`**: custom constraints (`@UniqueSeats`, `@ListSizeMatches`)
- **`CustomException/`**: domain exceptions mapped to HTTP statuses by the global handler

## Project structure

```
.
├── API DB + booking ref.txt          # SQL schema + seed data
└── SpringBootMovieReservationSystem/
    ├── pom.xml
    ├── mvnw / mvnw.cmd
    └── src/
        ├── main/
        │   ├── java/com/nour/SpringBootMovieReservationSystem/
        │   │   ├── Configuration/    # OpenAPI + web config
        │   │   ├── CustomException/  # Domain exceptions
        │   │   ├── api/              # REST controllers + GlobalExceptionHandler
        │   │   ├── apiservice/       # Business logic, token services, cookies
        │   │   ├── dao/              # Data access layer
        │   │   ├── dto/              # Request/response records
        │   │   ├── entity/           # JPA entities
        │   │   ├── security/         # Security config and handlers
        │   │   └── validation/       # Custom Bean Validation constraints
        │   └── resources/
        │       ├── application.properties
        │       └── static/images/    # Movie poster images
        └── test/                     # Service-layer unit tests
```

## Getting started

### Prerequisites

- **JDK 25**
- **MySQL 8+** running on `localhost:3306`
- Git (Maven is not required, the wrapper is included)

### 1. Clone the repository

```bash
git clone -b feature/rest-api https://github.com/NourMAly/Spring-Boot-Movie-Reservation-System.git
cd Spring-Boot-Movie-Reservation-System
```

### 2. Create the database and user

Run the SQL script. It creates the `movie_db_spring_boot` database, all tables, and seed data (movies, halls, showtimes, users, roles and sample reservations).

```bash
mysql -u root -p < "API DB + booking ref.txt"
```

Then create the database user the application expects (matching `application.properties`):

```sql
CREATE USER 'springstudent'@'localhost' IDENTIFIED BY 'springstudent';
GRANT ALL PRIVILEGES ON movie_db_spring_boot.* TO 'springstudent'@'localhost';
FLUSH PRIVILEGES;
```

> Change these credentials for anything other than local development.
>
> The script is written for this branch's schema. If you previously ran the `main` (Thymeleaf) branch against the same database name, the tables are not compatible, so use a fresh database.

### 3. Set the JWT secret

The application refuses to start without a secret of **at least 32 bytes**.

```bash
# macOS / Linux
export JWT_SECRET="$(openssl rand -base64 48)"

# Windows (PowerShell)
$env:JWT_SECRET = "replace-with-a-long-random-string-of-32+-chars"
```

### 4. Run the application

```bash
cd SpringBootMovieReservationSystem
./mvnw spring-boot:run          # Windows: mvnw.cmd spring-boot:run
```

The server starts on **http://localhost:8080**.

| Resource | URL |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |

### Seeded accounts

All seeded users share the password **`fun123`**.

| Username | Role |
|---|---|
| `loislane`, `maryjane`, `patrickjane` | `ROLE_EMPLOYEE` |
| `susanpark`, `gracevanpelt`, `angelamonty` | `ROLE_CUSTOMER` |

> **Heads up:** the seeded showtimes use fixed dates in November 2026, and reservations are only allowed for showtimes that haven't started yet. To try the booking flow, log in as an employee and create new showtimes via `POST /api/v1/admin/showtimes`.

## Configuration

Settings live in `src/main/resources/application.properties`.

| Property | Default | Description |
|---|---|---|
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/movie_db_spring_boot` | JDBC URL |
| `spring.datasource.username` / `password` | `springstudent` / `springstudent` | Database credentials |
| `app.jwt.secret` | `${JWT_SECRET}` | HMAC secret for signing JWTs (min. 32 bytes), **required** |
| `access.ttl.minutes` | `15` | Access token lifetime |
| `refresh.ttl.days` | `7` | Refresh token and cookie lifetime |
| `cookie.secure` | `false` | Set to `true` in production (HTTPS only) |
| `app.cors.allowed-origins` | `http://localhost:5173` | Allowed browser origin(s) for `/api/**` (comma-separated) |
| `spring.jackson.deserialization.fail-on-unknown-properties` | `true` | Reject request bodies with unknown fields |
| `spring.jpa.open-in-view` | `false` | Disable open-session-in-view |
| `spring.jpa.properties.hibernate.jdbc.time_zone` | `UTC` | Store timestamps in UTC |

## Authentication and security

The API is **stateless**: CSRF is disabled, CORS is enabled for the configured origins, and protected endpoints require a bearer token.

### Token flow

```
POST /auth/login  ──►  access token (JSON body, 15 min)
                       + refresh token (HttpOnly cookie, 7 days)

POST /auth/refresh ──► new access token + rotated refresh cookie
POST /auth/logout  ──► refresh token family revoked, cookie cleared
```

- **Access token**: HS256 JWT. The subject is the username and a `roles` claim holds roles without the `ROLE_` prefix. Send it as `Authorization: Bearer <token>`.
- **Refresh token**: 32 random bytes, delivered only as an `HttpOnly`, `SameSite=Strict` cookie scoped to `/api/v1/auth`. Only a **SHA-256 hash** is stored in the database.
- **Rotation and reuse detection**: every refresh invalidates the used token and issues a new one in the same family. If an already-used token is presented again, the entire family is revoked.
- **Live account check**: on refresh, the user is reloaded from the database, so disabling a user or changing a role takes effect at the next refresh.
- **Passwords**: stored with Spring's delegating encoder (`{bcrypt}` prefix).
- **Consistent errors**: authentication and authorization failures return the same JSON error body as the rest of the API.

### Access rules

| Endpoint pattern | Access |
|---|---|
| `/api/v1/auth/**` (except `/me`) | Public |
| `GET /api/v1/movies/**`, `GET /api/v1/showtimes/**` | Public |
| `GET /api/v1/reservations/**` | `CUSTOMER` or `EMPLOYEE` |
| `/api/v1/admin/**` | `EMPLOYEE` |
| Everything else under `/api/**` | Authenticated |
| `/swagger-ui/**`, `/v3/api-docs/**` | Public |

## API reference

Base URL: `http://localhost:8080/api/v1`. Full, interactive documentation is available in **Swagger UI**. Use the **Authorize** button and paste the access token returned by `/auth/login`.

### Auth

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/auth/register` | Public | Register a new customer (`201 Created`) |
| `POST` | `/auth/login` | Public | Returns `{ "accessToken": "..." }` and sets the refresh cookie |
| `POST` | `/auth/refresh` | Refresh cookie | Rotates the refresh token, returns a new access token |
| `POST` | `/auth/logout` | Refresh cookie | Revokes the token family and clears the cookie (`204`) |
| `GET` | `/auth/me` | Bearer | Current user's profile and roles |

### Movies and showtimes (public)

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/movies` | Paginated movies with upcoming showtimes. Query: `page`, `size` (default 10, max 50), `sort` (default `movieName,asc`) |
| `GET` | `/movies/{id}` | A single movie with its upcoming showtimes |
| `GET` | `/showtimes/{id}` | Seat map: `reservedSeats`, `allTheSeats`, `freeSeats` |

### Reservations (authenticated)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/reservations/reserve` | Reserve seats for a showtime (`201 Created`) |
| `GET` | `/reservations/myreservations` | The caller's bookings, grouped by booking reference |
| `DELETE` | `/reservations/{bookingRef}` | Cancel one of the caller's bookings (`204`) |

### Admin (`EMPLOYEE` only)

| Resource | Endpoints |
|---|---|
| Halls | `GET /admin/halls`, `GET /admin/halls/{id}`, `POST /admin/halls`, `PUT /admin/halls/{id}`, `DELETE /admin/halls/{id}` |
| Movies | `GET /admin/movies` (optional `?name=`), `GET /admin/movies/{id}`, `POST /admin/movies`, `PUT /admin/movies/{id}`, `DELETE /admin/movies/{id}` |
| Showtimes | `GET /admin/showtimes`, `GET /admin/showtimes/{id}`, `POST /admin/showtimes`, `PUT /admin/showtimes/{id}`, `DELETE /admin/showtimes/{id}` |
| Reservations | `GET /admin/reservations`, `DELETE /admin/reservations/{bookingRef}` |

### Request bodies

Unknown JSON properties are rejected with `400`.

| Endpoint | Body | Constraints |
|---|---|---|
| `POST /auth/register` | `firstname`, `lastname`, `email`, `username`, `password`, `confirmPassword` | Name/email ≤ 45 chars, username ≤ 50, password 8–72 chars, valid email |
| `POST /auth/login` | `username`, `password` | Required |
| `POST /reservations/reserve` | `showtimeId`, `seats` | 1–10 unique seats matching `^[A-Z][1-9][0-9]?$` (e.g. `A1`, `C10`) |
| `POST/PUT /admin/halls` | `numberOfSeats`, `seats` | Seat count must match list size; unique, well-formed seat labels |
| `POST/PUT /admin/movies` | `name`, `genre`, `posterFileName`, `description`, `runtimeInMinutes` | Name/genre ≤ 100, description ≤ 500, runtime 1–600, poster must be `*.png/.jpg/.jpeg` |
| `POST/PUT /admin/showtimes` | `hallNumber`, `movieID`, `showDateTime`, `price` | Date-time now or later (ISO-8601), price > 0 and ≤ 600.00 |

Movie responses expose the poster as `posterURL`, which holds the poster **file name**. Sample poster images are bundled in `src/main/resources/static/images/`.

### Example session

```bash
# 1. Log in (saves the refresh cookie in cookies.txt)
curl -c cookies.txt -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"susanpark","password":"fun123"}'
# -> {"accessToken":"eyJhbGciOi..."}

# 2. Browse movies (public)
curl "http://localhost:8080/api/v1/movies?page=0&size=5"

# 3. Check a seat map
curl http://localhost:8080/api/v1/showtimes/1

# 4. Reserve seats
curl -X POST http://localhost:8080/api/v1/reservations/reserve \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"showtimeId": 1, "seats": ["A1", "A2"]}'

# 5. Refresh the access token using the cookie
curl -b cookies.txt -c cookies.txt -X POST http://localhost:8080/api/v1/auth/refresh

# 6. Cancel the booking
curl -X DELETE http://localhost:8080/api/v1/reservations/<bookingReference> \
  -H "Authorization: Bearer $TOKEN"
```

Creating a showtime as an employee:

```bash
curl -X POST http://localhost:8080/api/v1/admin/showtimes \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"hallNumber":1,"movieID":1,"showDateTime":"2026-12-05T19:00:00","price":150.00}'
```

## Error format

All errors, including security failures, are returned as JSON:

```json
{
  "status": 409,
  "error": "Conflict",
  "message": "One or more of the selected seats are already reserved",
  "path": "/api/v1/reservations/reserve",
  "timestamp": "2026-11-19T14:05:31.123"
}
```

| Status | Typical causes |
|---|---|
| `400` | Validation failure, malformed JSON, invalid seat, password mismatch, bad path/parameter type |
| `401` | Bad credentials, missing/invalid/expired access token, invalid refresh token |
| `403` | Authenticated but lacking the required role |
| `404` | Movie, hall, showtime or reservation not found |
| `409` | Seat already taken, hall already occupied, reservation no longer cancellable, user already exists |

## Business rules

- **One booking = one showtime.** Each seat is stored as its own row; seats booked together share a `booking_ref` (UUID) and are returned as a single reservation.
- **No double-booking.** A unique constraint on `(showtime_id, seat_label)` enforces this at the database level, in addition to the service-level check.
- **Seats must exist in the hall** assigned to the showtime.
- **Past showtimes can't be booked** and are treated as not found.
- **Cancellation cut-off:** customers can't cancel once the showtime has started. Employees can delete any booking.
- **Total price** = showtime price × number of seats.
- **Hall scheduling:** a showtime is rejected (`409`) if it overlaps another showtime in the same hall, computed from start time plus movie runtime.
- **Pagination:** page size is capped at 50.

## Database schema

Defined in `API DB + booking ref.txt`.

| Table | Purpose |
|---|---|
| `movie` | Title, genre, poster file name, description, runtime |
| `hall` | Seat count and a JSON array of seat labels |
| `showtime` | Hall, movie, date, start time, price |
| `user` | Account details, hashed password, `active` flag |
| `roles` | Username → role mapping |
| `reservation` | One row per seat, linked to a user and a showtime, grouped by `booking_ref`; stores the price paid |
| `refresh_token` | Hashed refresh tokens with family ID, expiry and revoked flag |

## Testing

```bash
cd SpringBootMovieReservationSystem
./mvnw test
```

The suite (94 tests) covers the service layer (movies, halls, showtimes, reservations, users, access tokens and refresh-token rotation) using JUnit 5 and Mockito. JaCoCo generates a coverage report at:

```
target/site/jacoco/index.html
```

> The `contextLoads` test boots the full Spring context, so it needs a reachable MySQL instance with the schema above. It supplies its own JWT secret, so `JWT_SECRET` isn't required for tests.