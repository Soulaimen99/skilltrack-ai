import {useEffect, useState} from "react";
import {useKeycloak} from "@react-keycloak/web";

/**
 * Retrieves the current authenticated user's information if the user is logged in.
 *
 * The method uses Keycloak for authentication. If the user is authenticated,
 * it fetches the user's data from an API endpoint and returns it as an object.
 *
 * @return {Object|null} The user object containing authenticated user information, or null if the user is not authenticated or if an error occurs.
 */
export default function useCurrentUser() {
	const { keycloak } = useKeycloak();
	const [user, setUser] = useState( null );

	useEffect( () => {
		if ( keycloak.authenticated ) {
			fetch( "/api/auth/me", {
				headers: {
					Authorization: `Bearer ${keycloak.token}`,
				},
			} )
				.then( ( res ) => {
					if ( !res.ok ) throw new Error( "Failed to fetch user" );
					return res.json();
				} )
				.then( setUser )
				.catch( ( err ) => console.error( err ) );
		}
	}, [keycloak] );

	return user;
}
