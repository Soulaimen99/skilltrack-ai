import './App.css';
import { useKeycloak } from '@react-keycloak/web';
import Header from './components/Header';
import Dashboard from './pages/Dashboard';

export default function App() {
  const { keycloak, initialized } = useKeycloak();

  if (!initialized) return <p>Loading authentication...</p>;
  if (!keycloak.authenticated) {
    keycloak.login();
    return null;
  }

  return (
    <>
      <Header />
      <main style={{ width: '100%' }}>
        <Dashboard />
      </main>
    </>
  );
}
