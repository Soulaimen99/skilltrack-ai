# SkillTrack-UI (Frontend)

React frontend for SkillTrack-AI — a clean, responsive learning log tracker with summaries powered by OpenAI and authentication via Keycloak.

---

## 🛠 Tech Stack

- React + Vite
- Keycloak.js + @react-keycloak/web
- React Router
- Fetch API
- Custom dark/light theme (CSS variables)
- Minimal CSS (no UI framework)

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
- ✅ Generate summaries from logs (OpenAI-powered)
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

### ✨ UI/UX Enhancements
- 🎨 Clean, responsive layout
- 🌗 Theme toggle (with `localStorage` persistence)
- 🏷 AI-generated tags (coming soon)
- 📊 Dashboard insights and activity widgets (in progress)

---

## ⚙️ Vite Proxy Config (for local backend API)

```js
// vite.config.js
server: {
  proxy: {
    '/logs': 'http://localhost:8081',
    '/admin': 'http://localhost:8081',
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
