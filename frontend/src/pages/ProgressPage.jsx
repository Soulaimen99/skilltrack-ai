import {useCallback, useEffect, useState} from "react";
import useFetch from "../hooks/useFetch";
import ErrorMessage from "../components/ErrorMessage";
import LoadingSpinner from "../components/LoadingSpinner";

/**
 * Renders a progress tracking page for authenticated users. Displays insights
 * about user's logging activity, such as logs in the last 7 and 30 days, most
 * used tag, and days since the last log. If no insights are available, prompts
 * users to start logging their progress.
 *
 * Automatically fetches insights data from the server when the user is
 * authenticated. Shows a loading message while fetching data.
 *
 * @return {JSX.Element} The rendered ProgressPage component.
 */
export default function ProgressPage() {
	const { get, loading, error } = useFetch();
	const [insights, setInsights] = useState( null );

	const fetchInsights = useCallback( async () => {
		try {
			const data = await get( "/api/logs/insights" );
			setInsights( data );
		}
		catch ( err ) {
			console.error( "Failed to fetch insights", err );
		}
	}, [get] );

	useEffect( () => {
		fetchInsights();
	}, [fetchInsights] );

	return (
		<div className="container">
			<h2>My Progress</h2>

			<ErrorMessage message={error}/>

			{loading && <LoadingSpinner label="Loading your progress..."/>}

			{insights ? (
				<div className="insights-and-export">
					<ul>
						<li>
							<strong>Logs in last 7 days:</strong> {insights.logsLast7Days}
						</li>
						<li>
							<strong>Logs in last 30 days:</strong> {insights.logsLast30Days}
						</li>
						<li>
							<strong>Most used tag:</strong> {insights.mostUsedTag || "None"}
						</li>
						<li>
							<strong>Days since last log:</strong>{" "}
							{insights.daysSinceLastLog ?? "N/A"}
						</li>
					</ul>
				</div>
			) : (
				<p>No learning activity yet. Start logging your progress!</p>
			)}
		</div>
	);
}
