import { NavLink } from "react-router-dom";
import { useKeycloak } from "@react-keycloak/web";
import useCurrentUser from "../hooks/useCurrentUser";
import useTheme from "../hooks/useTheme";

export default function Header() {
  const { keycloak } = useKeycloak();
  const user = useCurrentUser();
  const { theme, toggleTheme } = useTheme();

  const username =
    user?.username || keycloak.tokenParsed?.preferred_username || "User";
  const email = keycloak.tokenParsed?.email || "";
  const isAdmin = keycloak.tokenParsed?.realm_access?.roles?.includes("admin");

  const handleLogout = () => {
    keycloak.logout({ redirectUri: window.location.origin });
  };

  return (
    <nav>
      <div>
        <strong>SkillTrack</strong>
      </div>
      <div style={{ display: "flex", gap: "1rem", alignItems: "center" }}>
        <NavLink to="/goals" className="nav-link">
          Goals
        </NavLink>
        <NavLink to="/instructions" className="nav-link">
          Instructions
        </NavLink>
        <NavLink to="/logs" className="nav-link">
          Logs
        </NavLink>
        <NavLink to="/progress" className="nav-link">
          Progress
        </NavLink>
        {isAdmin && (
          <NavLink to="/admin" className="nav-link">
            Admin
          </NavLink>
        )}
        <span>
          Welcome, <strong>{username}</strong>
        </span>
        <span className="log-footer">{email}</span>
        <button onClick={toggleTheme}>
          {theme === "dark" ? "☀️ Light Mode" : "🌙 Dark Mode"}
        </button>
        <button onClick={handleLogout}>Logout</button>
      </div>
    </nav>
  );
}
