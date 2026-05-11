import {useEffect, useState} from "react";
import useFetch from "./useFetch";

/**
 * Retrieves the current authenticated user's information if the user is logged in.
 *
 * The method uses Keycloak for authentication. If the user is authenticated,
 * it fetches the user's data from an API endpoint and returns it as an object.
 *
 * @return {Object|null} The user object containing authenticated user information, or null if the user is not authenticated or if an error occurs.
 */
export default function useCurrentUser() {
	const { get } = useFetch();
	const [user, setUser] = useState( null );

	useEffect( () => {
		get( "/api/auth/me" )
			.then( setUser )
			.catch( ( err ) => console.error( err ) );
	}, [get] );

	return user;
}
