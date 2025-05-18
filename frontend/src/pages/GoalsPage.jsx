import {useEffect, useState} from "react";
import {useKeycloak} from "@react-keycloak/web";
import {Link} from "react-router-dom";

/**
 * Renders a GoalsPage component that allows users to view, create, and manage their learning goals.
 * It fetches goals for authenticated users, displays the list of goals, and provides functionalities
 * to create new goals.
 *
 * @return {JSX.Element} A React component displaying the goals list and a form to create new goals.
 */
export default function GoalsPage() {
	const { keycloak } = useKeycloak();
	const [goals, setGoals] = useState( [] );
	const [title, setTitle] = useState( "" );
	const [description, setDescription] = useState( "" );

	useEffect( () => {
		console.log( "GoalsPage - Component mounted or auth changed" );
		if ( keycloak.authenticated ) {
			fetchGoals();
		}
	}, [keycloak.authenticated] );

	const fetchGoals = async () => {
		try {
			console.log( "GoalsPage - Fetching goals from API" );
			const res = await fetch( "/api/goals", {
				headers: { Authorization: `Bearer ${keycloak.token}` },
			} );
			const data = await res.json();
			setGoals( data.content || [] );
		}
		catch ( err ) {
			console.error( "Failed to fetch goals", err );
		}
	};

	const handleCreateGoal = async ( e ) => {
		e.preventDefault();
		console.log( "GoalsPage - Creating new goal:", { title, description } );
		try {
			const res = await fetch( "/api/goals", {
				method: "POST",
				headers: {
					"Content-Type": "application/json",
					Authorization: `Bearer ${keycloak.token}`,
				},
				body: JSON.stringify( { title, description } ),
			} );
			if ( !res.ok ) throw new Error( await res.text() );
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
