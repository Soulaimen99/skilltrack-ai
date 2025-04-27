import React from "react";

export default function SummaryBox({
  summary,
  loadingSummary,
  handleSummarize,
  logs,
  dateRange,
  remaining,
  readOnly = false,
}) {
  const disabled = loadingSummary || logs.length === 0;

  return (
    <div className={`ai-output ${readOnly ? "read-only" : ""}`}>
      {!readOnly && (
        <button onClick={handleSummarize} disabled={disabled}>
          {loadingSummary
            ? "Summarizing..."
            : logs.length === 0
            ? "No logs to summarize"
            : "Generate Summary"}
          {loadingSummary && <span className="spinner" />}
        </button>
      )}

      {summary?.content && (
        <>
          <div
            style={{
              marginTop: "1rem",
              marginBottom: "1rem",
              fontSize: "0.9rem",
              color: "var(--muted)",
            }}
          >
            {summary?.createdAt
              ? `Summary generated at ${new Date(
                  summary.createdAt
                ).toLocaleString()}`
              : dateRange?.from && dateRange?.to
              ? `Summary from ${dateRange.from} to ${dateRange.to}`
              : "Summary"}
          </div>

          <pre>{summary.content}</pre>

          {!readOnly && remaining !== null && (
            <div className="input-footer">
              <p style={{ fontStyle: "italic", marginTop: "0.5rem" }}>
                You have {remaining} summaries left today.
              </p>
            </div>
          )}
        </>
      )}
    </div>
  );
}
