# SkillTrack-AI (Backend)

Spring Boot backend for SkillTrack-AI — a learning log and summarization tool secured with Keycloak and powered by
PostgreSQL.

## 🛠 Tech Stack

- Java 21 + Spring Boot
- Spring Security with Keycloak
- PostgreSQL (via Docker)
- H2 (for tests only)
- Maven
- GitHub Actions for CI

## 🚀 Running Locally

1. **Start PostgreSQL and Keycloak via Docker Compose**
2. Set environment variables via `.env` or system:
   ```
   POSTGRES_DB=skilltrack
   POSTGRES_USER=youruser
   POSTGRES_PASSWORD=yourpass
   ```
3. **Run the app**
   ```bash
   ./mvnw spring-boot:run
   ```

## 🧪 Testing

Runs with H2 in-memory DB:

```bash
./mvnw test -Dspring.profiles.active=test
```

## 🔐 Auth Setup

- Realm: `skilltrack`
- Client: `skilltrack-frontend`
- Role: `USER`
- Uses `preferred_username` from JWT

## 🔁 Endpoints

| Method | Path         | Description             |
|--------|--------------|-------------------------|
| GET    | `/logs`      | Get user logs           |
| POST   | `/logs`      | Add a new log           |
| DELETE | `/logs/{id}` | Delete a log            |
| POST   | `/summaries` | Generate + save summary |

## 📦 CI/CD

GitHub Actions runs tests using the `test` profile (H2 DB) on each push to `main`.

## 📄 License

MIT © 2025 Soulaimen Choura
