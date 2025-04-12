import { useKeycloak } from '@react-keycloak/web';
import Header from './components/Header';
import Dashboard from './pages/Dashboard';
import './App.css';

export default function App() {
  const { keycloak, initialized } = useKeycloak();

  if (!initialized) return <p>Loading authentication...</p>;
  if (!keycloak.authenticated) {
    keycloak.login();
    return null;
  }

  return (
    <div className="container"> {/* this applies the same max-width and centering */}
      <Header />
      <main>
        <Dashboard />
      </main>
    </div>
  );
}
