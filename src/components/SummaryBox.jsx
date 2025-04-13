import React from 'react';

export default function SummaryBox({ summary, loadingSummary, handleSummarize }) {
    return (
        <div>
            <h3>Summary</h3>
            <button onClick={handleSummarize} disabled={loadingSummary}>
                {loadingSummary ? 'Summarizing...' : 'Generate Summary'}
            </button>
            {summary && (
                <div className="summary">
                    <pre>{summary}</pre>
                </div>
            )}
        </div>
    );
}