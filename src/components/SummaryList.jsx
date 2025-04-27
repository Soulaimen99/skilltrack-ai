import React from "react";

export default function SummaryList({ summaries }) {
  if (!summaries.length) {
    return <p>No summaries available.</p>;
  }

  return (
    <div>
      {summaries.map((summary) => (
        <div key={summary.id} className="ai-output">
          <div
            style={{
              marginBottom: "1rem",
              fontSize: "0.9rem",
              color: "var(--muted)",
            }}
          >
            Summary generated at{" "}
            {new Date(summary.createdAt).toLocaleDateString()}{" "}
            {new Date(summary.createdAt).toLocaleTimeString([], {
              hour: "2-digit",
              minute: "2-digit",
            })}
          </div>

          <pre>{summary.content}</pre>
        </div>
      ))}
    </div>
  );
}
