# SkillTrack-AI (Backend)

Spring Boot backend for **SkillTrack-AI** — a secure learning log tracker that generates AI-based summaries using OpenAI
and authenticates via Keycloak. Built with PostgreSQL and ready for local development, testing, and deployment.

---

## 🛠 Tech Stack

- Java 21 + Spring Boot 3.4
- Spring Security + OAuth2 Resource Server (Keycloak)
- PostgreSQL (via Docker Compose)
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

```env
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

## 🔐 Auth Setup (Keycloak)

- Realm: `skilltrack`
- Client: `skilltrack-client`
- Roles: `user`, `admin`
- JWT claims used: `preferred_username`, `email`
- Frontend authenticates via Bearer token

---

## 📁 Features

### 🧑‍🎓 User Features

- ✅ Add, edit, and delete learning logs
- ✅ Generate summaries from logs (GPT-3.5 Turbo)
- ✅ Rate-limited summaries with daily quota
- ✅ Persistent summaries and logs in PostgreSQL
- ✅ Filter logs by date range and tag
- ✅ Export logs/summaries as JSON or TXT
- ✅ View user insights (log count, top tags, activity)
- ✅ `/me` endpoint returns current user profile and quota

### 🛡 Admin Features

- ✅ View all users
- ✅ View logs and summaries of any user
- ✅ Filter by date and tag
- ✅ Read-only access (no edit/delete)
- ✅ Detect inactive users for reminders

### 🧠 AI Tagging (Upcoming)

- Automatically extract tags from learning logs using OpenAI
- Merge AI tags with user-defined ones before saving

---

## 🤖 AI Summarization

- Uses `com.theokanning.openai.service.OpenAiService`
- Summarizes learning logs using `gpt-3.5-turbo`
- Prompt defined in `SummaryService.java`
- Rate-limiting via custom usage tracking entity
- Configurable via environment:

```properties
OPENAI_API_KEY=sk-...
OPENAI_MODEL=gpt-3.5-turbo
OPENAI_TEMPERATURE=0.7
OPENAI_MAX_TOKENS=300
```

---

## 🔁 REST Endpoints

### ✏️ Learning Logs

| Method | Path              | Description                  |
|--------|-------------------|------------------------------|
| GET    | `/logs`           | Get current user's logs      |
| POST   | `/logs`           | Add a new learning log       |
| PUT    | `/logs/{id}`      | Update a learning log        |
| DELETE | `/logs/{id}`      | Delete a learning log        |
| POST   | `/logs/summarize` | Generate a summary from logs |
| GET    | `/logs/export`    | Export logs (json/txt)       |

### 🧑‍💼 Admin Endpoints (Requires `admin` role)

| Method | Path                          | Description                       |
|--------|-------------------------------|-----------------------------------|
| GET    | `/admin/users`                | List all users                    |
| GET    | `/admin/users/{id}/logs`      | Get logs for a specific user      |
| GET    | `/admin/users/{id}/summaries` | Get summaries for a specific user |
| GET    | `/admin/users/{id}/insights`  | User activity insights            |

---

## 🧪 Testing

Runs with H2 in-memory DB using the `test` profile. Flyway is disabled in test mode.

```bash
./mvnw test -Dspring.profiles.active=test
```

---

## 📦 CI/CD

- GitHub Actions workflow
- Runs tests via H2 with profile `test` on push to `main`

---

## 📄 License

MIT © 2025 Soulaimen Choura
