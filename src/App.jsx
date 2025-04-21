import { useKeycloak } from "@react-keycloak/web";
import { Routes, Route, Navigate } from "react-router-dom";
import Header from "./components/Header";
import Dashboard from "./pages/Dashboard";
import AdminPanel from "./pages/AdminPanel";
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
          <Route path="/dashboard" element={<Dashboard />} />
          {isAdmin && <Route path="/admin" element={<AdminPanel />} />}
          <Route path="*" element={<Navigate to="/dashboard" />} />
        </Routes>
      </main>
    </div>
  );
}
