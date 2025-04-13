import React from 'react';

export default function SummaryBox({ summary, loadingSummary, handleSummarize, logs, dateRange }) {
    const disabled = loadingSummary || logs.length === 0;
    return (
        <div>
            <h3>Summary</h3>
            <button onClick={handleSummarize} disabled={disabled}>
                {loadingSummary ? 'Summarizing...' : logs.length === 0 ? 'No logs to summarize' : 'Generate Summary'}
            </button>
            {summary && (
                <div className="summary">
                    <input type="text" value={`Summary from ${dateRange.from} to ${dateRange.to}`} readOnly />
                    <pre>{summary}</pre>
                </div>
            )}
        </div>
    );
}