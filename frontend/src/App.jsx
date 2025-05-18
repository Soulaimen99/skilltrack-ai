import { useKeycloak } from "@react-keycloak/web";
import { Routes, Route, Navigate } from "react-router-dom";
import Header from "./components/Header";
import GoalsPage from "./pages/GoalsPage";
import InstructionsPage from "./pages/InstructionsPage";
import LogsPage from "./pages/LogsPage";
import ProgressPage from "./pages/ProgressPage";
import AdminPanelPage from "./pages/AdminPanelPage";
import QuizzesPage from "./pages/QuizzesPage";
import QuizPage from "./pages/QuizPage";
import NewQuizPage from "./pages/NewQuizPage";
import "./index.css";

export default function App() {
  const { keycloak, initialized } = useKeycloak();
  const isAdmin = keycloak?.tokenParsed?.realm_access?.roles?.includes("admin");

  if (!initialized) return <p>Loading authentication...</p>;
  if (!keycloak.authenticated) {
    keycloak.login();
    return null;
  }

  return (
    <div className="container">
      <Header />
      <main>
        <Routes>
          <Route path="/goals" element={<GoalsPage />} />
          <Route path="/instructions" element={<InstructionsPage />} />
          <Route path="/logs" element={<LogsPage />} />
          <Route path="/progress" element={<ProgressPage />} />
          <Route path="/quizzes" element={<QuizzesPage />} />
          <Route path="/quizzes/new" element={<NewQuizPage />} />
          <Route path="/quizzes/:quizId" element={<QuizPage />} />
          {isAdmin && <Route path="/admin" element={<AdminPanelPage />} />}
          <Route path="*" element={<Navigate to="/goals" />} />
        </Routes>
      </main>
    </div>
  );
}
