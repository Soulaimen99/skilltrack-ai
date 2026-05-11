import {useCallback, useEffect, useState} from "react";
import LogList from "../components/LogList";
import useFetch from "../hooks/useFetch";
import ErrorMessage from "../components/ErrorMessage";
import LoadingSpinner from "../components/LoadingSpinner";

/**
 * The `InstructionsPage` component provides an interface for users to select a learning goal and receive personalized AI coaching advice based on their activity logs.
 *
 * The component integrates with Keycloak for authentication, retrieves learning goals and activity logs from the backend, and generates AI-powered instructions for the selected goal.
 *
 * @return {JSX.Element} A React functional component that renders the instructions page, including goal selection, log display, and instructions generation functionality.
 */
export default function InstructionsPage() {
	const { get, post, loading, error } = useFetch();
	const [goals, setGoals] = useState( [] );
	const [selectedGoalId, setSelectedGoalId] = useState( "" );
	const [logs, setLogs] = useState( [] );
	const [loadingLogs, setLoadingLogs] = useState( false );
	const [instructions, setInstructions] = useState( "" );
	const [loadingInstructions, setLoadingInstructions] = useState( false );

	const fetchGoals = useCallback( async () => {
		try {
			const data = await get( "/api/goals" );
			setGoals( data.content || [] );
		}
		catch ( err ) {
			console.error( "Failed to fetch goals", err );
		}
	}, [get] );

	const fetchLogs = useCallback( async ( goalId ) => {
		setLoadingLogs( true );
		try {
			const data = await get( `/api/logs?goalId=${goalId}` );
			setLogs( data.content || [] );
		}
		catch ( err ) {
			console.error( "Failed to fetch logs", err );
		}
		finally {
			setLoadingLogs( false );
		}
	}, [get] );

	useEffect( () => {
		fetchGoals();
	}, [fetchGoals] );

	useEffect( () => {
		if ( selectedGoalId ) {
			fetchLogs( selectedGoalId );
		}
	}, [fetchLogs, selectedGoalId] );

	const handleGenerateInstructions = async () => {
		if ( !selectedGoalId ) return;
		setLoadingInstructions( true );
		try {
			const instruction = await post( "/api/instructions", {
				goalId: selectedGoalId,
				logs: logs,
			} );
			setInstructions( instruction.advice || "No instructions available." );
		}
		catch ( err ) {
			console.error( "Failed to generate instructions", err );
		}
		finally {
			setLoadingInstructions( false );
		}
	};

	return (
		<div className="container">
			<h2>AI Coach</h2>

			<p>Select a learning goal to get personalized AI coaching advice.</p>

			<ErrorMessage message={error}/>

			<select
				value={selectedGoalId}
				onChange={( e ) => setSelectedGoalId( e.target.value )}
				disabled={goals.length === 0}
			>
				<option value="">-- Select a Goal --</option>
				{goals.map( ( goal ) => (
					<option key={goal.id} value={goal.id}>
						{goal.title}
					</option>
				) )}
			</select>

			{loading && !loadingLogs && <LoadingSpinner label="Loading goals..."/>}

			{loadingLogs ? (
				<LoadingSpinner label="Loading logs..."/>
			) : (
				<>
					<LogList logs={logs} readOnly={true}/>

					<button
						onClick={handleGenerateInstructions}
						disabled={logs.length === 0 || loadingInstructions}
					>
						Generate AI Plan
						{loadingInstructions && <span className="spinner"/>}
					</button>

					{instructions && (
						<div className="ai-output">
							<h3>AI Suggested Next Steps:</h3>
							<pre>{instructions}</pre>
						</div>
					)}
				</>
			)}
		</div>
	);
}
