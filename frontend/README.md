# SkillTrack-UI (Frontend)

React frontend for SkillTrack — a clean, responsive learning management system with logs, goals, quizzes, and AI-powered features including summaries and coaching advice. Secured with Keycloak authentication.

---

## 🛠 Tech Stack

- React 18 + Vite
- Keycloak.js + @react-keycloak/web
- React Router for navigation
- Custom hooks for data fetching, pagination, and theme management
- Custom dark/light theme (CSS variables)
- Responsive CSS with no external UI framework

---

## 🚀 Running Locally

```bash
npm install
npm run dev
```

App runs at: [http://localhost:3000](http://localhost:3000)

---

## 🔐 Auth Setup

- Uses Keycloak for login, register, and logout
- Access token injected into all API requests
- Requires:
  - Realm: `skilltrack`
  - Client: `skilltrack-frontend`
- Supports `user` and `admin` roles

---

## 📁 Features

### 🧑‍🎓 User Features
- ✅ Add, edit, and delete learning logs
- ✅ Create and manage learning goals
- ✅ Generate summaries from logs (OpenAI-powered)
- ✅ Get AI coaching advice for learning goals
- ✅ Create and take quizzes for learning goals
- ✅ Track quiz scores and performance
- ✅ Rate-limited summaries with usage tracking
- ✅ Filter logs by date range or tag
- ✅ View your summary history
- ✅ Switch between dark/light theme
- ✅ Export logs/summaries as `.json` or `.txt`
- ✅ View personal learning insights:
  - Most used tags
  - Time since last log
  - Logs this week/month
- ✅ Smart reminder if no logs added recently

### 🛡 Admin Panel
- 🔍 View all users
- 📚 Filter and browse user logs by date/tag
- 🧠 View user-specific summaries
- 🔒 Read-only mode (no edit/delete)
- 📊 Export user data for analysis

### 🧩 Components
- **Core Components**: Header, ErrorMessage, LoadingSpinner, Pagination
- **Learning Management**: GoalsPage, LogsPage, ProgressPage
- **AI Features**: InstructionsPage, SummaryList
- **Quiz System**: QuizList, QuizForm, QuizQuestion, QuizPage, NewQuizPage
- **Admin Tools**: AdminPanelPage with user management

### 🪝 Custom Hooks
- **useFetch**: Authenticated API requests with error handling
- **usePagination**: Pagination logic and navigation
- **useTheme**: Theme switching with system preference detection
- **useLocalStorage**: State persistence in localStorage

### ✨ UI/UX Enhancements
- 🎨 Clean, responsive layout
- 🌗 Theme toggle (with `localStorage` persistence)
- 🏷 AI-generated tags (coming soon)
- 📊 Dashboard insights and activity widgets

---

## ⚙️ Vite Proxy Config (for local backend API)

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

## 🔧 Environment Variables

You can configure Keycloak dynamically via `.env`:

```env
VITE_KEYCLOAK_URL=http://localhost:8080
VITE_KEYCLOAK_REALM=skilltrack
VITE_KEYCLOAK_CLIENT_ID=skilltrack-frontend
```

---

## 📄 License

MIT © 2025 Soulaimen Choura
