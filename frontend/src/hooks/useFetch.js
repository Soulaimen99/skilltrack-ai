import {useCallback, useState} from "react";
import {useKeycloak} from "@react-keycloak/web";

/**
 * Custom hook for making authenticated API requests with better error handling
 * @returns {Object} fetch functions and state
 */
export default function useFetch() {
	const { keycloak } = useKeycloak();
	const [loading, setLoading] = useState( false );
	const [error, setError] = useState( null );

	const fetchWithAuth = useCallback(
		async ( url, method = "GET", body = null, options = {} ) => {
			setLoading( true );
			setError( null );

			try {
				const headers = {
					...options.headers,
					Authorization: `Bearer ${keycloak.token}`,
				};

				// Add Content-Type header for requests with body
				if ( body && !headers["Content-Type"] ) {
					headers["Content-Type"] = "application/json";
				}

				const response = await fetch( url, {
					...options,
					method,
					headers,
					body: body ? JSON.stringify( body ) : null,
				} );

				if ( !response.ok ) {
					const errorText = await response.text();
					throw new Error( errorText || `Error ${response.status}: ${response.statusText}` );
				}

				// Check if response is empty
				const contentType = response.headers.get( "content-type" );
				if ( contentType && contentType.includes( "application/json" ) ) {
					const data = await response.json();
					return data;
				}

				return null;
			}
			catch ( err ) {
				setError( err.message || "An unknown error occurred" );
				throw err;
			}
			finally {
				setLoading( false );
			}
		},
		[keycloak]
	);

	const get = useCallback(
		( url, options = {} ) => fetchWithAuth( url, "GET", null, options ),
		[fetchWithAuth]
	);

	const post = useCallback(
		( url, body, options = {} ) => fetchWithAuth( url, "POST", body, options ),
		[fetchWithAuth]
	);

	const put = useCallback(
		( url, body, options = {} ) => fetchWithAuth( url, "PUT", body, options ),
		[fetchWithAuth]
	);

	const del = useCallback(
		( url, options = {} ) => fetchWithAuth( url, "DELETE", null, options ),
		[fetchWithAuth]
	);
    
	const downloadData = useCallback( ( data, filename, format = "json" ) => {
		const blob = new Blob( [JSON.stringify( data, null, 2 )], {
			type: format === "json" ? "application/json" : "text/plain",
		} );
		const url = URL.createObjectURL( blob );

		const a = document.createElement( "a" );
		a.href = url;
		a.download = filename;
		a.click();
		URL.revokeObjectURL( url );
	}, [] );

	return { get, post, put, del, loading, error, downloadData };
}
