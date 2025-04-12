import { useKeycloak } from '@react-keycloak/web';

export default function Header() {
    const { keycloak } = useKeycloak();

    const handleLogout = () => {
        keycloak.logout({ redirectUri: window.location.origin });
    };

    const username = keycloak.tokenParsed?.preferred_username || 'User';

    return (
        <nav>
            <div><strong>SkillTrack</strong></div>
            {keycloak.authenticated && (
                <div style={{ display: 'flex', gap: '1rem', alignItems: 'center' }}>
                    <span>Welcome, <strong>{username}</strong></span>
                    <button onClick={handleLogout}>Logout</button>
                </div>
            )}
        </nav>
    );
}
