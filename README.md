# SkillTrack AI

SkillTrack AI is a full-stack AI-powered learning platform for setting goals, tracking learning activity, generating coaching feedback, and taking quizzes. It uses Keycloak for authentication, Spring Boot for the backend, React for the frontend, PostgreSQL for persistence, and Spring AI for AI-powered features.

## Project Structure

```text
.
|-- backend/        # Spring Boot API
|-- frontend/       # React UI (Vite)
|-- docker/         # PostgreSQL and Keycloak init scripts
|-- keycloak/       # Custom Keycloak theme
|-- .github/        # GitHub Actions CI
`-- docker-compose.yml
```

## Tech Stack

### Backend

- Java 21
- Spring Boot 3.4
- Spring Security OAuth2 Resource Server
- Spring Data JPA
- Flyway
- PostgreSQL
- H2 for tests
- Spring AI for OpenAI integration
- Maven

### Frontend

- React 18
- Vite
- React Router
- Keycloak.js and `@react-keycloak/web`
- Shared authenticated fetch hook
- CSS variables for theme support

## Running Locally

### 1. Start infrastructure

```bash
docker compose up -d
```

Set the required environment variables in `.env` before starting the app:

```env
POSTGRES_DB=skilltrack
POSTGRES_USER=youruser
POSTGRES_PASSWORD=yourpass
OPENAI_API_KEY=sk-...
```

### 2. Run the backend

```bash
mvn -f backend/pom.xml spring-boot:run
```

### 3. Run the frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend runs at `http://localhost:3000`.

## Keycloak Setup

- Realm: `skilltrack`
- Backend client: `skilltrack-client`
- Frontend client: `skilltrack-frontend`
- Admin role: `admin`
- JWT claims used by the app: `preferred_username`, `email`

## Features

### Learning Workflow

- Create, edit, and delete learning goals
- View goal detail pages with linked logs, quizzes, and coaching history
- Add, filter, edit, and export learning logs
- View progress insights and activity trends

### AI Features

- Generate AI learning instructions from logs
- Generate AI summaries from learning activity
- Generate AI quizzes for a goal
- Fallback handling for malformed quiz generation responses
- Semantic grading for free-text quiz answers

### UX Features

- Responsive dashboard-style layout
- Light and dark themes
- Shared authenticated API client
- Loading, error, and empty states across major pages

## REST API Overview

- `GET /api/goals`
- `GET /api/goals/{id}`
- `POST /api/goals`
- `GET /api/logs`
- `GET /api/logs/insights`
- `GET /api/logs/export`
- `GET /api/summaries`
- `GET /api/quizzes`
- `GET /api/quizzes/goal/{goalId}`
- `GET /api/quizzes/{quizId}`
- `POST /api/instructions`
- `GET /api/instructions`
- `GET /api/admin/...`

## Testing

Backend tests run against H2 with the test profile:

```bash
mvn -f backend/pom.xml test -Dspring.profiles.active=test
```

## CI

GitHub Actions runs backend tests with Maven and builds the frontend with Vite.

## License

MIT (c) 2025 Soulaimen Choura
