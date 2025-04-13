import { useKeycloak } from '@react-keycloak/web';
import Header from './components/Header';
import Dashboard from './pages/Dashboard';
import './index.css';

export default function App() {
  const { keycloak, initialized } = useKeycloak();

  if (!initialized) return <p>Loading authentication...</p>;
  if (!keycloak.authenticated) {
    keycloak.login();
    return null;
  }

  return (
    <div className="container">
      <Header />
      <main>
        <Dashboard />
      </main>
    </div>
  );
}
