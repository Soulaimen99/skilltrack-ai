import { useState, useEffect } from "react";
import { useKeycloak } from "@react-keycloak/web";
import LogList from "../components/LogList";

export default function InstructionsPage() {
  const { keycloak } = useKeycloak();
  const [logs, setLogs] = useState([]);
  const [loadingLogs, setLoadingLogs] = useState(false);
  const [instructions, setInstructions] = useState("");
  const [loadingInstructions, setLoadingInstructions] = useState(false);

  useEffect(() => {
    if (keycloak.authenticated && keycloak.token) {
      fetchLogs();
    }
  }, [keycloak.authenticated, keycloak.token]);

  const fetchLogs = async () => {
    setLoadingLogs(true);
    try {
      const res = await fetch("/api/logs?page=0&size=20", {
        headers: { Authorization: `Bearer ${keycloak.token}` },
      });
      if (!res.ok) throw new Error(await res.text());
      const data = await res.json();
      setLogs(data.content || []);
    } catch (err) {
      console.error("Failed to fetch logs", err);
    } finally {
      setLoadingLogs(false);
    }
  };

  const handleGenerateInstructions = async () => {
    setLoadingInstructions(true);
    try {
      const res = await fetch("/api/summaries", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${keycloak.token}`,
        },
        body: JSON.stringify(logs),
      });
      if (!res.ok) throw new Error(await res.text());
      const summary = await res.json();
      setInstructions(summary.content || "No instructions available.");
    } catch (err) {
      console.error("Failed to generate instructions", err);
    } finally {
      setLoadingInstructions(false);
    }
  };

  return (
    <div className="container">
      <h2>AI Instructions</h2>

      <p>
        Based on your recent learning logs, get AI-generated next steps or
        advice!
      </p>

      {loadingLogs ? (
        <p>Loading logs...</p>
      ) : (
        <>
          <LogList logs={logs} readOnly={true} />
          <button
            onClick={handleGenerateInstructions}
            disabled={logs.length === 0 || loadingInstructions}
          >
            {loadingInstructions
              ? "Generating Instructions..."
              : "Generate Instructions"}
          </button>

          {instructions && (
            <div className="summary" style={{ marginTop: "2rem" }}>
              <h3>Instructions:</h3>
              <pre>{instructions}</pre>
            </div>
          )}
        </>
      )}
    </div>
  );
}
