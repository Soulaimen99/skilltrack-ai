import { useKeycloak } from '@react-keycloak/web';

export default function Header() {
    const { keycloak } = useKeycloak();

    const handleLogout = () => {
        keycloak.logout({ redirectUri: window.location.origin });
    };

    return (
        <nav>
            <div><strong>SkillTrack</strong></div>
            {keycloak.authenticated && (
                <button onClick={handleLogout}>Logout</button>
            )}
        </nav>
    );
}
