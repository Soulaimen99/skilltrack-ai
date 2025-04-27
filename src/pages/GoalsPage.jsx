import { useState, useEffect } from "react";
import { useKeycloak } from "@react-keycloak/web";

export default function GoalsPage() {
  const { keycloak } = useKeycloak();
  const [goals, setGoals] = useState([]);
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");

  useEffect(() => {
    if (keycloak.authenticated) fetchGoals();
  }, [keycloak]);

  const fetchGoals = async () => {
    try {
      const res = await fetch("/api/goals", {
        headers: { Authorization: `Bearer ${keycloak.token}` },
      });
      const data = await res.json();
      setGoals(data.content || []);
    } catch (err) {
      console.error("Failed to fetch goals", err);
    }
  };

  const handleCreateGoal = async (e) => {
    e.preventDefault();
    try {
      const res = await fetch("/api/goals", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${keycloak.token}`,
        },
        body: JSON.stringify({ title, description }),
      });
      if (!res.ok) throw new Error(await res.text());
      await fetchGoals();
      setTitle("");
      setDescription("");
    } catch (err) {
      console.error("Failed to create goal", err);
    }
  };

  return (
    <div className="container">
      <h2>My Learning Goals</h2>

      <form onSubmit={handleCreateGoal}>
        <input
          type="text"
          placeholder="Goal title"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          required
        />
        <textarea
          placeholder="Goal description (optional)"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />
        <button type="submit">Create Goal</button>
      </form>

      <ul>
        {goals.map((goal) => (
          <li key={goal.id}>
            <div className="log-left">
              <div className="log-main">{goal.title}</div>
              <div className="log-tags">{goal.description || "—"}</div>
              <div className="log-footer">
                Created: {new Date(goal.createdAt).toLocaleDateString()}
              </div>
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
}
