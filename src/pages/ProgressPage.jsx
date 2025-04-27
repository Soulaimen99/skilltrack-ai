import { useState, useEffect } from "react";
import { useKeycloak } from "@react-keycloak/web";

export default function ProgressPage() {
  const { keycloak } = useKeycloak();
  const [insights, setInsights] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (keycloak.authenticated && keycloak.token) {
      fetchInsights();
    }
  }, [keycloak.authenticated, keycloak.token]);

  const fetchInsights = async () => {
    setLoading(true);
    try {
      const res = await fetch("/api/logs/insights", {
        headers: { Authorization: `Bearer ${keycloak.token}` },
      });
      if (!res.ok) throw new Error(await res.text());
      const data = await res.json();
      setInsights(data);
    } catch (err) {
      console.error("Failed to fetch insights", err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <p>Loading your progress...</p>;

  return (
    <div className="container">
      <h2>My Progress</h2>

      {insights ? (
        <ul>
          <li>
            <strong>Logs in last 7 days:</strong> {insights.logsLast7Days}
          </li>
          <li>
            <strong>Logs in last 30 days:</strong> {insights.logsLast30Days}
          </li>
          <li>
            <strong>Most used tag:</strong> {insights.mostUsedTag || "None"}
          </li>
          <li>
            <strong>Days since last log:</strong>{" "}
            {insights.daysSinceLastLog ?? "N/A"}
          </li>
        </ul>
      ) : (
        <p>No learning activity yet. Start logging your progress!</p>
      )}
    </div>
  );
}
