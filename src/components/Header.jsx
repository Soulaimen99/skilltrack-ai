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
    <nav className="header-nav">
      <div className="logo">
        <strong>SkillTrack</strong>
      </div>

      <div className="nav-links">
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
        <NavLink to="/quizzes" className="nav-link">
          Quizzes
        </NavLink>
        {isAdmin && (
          <NavLink to="/admin" className="nav-link">
            Admin
          </NavLink>
        )}
      </div>

      <div className="user-info">
        <span>
          Welcome, <strong>{username}</strong>
        </span>
        {email && <span className="input-footer">{email}</span>}
        <div className="header-buttons">
          <button onClick={toggleTheme}>
            {theme === "dark" ? "☀️ Light" : "🌙 Dark"}
          </button>
          <button onClick={handleLogout}>Logout</button>
        </div>
      </div>
    </nav>
  );
}
