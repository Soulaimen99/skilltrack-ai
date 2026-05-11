import {useCallback, useEffect, useState} from "react";
import {Link} from "react-router-dom";
import useFetch from "../hooks/useFetch";
import ErrorMessage from "../components/ErrorMessage";
import LoadingSpinner from "../components/LoadingSpinner";

/**
 * Renders a GoalsPage component that allows users to view, create, and manage their learning goals.
 * It fetches goals for authenticated users, displays the list of goals, and provides functionalities
 * to create new goals.
 *
 * @return {JSX.Element} A React component displaying the goals list and a form to create new goals.
 */
export default function GoalsPage() {
	const { get, post, loading, error } = useFetch();
	const [goals, setGoals] = useState( [] );
	const [title, setTitle] = useState( "" );
	const [description, setDescription] = useState( "" );

	const fetchGoals = useCallback( async () => {
		try {
			const data = await get( "/api/goals" );
			setGoals( data.content || [] );
		}
		catch ( err ) {
			console.error( "Failed to fetch goals", err );
		}
	}, [get] );

	useEffect( () => {
		fetchGoals();
	}, [fetchGoals] );

	const handleCreateGoal = async ( e ) => {
		e.preventDefault();
		try {
			await post( "/api/goals", { title, description } );
			await fetchGoals();
			setTitle( "" );
			setDescription( "" );
		}
		catch ( err ) {
			console.error( "Failed to create goal", err );
		}
	};

	return (
		<div className="container">
			<h2>My Learning Goals</h2>

			<ErrorMessage message={error}/>

			{loading && <LoadingSpinner label="Loading goals..."/>}

			<form onSubmit={handleCreateGoal}>
				<input
					type="text"
					placeholder="Goal title"
					value={title}
					onChange={( e ) => setTitle( e.target.value )}
					required
				/>
				<textarea
					placeholder="Goal description (optional)"
					value={description}
					onChange={( e ) => setDescription( e.target.value )}
				/>
				<button type="submit">Create Goal</button>
			</form>

			<ul>
				{goals.map( ( goal ) => (
					<li key={goal.id}>
						<div className="input-left">
							<div className="input-first">{goal.title}</div>
							<div className="input-second">{goal.description || "—"}</div>
							<div className="input-footer">
								Created: {new Date( goal.date ).toLocaleDateString()}{" "}
								{new Date( goal.date ).toLocaleTimeString( [], {
									hour: "2-digit",
									minute: "2-digit",
								} )}
							</div>
						</div>
						<div className="goal-actions">
							<Link to={`/quizzes/new?goalId=${goal.id}`} className="button">
								Create Quiz
							</Link>
							<Link to={`/quizzes?goalId=${goal.id}`} className="button secondary">
								View Quizzes
							</Link>
						</div>
					</li>
				) )}
			</ul>
		</div>
	);
}
