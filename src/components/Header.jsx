import { useKeycloak } from '@react-keycloak/web';
import useCurrentUser from '../hooks/useCurrentUser';

export default function Header() {
    const { keycloak } = useKeycloak();
    const user = useCurrentUser();

    const username = user?.username || keycloak.tokenParsed?.preferred_username || 'User';
    const email = keycloak.tokenParsed?.email || '';

    const handleLogout = () => {
        keycloak.logout({ redirectUri: window.location.origin });
    };

    const handleRegister = () => {
        const registerUrl = `${keycloak.authServerUrl}realms/${keycloak.realm}/protocol/openid-connect/registrations?client_id=${keycloak.clientId}&response_type=code&scope=openid&redirect_uri=${encodeURIComponent(window.location.origin)}`;
        window.location.href = registerUrl;
    };

    return (
        <nav>
            <div><strong>SkillTrack</strong></div>
            {keycloak.authenticated ? (
                <div style={{ display: 'flex', gap: '1rem', alignItems: 'center' }}>
                    <span>Welcome, <strong>{username}</strong></span>
                    <span className="log-footer">{email}</span>
                    <button onClick={handleLogout}>Logout</button>
                </div>
            ) : (
                <div style={{ display: 'flex', gap: '1rem' }}>
                    <button onClick={() => keycloak.login()}>Login</button>
                    <button onClick={handleRegister}>Register</button>
                </div>
            )}
        </nav>
    );
}
