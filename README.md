# SkillTrack-AI (Backend)

Spring Boot backend for **SkillTrack-AI** — a learning log tracker that generates smart summaries using OpenAI and is
secured with Keycloak. Powered by PostgreSQL.

---

## 🛠 Tech Stack

- Java 21 + Spring Boot 3.4
- Spring Security (with Keycloak)
- PostgreSQL (via Docker)
- OpenAI Integration via `openai-gpt3-java`
- H2 (for test profile)
- Maven
- GitHub Actions (CI)

---

## 🚀 Running Locally

1. **Start PostgreSQL and Keycloak via Docker Compose**

   ```bash
   docker compose -f postgres/docker-compose.yml up -d
   docker compose -f keycloak/docker-compose.yml up -d
   ```

2. **Set environment variables in `.env`**

   ```dotenv
   POSTGRES_DB=skilltrack
   POSTGRES_USER=youruser
   POSTGRES_PASSWORD=yourpass
   OPENAI_API_KEY=sk-xxxx...
   ```

3. **Run the app**

   ```bash
   ./mvnw spring-boot:run
   ```

---

## 🧪 Testing

Runs with H2 in-memory DB using the `test` profile.

```bash
./mvnw test -Dspring.profiles.active=test
```

---

## 🔐 Auth Setup (Keycloak)

- Realm: `skilltrack`
- Client: `skilltrack-frontend`
- Role: `USER`
- Uses `preferred_username` claim from JWT
- Frontend uses Bearer token for requests

---

## 🤖 AI Summarization

- Uses `com.theokanning.openai.service.OpenAiService`
- Summarizes learning logs using `gpt-3.5-turbo`
- Prompt customizable via `SummaryService.java`
- Temperature and model configurable in `.env`

```properties
OPENAI_API_KEY=sk-...
OPENAI_MODEL=gpt-3.5-turbo
OPENAI_TEMPERATURE=0.7
OPENAI_MAX_TOKENS=300
```

---

## 🔁 REST Endpoints

| Method | Path              | Description                 |
|--------|-------------------|-----------------------------|
| GET    | `/logs`           | Get authenticated user logs |
| POST   | `/logs`           | Add a new learning log      |
| DELETE | `/logs/{id}`      | Delete a learning log by ID |
| POST   | `/logs/summarize` | Generate a smart summary    |

---

## 📦 CI/CD

- GitHub Actions
- Runs tests using H2 (`test` profile) on push to `main`

---

## 📄 License

MIT © 2025 Soulaimen Choura
