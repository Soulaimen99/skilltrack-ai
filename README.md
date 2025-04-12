# Skilltrack UI

Frontend for the Skilltrack project built with **React** and **Vite**.

## 🚀 Setup

```bash
# Clone the repo (if needed)
git clone <your-repo-url>
cd skilltrack-ui

# Install dependencies
npm install

# Start development server
npm run dev
```

> The app will run on [http://localhost:3000](http://localhost:3000) if you've set that in `vite.config.js`.

## 📁 Project Structure

```text
src/
├── App.jsx              # Main app with routing
├── main.jsx             # Entry point
├── App.css              # Global styles
├── components/
│   └── MainPage.jsx     # View, add, delete, summarize logs
```

## 🌐 API

- Expects backend running on **http://localhost:8081**
- Uses **Basic Auth** via `Authorization` header
- Communicates with endpoints:
  - `GET /logs`
  - `POST /logs`
  - `DELETE /logs/{id}`
  - `POST /logs/summarize`

## 🛠 Built With

- [React](https://reactjs.org/)
- [Vite](https://vitejs.dev/)
- [React Router DOM](https://reactrouter.com/)

## 📦 Available Scripts

```bash
npm run dev     # Start dev server
npm run build   # Build for production
npm run preview # Preview production build
```

## 📌 Notes

- Credentials are stored temporarily in `localStorage` (`auth` key)
- `vite.config.js` is set to use port 3000
- Layout adjusts for login vs logs view
