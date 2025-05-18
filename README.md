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
docker compose up -d
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
- ✅ Generate AI-powered instructions for learning goals
- ✅ Create and take quizzes for learning goals
- ✅ Track quiz scores and performance

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

- Summarizes learning logs using `gpt-3.5-turbo`
- Prompt defined in `SummaryService.java`
- Rate-limiting via custom usage tracking entity
- Configurable via environment:

---

## 🔁 REST Endpoints

### 🔑 Authentication

| Method | Path           | Description                        |
|--------|----------------|------------------------------------|
| GET    | `/api/auth/me` | Get current user profile and quota |

### 🎯 Learning Goals

| Method | Path              | Description                |
|--------|-------------------|----------------------------|
| GET    | `/api/goals`      | Get current user's goals   |
| POST   | `/api/goals`      | Create a new learning goal |
| PUT    | `/api/goals/{id}` | Update a learning goal     |
| DELETE | `/api/goals/{id}` | Delete a learning goal     |

### ✏️ Learning Logs

| Method | Path                 | Description             |
|--------|----------------------|-------------------------|
| GET    | `/api/logs`          | Get current user's logs |
| POST   | `/api/logs`          | Add a new learning log  |
| PUT    | `/api/logs/{id}`     | Update a learning log   |
| DELETE | `/api/logs/{id}`     | Delete a learning log   |
| GET    | `/api/logs/insights` | Get user insights       |
| GET    | `/api/logs/export`   | Export logs (json/txt)  |

### 📚 Instructions

| Method | Path                | Description                     |
|--------|---------------------|---------------------------------|
| GET    | `/api/instructions` | Get current user's instructions |
| POST   | `/api/instructions` | Generate a new instruction      |

### 📊 Summaries

| Method | Path                    | Description                  |
|--------|-------------------------|------------------------------|
| POST   | `/api/summaries`        | Generate a summary from logs |
| GET    | `/api/summaries/export` | Export summaries (json/txt)  |

### 📝 Quizzes

| Method | Path                                                  | Description                     |
|--------|-------------------------------------------------------|---------------------------------|
| GET    | `/api/quizzes`                                        | Get current user's quizzes      |
| GET    | `/api/quizzes/{quizId}`                               | Get a specific quiz             |
| GET    | `/api/quizzes/goal/{goalId}`                          | Get quizzes for a learning goal |
| POST   | `/api/quizzes/goal/{goalId}`                          | Create a new quiz for a goal    |
| POST   | `/api/quizzes/{quizId}/questions`                     | Add a question to a quiz        |
| POST   | `/api/quizzes/{quizId}/questions/{questionId}/answer` | Submit an answer                |
| PUT    | `/api/quizzes/{quizId}/complete`                      | Complete a quiz and get results |
| DELETE | `/api/quizzes/{quizId}`                               | Delete a quiz                   |

### 🧑‍💼 Admin Endpoints (Requires `admin` role)

| Method | Path                              | Description                       |
|--------|-----------------------------------|-----------------------------------|
| GET    | `/api/admin/users`                | List all users                    |
| GET    | `/api/admin/users/{id}/logs`      | Get logs for a specific user      |
| GET    | `/api/admin/users/{id}/summaries` | Get summaries for a specific user |

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
