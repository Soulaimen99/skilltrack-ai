# SkillTrack (Monorepo)

**SkillTrack** is a full-stack AI-powered learning management system that helps users define learning goals, log progress, generate summaries, get coaching advice, and take quizzes — all secured with Keycloak authentication and built with modern technologies.

---

## 🧱 Project Structure

```
.
├── backend/       # Spring Boot API
├── frontend/      # React UI (Vite)
├── docker/        # PostgreSQL & Keycloak init scripts
├── keycloak/      # Custom Keycloak theme
├── docker-compose.yml
```

---

## 🛠 Tech Stack

### Backend
- Java 21 + Spring Boot 3.4
- Spring Security (OAuth2 Resource Server)
- PostgreSQL (Docker)
- OpenAI (via `openai-gpt3-java`)
- H2 (test profile)
- Maven
- GitHub Actions (CI/CD)

### Frontend
- React 18 + Vite
- React Router
- Keycloak.js + @react-keycloak/web
- Custom hooks (auth, theme, fetch, pagination)
- CSS Variables (dark/light mode)

---

## 🚀 Running the App Locally

### 1. Start PostgreSQL + Keycloak via Docker Compose

```bash
docker compose up -d
```

> Make sure `.env` file is set with:

```env
POSTGRES_DB=skilltrack
POSTGRES_USER=youruser
POSTGRES_PASSWORD=yourpass
OPENAI_API_KEY=sk-xxxx...
```

### 2. Run the Backend

```bash
cd backend
./mvnw spring-boot:run
```

### 3. Run the Frontend

```bash
cd frontend
npm install
npm run dev
```

App runs at: [http://localhost:3000](http://localhost:3000)

---

## 🔐 Keycloak Auth Setup

- Realm: `skilltrack`
- Backend Client: `skilltrack-client`
- Frontend Client: `skilltrack-frontend`
- Roles: `user`, `admin`
- JWT claims: `preferred_username`, `email`

---

## 📁 Features

### 🧑‍🎓 User Features
- ✅ Learning goals & progress tracking
- ✅ Add/edit/delete learning logs
- ✅ GPT-powered summaries
- ✅ AI-generated instructions and coaching
- ✅ Smart insights: top tags, activity, reminders
- ✅ Quiz system: create, take, score quizzes
- ✅ Light/dark theme with local storage
- ✅ Export logs/summaries (JSON/TXT)

### 🛡 Admin Features
- 🔍 View all users
- 📚 Filter and view user logs/summaries
- 🧠 User-specific insights
- 🔒 Read-only mode for admins

---

## 🔁 REST API Endpoints

See [backend/README.md](backend/) for detailed API documentation, including:

- `/api/auth/me`
- `/api/logs`, `/api/summaries`, `/api/goals`, `/api/quizzes`, `/api/instructions`
- `/api/admin/...`

---

## 🧪 Testing (Backend)

```bash
./mvnw test -Dspring.profiles.active=test
```

> Uses H2 in-memory DB, Flyway disabled for test profile.

---

## ⚙️ Frontend Vite Proxy Config

```js
// vite.config.js
export default {
  server: {
    proxy: {
      '/api': 'http://localhost:8081'
    }
  }
}
```

---

## 📄 License

MIT © 2025 Soulaimen Choura
