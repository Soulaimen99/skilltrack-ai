import { useEffect } from "react";
import useLocalStorage from "./useLocalStorage";

const THEME_KEY = "theme";

export default function useTheme() {
  const [theme, setTheme] = useLocalStorage(THEME_KEY, "light");

  // Apply theme to document
  useEffect(() => {
    document.documentElement.setAttribute("data-theme", theme);
  }, [theme]);

  // Check system preference on first load
  useEffect(() => {
    const prefersDark = window.matchMedia("(prefers-color-scheme: dark)").matches;
    if (!localStorage.getItem(THEME_KEY) && prefersDark) {
      setTheme("dark");
    }
  }, [setTheme]);

  const toggleTheme = () => {
    setTheme((prev) => (prev === "light" ? "dark" : "light"));
  };

  return { theme, toggleTheme };
}
