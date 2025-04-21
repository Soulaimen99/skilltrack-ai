import { useState, useEffect } from "react";
import { useKeycloak } from "@react-keycloak/web";

export default function useCurrentUser() {
  const { keycloak } = useKeycloak();
  const [user, setUser] = useState(null);

  useEffect(() => {
    if (keycloak.authenticated) {
      fetch("/api/auth/me", {
        headers: {
          Authorization: `Bearer ${keycloak.token}`,
        },
      })
        .then((res) => {
          if (!res.ok) throw new Error("Failed to fetch user");
          return res.json();
        })
        .then(setUser)
        .catch((err) => console.error(err));
    }
  }, [keycloak]);

  return user;
}
