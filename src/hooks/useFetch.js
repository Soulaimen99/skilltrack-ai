import { useState, useCallback } from "react";
import { useKeycloak } from "@react-keycloak/web";

/**
 * Custom hook for making authenticated API requests with better error handling
 * @returns {Object} fetch functions and state
 */
export default function useFetch() {
  const { keycloak } = useKeycloak();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  /**
   * Make an authenticated API request
   * @param {string} url - The URL to fetch
   * @param {string} method - The HTTP method
   * @param {Object} body - The request body (for POST/PUT)
   * @param {Object} options - Additional fetch options
   * @returns {Promise<any>} The response data
   */
  const fetchWithAuth = useCallback(
    async (url, method = "GET", body = null, options = {}) => {
      setLoading(true);
      setError(null);

      try {
        const headers = {
          ...options.headers,
          Authorization: `Bearer ${keycloak.token}`,
        };

        // Add Content-Type header for requests with body
        if (body && !headers["Content-Type"]) {
          headers["Content-Type"] = "application/json";
        }

        const response = await fetch(url, {
          ...options,
          method,
          headers,
          body: body ? JSON.stringify(body) : null,
        });

        if (!response.ok) {
          const errorText = await response.text();
          throw new Error(errorText || `Error ${response.status}: ${response.statusText}`);
        }

        // Check if response is empty
        const contentType = response.headers.get("content-type");
        if (contentType && contentType.includes("application/json")) {
          const data = await response.json();
          return data;
        }

        return null;
      } catch (err) {
        setError(err.message || "An unknown error occurred");
        throw err;
      } finally {
        setLoading(false);
      }
    },
    [keycloak]
  );

  /**
   * Make an authenticated GET request
   * @param {string} url - The URL to fetch
   * @param {Object} options - Additional fetch options
   * @returns {Promise<any>} The response data
   */
  const get = useCallback(
    (url, options = {}) => fetchWithAuth(url, "GET", null, options),
    [fetchWithAuth]
  );

  /**
   * Make an authenticated POST request
   * @param {string} url - The URL to fetch
   * @param {Object} body - The request body
   * @param {Object} options - Additional fetch options
   * @returns {Promise<any>} The response data
   */
  const post = useCallback(
    (url, body, options = {}) => fetchWithAuth(url, "POST", body, options),
    [fetchWithAuth]
  );

  /**
   * Make an authenticated PUT request
   * @param {string} url - The URL to fetch
   * @param {Object} body - The request body
   * @param {Object} options - Additional fetch options
   * @returns {Promise<any>} The response data
   */
  const put = useCallback(
    (url, body, options = {}) => fetchWithAuth(url, "PUT", body, options),
    [fetchWithAuth]
  );

  /**
   * Make an authenticated DELETE request
   * @param {string} url - The URL to fetch
   * @param {Object} options - Additional fetch options
   * @returns {Promise<any>} The response data
   */
  const del = useCallback(
    (url, options = {}) => fetchWithAuth(url, "DELETE", null, options),
    [fetchWithAuth]
  );

  /**
   * Download data as a file
   * @param {any} data - The data to download
   * @param {string} filename - The filename
   * @param {string} format - The file format (json, txt)
   */
  const downloadData = useCallback((data, filename, format = "json") => {
    const blob = new Blob([JSON.stringify(data, null, 2)], {
      type: format === "json" ? "application/json" : "text/plain",
    });
    const url = URL.createObjectURL(blob);

    const a = document.createElement("a");
    a.href = url;
    a.download = filename;
    a.click();
    URL.revokeObjectURL(url);
  }, []);

  return { get, post, put, del, loading, error, downloadData };
}
