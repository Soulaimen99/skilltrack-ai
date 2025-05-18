import {useEffect} from "react";
import useLocalStorage from "./useLocalStorage";

const THEME_KEY = "theme";

/**
 * Custom hook to manage and apply the theme (light or dark) for the application.
 *
 * The hook utilizes local storage to persist the selected theme and applies it to the document's root element.
 * Additionally, it checks the user's system preference on first load to set an initial theme if none exists in local storage.
 *
 * @return {Object} An object containing the current theme and a method to toggle between light and dark themes.
 */
export default function useTheme() {
	const [theme, setTheme] = useLocalStorage( THEME_KEY, "light" );

	// Apply theme to document
	useEffect( () => {
		document.documentElement.setAttribute( "data-theme", theme );
	}, [theme] );

	// Check system preference on first load
	useEffect( () => {
		const prefersDark = window.matchMedia( "(prefers-color-scheme: dark)" ).matches;
		if ( !localStorage.getItem( THEME_KEY ) && prefersDark ) {
			setTheme( "dark" );
		}
	}, [setTheme] );

	const toggleTheme = () => {
		setTheme( ( prev ) => (prev === "light" ? "dark" : "light") );
	};

	return { theme, toggleTheme };
}
