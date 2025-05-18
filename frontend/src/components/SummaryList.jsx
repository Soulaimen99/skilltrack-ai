import React from "react";

/**
 * Renders a list of summaries. If no summaries are provided, displays a message indicating no summaries are available.
 *
 * @param {Object} props - The component's props.
 * @param {Array<Object>} props.summaries - An array of summary objects. Each summary object should contain `id`, `createdAt`, and `content` properties.
 * @return {JSX.Element} A React component displaying a list of summaries or a message if no summaries are available.
 */
export default function SummaryList( { summaries } ) {
	if ( !summaries.length ) {
		return <p>No summaries available.</p>;
	}

	return (
		<div>
			{summaries.map( ( summary ) => (
				<div key={summary.id} className="ai-output">
					<div
						style={{
							marginBottom: "1rem",
							fontSize: "0.9rem",
							color: "var(--muted)",
						}}
					>
						Summary generated at{" "}
						{new Date( summary.createdAt ).toLocaleDateString()}{" "}
						{new Date( summary.createdAt ).toLocaleTimeString( [], {
							hour: "2-digit",
							minute: "2-digit",
						} )}
					</div>

					<pre>{summary.content}</pre>
				</div>
			) )}
		</div>
	);
}
