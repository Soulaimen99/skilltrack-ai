import {useCallback, useEffect, useMemo, useState} from "react";
import {Link, useNavigate, useParams} from "react-router-dom";
import useFetch from "../hooks/useFetch";
import ErrorMessage from "../components/ErrorMessage";
import LoadingSpinner from "../components/LoadingSpinner";
import LogList from "../components/LogList";
import QuizList from "../components/QuizList";

function formatDateTime( value ) {
	if ( !value ) return "N/A";

	const date = new Date( value );
	return `${date.toLocaleDateString()} ${date.toLocaleTimeString( [], {
		hour: "2-digit",
		minute: "2-digit",
	} )}`;
}

function sortByDateDesc( items, getDate ) {
	return [...items].sort( ( left, right ) => {
		const leftTime = new Date( getDate( left ) ).getTime();
		const rightTime = new Date( getDate( right ) ).getTime();
		return rightTime - leftTime;
	} );
}

export default function GoalDetailPage() {
	const { goalId } = useParams();
	const navigate = useNavigate();
	const { get } = useFetch();
	const [goal, setGoal] = useState( null );
	const [logs, setLogs] = useState( [] );
	const [quizzes, setQuizzes] = useState( [] );
	const [instructions, setInstructions] = useState( [] );
	const [goalError, setGoalError] = useState( "" );
	const [logsError, setLogsError] = useState( "" );
	const [quizzesError, setQuizzesError] = useState( "" );
	const [instructionsError, setInstructionsError] = useState( "" );
	const [loading, setLoading] = useState( true );

	const loadGoalDetails = useCallback( async () => {
		if ( !goalId ) {
			setGoalError( "Missing goal identifier." );
			setLoading( false );
			return;
		}

		setLoading( true );
		setGoalError( "" );
		setLogsError( "" );
		setQuizzesError( "" );
		setInstructionsError( "" );

		try {
			const goalData = await get( `/api/goals/${goalId}` );
			setGoal( goalData );
		}
		catch {
			setGoalError( "This goal could not be loaded." );
			setLoading( false );
			return;
		}

		const [logsResult, quizzesResult, instructionsResult] = await Promise.allSettled( [
			get( `/api/logs?goalId=${goalId}&page=0&size=100` ),
			get( `/api/quizzes/goal/${goalId}` ),
			get( "/api/instructions" ),
		] );

		if ( logsResult.status === "fulfilled" ) {
			setLogs( sortByDateDesc( logsResult.value.content || [], ( log ) => log.date ) );
		}
		else {
			setLogsError( "Unable to load logs for this goal." );
		}

		if ( quizzesResult.status === "fulfilled" ) {
			setQuizzes( sortByDateDesc( quizzesResult.value || [], ( quiz ) => quiz.startedAt ) );
		}
		else {
			setQuizzesError( "Unable to load quizzes for this goal." );
		}

		if ( instructionsResult.status === "fulfilled" ) {
			const goalInstructions = ( instructionsResult.value || [] )
				.filter( ( instruction ) => instruction.goalId === goalId )
				.sort( ( left, right ) => new Date( right.createdAt ).getTime() - new Date( left.createdAt ).getTime() );
			setInstructions( goalInstructions );
		}
		else {
			setInstructionsError( "Unable to load coaching history for this goal." );
		}

		setLoading( false );
	}, [goalId, get] );

	useEffect( () => {
		loadGoalDetails();
	}, [loadGoalDetails] );

	const latestActivity = useMemo( () => {
		const timestamps = [
			...logs.map( ( log ) => log.date ),
			...quizzes.map( ( quiz ) => quiz.endedAt || quiz.startedAt ),
			...instructions.map( ( instruction ) => instruction.createdAt ),
		]
			.filter( Boolean )
			.map( ( value ) => new Date( value ).getTime() )
			.filter( Number.isFinite );

		if ( timestamps.length === 0 ) return null;

		return new Date( Math.max( ...timestamps ) );
	}, [instructions, logs, quizzes] );

	const completedQuizzes = quizzes.filter( ( quiz ) => quiz.completed ).length;
	const recentInstruction = instructions[0] || null;

	if ( loading ) {
		return (
			<div className="container">
				<LoadingSpinner label="Loading goal details..."/>
			</div>
		);
	}

	if ( goalError ) {
		return (
			<div className="container">
				<ErrorMessage message={goalError}/>
				<button className="back-button" onClick={() => navigate( "/goals" )}>
					Back to goals
				</button>
			</div>
		);
	}

	return (
		<div className="container goal-detail-page">
			<div className="page-header goal-detail-header">
				<div>
					<h2>{goal.title}</h2>
					<p className="goal-detail-description">
						{goal.description || "No description provided for this goal."}
					</p>
					<div className="goal-detail-meta">
						Created {formatDateTime( goal.date )}
						{latestActivity && <span> - Last activity {formatDateTime( latestActivity )}</span>}
					</div>
				</div>

				<div className="goal-detail-actions">
					<Link to="/goals" className="back-button">
						Back to goals
					</Link>
					<Link to={`/quizzes/new?goalId=${goal.id}`} className="button">
						Create quiz
					</Link>
					<Link to={`/quizzes?goalId=${goal.id}`} className="button secondary">
						View quizzes
					</Link>
					<Link to="/instructions" className="button secondary">
						Open AI coach
					</Link>
				</div>
			</div>

			<div className="goal-metrics">
				<div className="goal-metric">
					<span>Logs</span>
					<strong>{logs.length}</strong>
				</div>
				<div className="goal-metric">
					<span>Quizzes</span>
					<strong>{quizzes.length}</strong>
				</div>
				<div className="goal-metric">
					<span>Completed</span>
					<strong>{completedQuizzes}</strong>
				</div>
				<div className="goal-metric">
					<span>AI notes</span>
					<strong>{instructions.length}</strong>
				</div>
			</div>

			<section className="goal-section">
				<div className="section-header">
					<h3>Recent logs</h3>
				</div>
				<ErrorMessage message={logsError}/>
				<LogList logs={logs.slice( 0, 5 )} readOnly={true}/>
			</section>

			<section className="goal-section">
				<div className="section-header">
					<h3>Quizzes for this goal</h3>
				</div>
				<ErrorMessage message={quizzesError}/>
				<QuizList
					quizzes={quizzes.slice( 0, 5 )}
					onCreateQuiz={() => navigate( `/quizzes/new?goalId=${goal.id}` )}
					showGoalInfo={false}
				/>
			</section>

			<section className="goal-section">
				<div className="section-header">
					<h3>AI coaching history</h3>
				</div>
				<ErrorMessage message={instructionsError}/>
				{recentInstruction ? (
					<div className="ai-output">
						<h4>Latest advice</h4>
						<div className="goal-detail-meta">
							Generated {formatDateTime( recentInstruction.createdAt )}
						</div>
						<pre>{recentInstruction.advice}</pre>
					</div>
				) : (
					<div className="empty-state">
						<p>No AI coaching has been generated for this goal yet.</p>
						<Link to="/instructions" className="button secondary">
							Generate coaching advice
						</Link>
					</div>
				)}

				{instructions.length > 1 && (
					<ul className="goal-instruction-list">
						{instructions.slice( 1, 4 ).map( ( instruction ) => (
							<li key={instruction.id} className="goal-instruction-item">
								<div className="input-left">
									<div className="input-second">{instruction.advice}</div>
									<div className="input-footer">
										{formatDateTime( instruction.createdAt )}
									</div>
								</div>
							</li>
						) )}
					</ul>
				)}
			</section>
		</div>
	);
}
