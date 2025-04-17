import React from 'react';

export default function SummaryBox({ summary, loadingSummary, handleSummarize, logs, dateRange, remaining }) {
    const disabled = loadingSummary || logs.length === 0;

    return (
        <div>
            <h3>Summary</h3>
            <button onClick={handleSummarize} disabled={disabled}>
                {loadingSummary ? 'Summarizing...' : logs.length === 0 ? 'No logs to summarize' : 'Generate Summary'}
                {loadingSummary && <span className="spinner" />}
            </button>

            {summary && (
                <div className="summary" style={{ marginTop: '1rem' }}>
                    <input
                        type="text"
                        value={
                            dateRange?.from && dateRange?.to
                                ? `Summary from ${dateRange.from} to ${dateRange.to}`
                                : "Summary"
                        }
                        readOnly
                    />
                    <pre>{summary}</pre>
                    <div className='log-footer'>
                        {remaining !== null && (
                            <p style={{ fontStyle: 'italic', marginTop: '0.5rem' }}>
                                You have {remaining} summaries left today.
                            </p>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
}
