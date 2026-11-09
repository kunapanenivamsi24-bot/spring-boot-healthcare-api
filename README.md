# Spring Boot Healthcare API

A REST API for managing patients and appointments, built with Java 17 and Spring Boot 3.

> **Portfolio demo project.** This project was built by Sai Sasi Vamsi Kunapaneni to
> showcase a Java / Spring Boot / PostgreSQL / Docker stack. It is a demo only — it is
> **not** Providence Health code, it is not affiliated with any employer, and it contains
> no real patient data. All data is fictional sample data.
>
> Author: Sai Sasi Vamsi Kunapaneni — [LinkedIn](https://www.linkedin.com/in/vamsi-kunapaneni-6ab48831a/)

## Features

- Patient CRUD with Bean Validation and paginated listing
- Appointment scheduling, listing by patient, and cancellation
- JWT authentication (JJWT 0.12) with a stateless Spring Security filter chain
- Consistent JSON error responses via `@RestControllerAdvice` (timestamp, status, message)
- H2 in-memory database by default; PostgreSQL via the `postgres` Spring profile
- Unit tests with JUnit 5 + Mockito
- Multi-stage Dockerfile and Docker Compose (app + PostgreSQL 16)
- GitHub Actions CI (JDK 17, `mvn -B test`)

## Tech Stack

| Layer | Technology |
| --- | --- |
| Language | Java 17 |
| Framework | Spring Boot 3.2.5 (Web, Data JPA, Security, Validation) |
| Auth | JJWT 0.12.6, BCrypt |
| Databases | H2 (dev/default), PostgreSQL 16 (prod profile) |
| Build | Maven (single module) |
| Tests | JUnit 5, Mockito |
| Containers | Docker, Docker Compose |
| CI | GitHub Actions |

## Prerequisites

- Java 17+
- Maven 3.9+ (or use the Maven wrapper if added)
- Docker and Docker Compose (only for the containerized run)

## Run locally

```bash
mvn spring-boot:run
```

The API starts on http://localhost:8080.

The H2 console is available at http://localhost:8080/h2-console
(JDBC URL `jdbc:h2:mem:healthdb`, user `sa`, empty password).

## Run with Docker Compose (PostgreSQL)

```bash
docker compose up --build
```

This starts the app with the `postgres` profile against a PostgreSQL 16 container
(the app waits for the database healthcheck before starting). Stop with
`docker compose down` (add `-v` to remove the database volume).

## Authentication

All `/api/**` endpoints except `/api/auth/**` require a Bearer JWT.

Demo credentials (demo only — never use hard-coded credentials in a real system):

- Username: `demo`
- Password: `demo123`

```bash
# 1. Log in and capture the token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"demo123"}' | python3 -c "import sys,json; print(json.load(sys.stdin)['token'])")

# 2. Create a patient
curl -X POST http://localhost:8080/api/patients \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Jane",
    "lastName": "Doe",
    "dateOfBirth": "1990-05-14",
    "gender": "Female",
    "email": "jane.doe@example.com",
    "phone": "555-0100"
  }'

# 3. List patients (paged: ?page=0&size=20&sort=lastName)
curl http://localhost:8080/api/patients \
  -H "Authorization: Bearer $TOKEN"

# 4. Create an appointment for patient 1
curl -X POST http://localhost:8080/api/appointments \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": 1,
    "doctorName": "Dr. Smith",
    "appointmentTime": "2030-01-15T10:30:00",
    "reason": "Annual check-up"
  }'

# 5. List appointments for patient 1
curl http://localhost:8080/api/appointments/patient/1 \
  -H "Authorization: Bearer $TOKEN"

# 6. Cancel appointment 1
curl -X PUT http://localhost:8080/api/appointments/1/cancel \
  -H "Authorization: Bearer $TOKEN"
```

## API Endpoints

| Method | Endpoint | Description | Auth |
| --- | --- | --- | --- |
| POST | `/api/auth/login` | Log in, returns a JWT | Public |
| POST | `/api/patients` | Create a patient | Bearer JWT |
| GET | `/api/patients?page=0&size=20` | List patients (paged) | Bearer JWT |
| GET | `/api/patients/{id}` | Get a patient by id | Bearer JWT |
| PUT | `/api/patients/{id}` | Update a patient | Bearer JWT |
| DELETE | `/api/patients/{id}` | Delete a patient | Bearer JWT |
| POST | `/api/appointments` | Create an appointment for a patient | Bearer JWT |
| GET | `/api/appointments/patient/{patientId}` | List appointments for a patient | Bearer JWT |
| GET | `/api/appointments/{id}` | Get an appointment by id | Bearer JWT |
| PUT | `/api/appointments/{id}/cancel` | Cancel an appointment | Bearer JWT |

Error responses are JSON with `timestamp`, `status`, `message`, and (for validation
failures) a field-level `errors` map. Missing resources return `404`.

## Run tests

```bash
mvn test
```

Unit tests cover the patient service (create, missing-patient 404, paged list, update,
delete) and the JWT service (generate/validate/extract username, tampered token rejection).

## Configuration

| Property | Default | Description |
| --- | --- | --- |
| `server.port` | `8080` | HTTP port |
| `spring.datasource.url` | `jdbc:h2:mem:healthdb` | Default (H2) datasource |
| `spring.jpa.hibernate.ddl-auto` | `create-drop` | Default schema mode (H2) |
| `spring.h2.console.enabled` | `true` | H2 console (default profile only) |
| `app.jwt.secret` | demo-only value in `application.yml` | JWT signing secret — **demo only; replace with a strong secret supplied via environment in any real deployment** |
| `app.jwt.expiration` | `3600000` (1 hour) | JWT lifetime in milliseconds |
| `SPRING_PROFILES_ACTIVE` | — | Set to `postgres` to use PostgreSQL |
| `DB_URL` | `jdbc:postgresql://localhost:5432/healthdb` | PostgreSQL JDBC URL (`postgres` profile) |
| `DB_USERNAME` | `postgres` | PostgreSQL username (`postgres` profile) |
| `DB_PASSWORD` | `postgres` | PostgreSQL password (`postgres` profile) |

## License

MIT — see [LICENSE](LICENSE).
