import React from "react";

/**
 * Component for rendering a summary box with controls to generate summaries and display the resulting content.
 *
 * @param {Object} props - The properties passed into the component.
 * @param {Object} [props.summary] - An object containing the summary content and its metadata such as creation time.
 * @param {boolean} props.loadingSummary - A flag indicating whether a summary is currently being generated.
 * @param {Function} props.handleSummarize - A callback function to handle the summary generation action.
 * @param {Array} props.logs - A list of logs to be summarized.
 * @param {Object} [props.dateRange] - The date range for which the summary is applicable (includes `from` and `to` properties).
 * @param {number|null} [props.remaining] - The number of summaries remaining for the user today.
 * @param {boolean} [props.readOnly=false] - A flag indicating whether the component should be in read-only mode.
 * @return {JSX.Element} - The rendered SummaryBox component.
 */
export default function SummaryBox( {
	                                    summary,
	                                    loadingSummary,
	                                    handleSummarize,
	                                    logs,
	                                    dateRange,
	                                    remaining,
	                                    readOnly = false,
                                    } ) {
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
					{loadingSummary && <span className="spinner"/>}
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
