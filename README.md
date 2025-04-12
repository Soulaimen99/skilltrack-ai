# SkillTrack-UI (Frontend)

React frontend for SkillTrack-AI — a lightweight interface to track learning logs and generate summaries, authenticated via Keycloak.

## 🛠 Tech Stack

- React + Vite
- Keycloak.js + @react-keycloak/web
- React Router
- Fetch API
- Minimal custom CSS

## 🚀 Running Locally

```bash
npm install
npm run dev
```

App runs at: [http://localhost:3000](http://localhost:3000)

## 🔐 Auth Setup

- Uses Keycloak for login/logout
- Auto-injects access token into API requests
- Requires `skilltrack` realm and `skilltrack-frontend` client

## 📁 Features

- 📝 Add, view, and delete learning logs
- 🧠 Generate summary from your logs
- 🔐 Secure login/logout
- Minimal responsive UI

## 🔧 Vite Proxy Config (important)

```js
// vite.config.js
server: {
  proxy: {
    '/logs': 'http://localhost:8081',
    '/logs/summarize': 'http://localhost:8081',
  }
}
```

## 📄 License

MIT © 2025 Soulaimen Choura
