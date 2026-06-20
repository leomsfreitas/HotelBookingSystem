# HotelSystem

Hotel booking management system with a Spring Boot backend and a React frontend.

> Project developed for the **Software Verification, Validation, and Testing (VVTS)** course and later reused in the **Software Management and Configuration (GCSW)** course, where Docker Compose containerization infrastructure was applied.

## Project structure

```
HotelSystem/
├── src/                          → backend source code (Spring Boot)
├── frontend/                     → web interface (React + Vite)
├── Dockerfile                    → backend image
├── docker-compose.yml            → service orchestration
├── .env.example                  → required environment variables
└── pom.xml
```

---

## Running with Docker (recommended)

### Prerequisites

- Docker and Docker Compose installed

### Steps

```bash
cp .env.example .env
# edit .env with the desired values
docker compose up --build
```

The system starts with three services:

| Service | Description | Port |
|---------|-----------|-------|
| `db` | PostgreSQL database | 5432 |
| `backend` | REST API (Spring Boot) | 8080 |
| `frontend` | Web interface (React + Nginx) | 80 |

Access the system at `http://localhost`.

The database already starts populated with mock data via Flyway. Use the credentials below to log in:

```
Email: admin@hotel.com
Password: admin123
```

---

## Running locally (without Docker)

### Prerequisites

- Java 21
- Maven
- Node.js 18+ and npm
- PostgreSQL running locally

### Backend

Configure the environment variables, or create a database with the default credentials from `application.properties`:

```
database: hoteldb
user: hotel
password: hotel123
```

```bash
mvn spring-boot:run
```

The server starts on port `8080`. Flyway automatically runs the `V1_mock_data.sql` migration, creating the tables and populating the database with sample data.

### Tests

```bash
mvn test
```

### Frontend

```bash
cd frontend
cp .env.example .env.local
npm install
npm run dev
```

Access at `http://localhost:5173`.

---

## Endpoints

All endpoints require JWT authentication, except for registration and login.

The token must be sent in the header:
```
Authorization: Bearer <token>
```

#### Authentication

| Method | Route | Description |
|--------|------|-----------|
| POST | `/api/v1/register` | Register a new user |
| POST | `/api/v1/authenticate` | Login — returns the JWT token |

Registration body:
```json
{
  "name": "John",
  "lastname": "Smith",
  "email": "john@email.com",
  "password": "password123"
}
```

Login body:
```json
{
  "username": "john@email.com",
  "password": "password123"
}
```

#### Guests

| Method | Route | Description |
|--------|------|-----------|
| GET | `/api/v1/guests` | List guests |
| POST | `/api/v1/guests` | Register a guest |

#### Bookings

| Method | Route | Description |
|--------|------|-----------|
| GET | `/api/v1/bookings` | List all bookings |
| GET | `/api/v1/bookings/{id}` | Find booking by ID |
| POST | `/api/v1/bookings` | Create a booking |
| PUT | `/api/v1/bookings/{id}` | Update a booking |
| PATCH | `/api/v1/bookings/{id}/cancel` | Cancel a booking |
| PATCH | `/api/v1/bookings/{id}/checkin` | Perform check-in |
| PATCH | `/api/v1/bookings/{id}/checkout` | Perform check-out |

Body to create a booking:
```json
{
  "guestId": "guest-uuid",
  "roomCategory": "STANDARD",
  "checkIn": "2026-05-20",
  "checkOut": "2026-05-25"
}
```

Available categories: `STANDARD`, `DELUXE`, `SUITE`.

### Interactive documentation (Swagger)

With the backend running, access:
```
http://localhost:8080/api/v1/api-docs
```
