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

  const handleRegister = () => {
    const registerUrl = `${keycloak.authServerUrl}realms/${
      keycloak.realm
    }/protocol/openid-connect/registrations?client_id=${
      keycloak.clientId
    }&response_type=code&scope=openid&redirect_uri=${encodeURIComponent(
      window.location.origin
    )}`;
    window.location.href = registerUrl;
  };

  return (
    <nav>
      <div>
        <strong>SkillTrack</strong>
      </div>
      <div style={{ display: "flex", gap: "1rem", alignItems: "center" }}>
        <NavLink to="/dashboard" className="nav-link">
          Dashboard
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
