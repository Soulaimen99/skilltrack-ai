import { useState, useEffect } from "react";
import { useKeycloak } from "@react-keycloak/web";
import LogList from "../components/LogList";

export default function InstructionsPage() {
  const { keycloak } = useKeycloak();
  const [goals, setGoals] = useState([]);
  const [selectedGoalId, setSelectedGoalId] = useState("");
  const [logs, setLogs] = useState([]);
  const [loadingLogs, setLoadingLogs] = useState(false);
  const [instructions, setInstructions] = useState("");
  const [loadingInstructions, setLoadingInstructions] = useState(false);

  useEffect(() => {
    if (keycloak.authenticated && keycloak.token) {
      fetchGoals();
    }
  }, [keycloak.authenticated, keycloak.token]);

  useEffect(() => {
    if (selectedGoalId) {
      fetchLogs(selectedGoalId);
    }
  }, [selectedGoalId]);

  const fetchGoals = async () => {
    try {
      const res = await fetch("/api/goals", {
        headers: { Authorization: `Bearer ${keycloak.token}` },
      });
      if (!res.ok) throw new Error(await res.text());
      const data = await res.json();
      setGoals(data.content || []);
    } catch (err) {
      console.error("Failed to fetch goals", err);
    }
  };

  const fetchLogs = async (goalId) => {
    setLoadingLogs(true);
    try {
      const res = await fetch(`/api/logs?goalId=${goalId}`, {
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
    if (!selectedGoalId) return;
    setLoadingInstructions(true);
    try {
      const res = await fetch("/api/instructions", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${keycloak.token}`,
        },
        body: JSON.stringify({
          goalId: selectedGoalId,
          logs: logs,
        }),
      });
      if (!res.ok) throw new Error(await res.text());
      const instruction = await res.json();
      setInstructions(instruction.advice || "No instructions available.");
    } catch (err) {
      console.error("Failed to generate instructions", err);
    } finally {
      setLoadingInstructions(false);
    }
  };

  return (
    <div className="container">
      <h2>AI Coach</h2>

      <p>Select a learning goal to get personalized AI coaching advice.</p>

      <select
        value={selectedGoalId}
        onChange={(e) => setSelectedGoalId(e.target.value)}
        disabled={goals.length === 0}
      >
        <option value="">-- Select a Goal --</option>
        {goals.map((goal) => (
          <option key={goal.id} value={goal.id}>
            {goal.title}
          </option>
        ))}
      </select>

      {loadingLogs ? (
        <p>Loading logs...</p>
      ) : (
        <>
          <LogList logs={logs} readOnly={true} />

          <button
            onClick={handleGenerateInstructions}
            disabled={logs.length === 0 || loadingInstructions}
          >
            Generate AI Plan
            {loadingInstructions && <span className="spinner" />}
          </button>

          {instructions && (
            <div className="ai-output">
              <h3>AI Suggested Next Steps:</h3>
              <pre>{instructions}</pre>
            </div>
          )}
        </>
      )}
    </div>
  );
}
