import React from "react";

export default function SummaryList({ summaries = [] }) {
  if (summaries.length === 0) {
    return <p>No summaries available.</p>;
  }

  return (
    <div className="summary-list">
      {summaries.map((summary, idx) => (
        <div key={summary.id || idx} className="summary-item">
          <input
            type="text"
            value={
              summary?.createdAt
                ? `Summary generated at ${new Date(
                    summary.createdAt
                  ).toLocaleString()}`
                : "Summary"
            }
            readOnly
          />
          <pre>{summary.content}</pre>
        </div>
      ))}
    </div>
  );
}
