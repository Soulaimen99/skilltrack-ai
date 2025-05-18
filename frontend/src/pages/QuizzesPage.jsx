import {useEffect, useState} from "react";
import {useLocation, useNavigate} from "react-router-dom";
import useFetch from "../hooks/useFetch";
import QuizList from "../components/QuizList";
import DateFilter from "../components/DateFilter";
import ErrorMessage from "../components/ErrorMessage";

/**
 * Represents the QuizzesPage component which displays a list of quizzes,
 * provides filtering options, and allows navigation to create a quiz or view quizzes for a specific goal.
 *
 * @return {JSX.Element} The rendered QuizzesPage component including filters, quizzes, and navigation buttons.
 */
export default function QuizzesPage() {
	const navigate = useNavigate();
	const location = useLocation();
	const { get, loading, error } = useFetch();
	const queryParams = new URLSearchParams( location.search );
	const goalId = queryParams.get( "goalId" );
	const [quizzes, setQuizzes] = useState( [] );
	const [goalTitle, setGoalTitle] = useState( "" );
	const [presetRange, setPresetRange] = useState( "all" );
	const [dateRange, setDateRange] = useState( { from: "", to: "" } );
	const [showCompleted, setShowCompleted] = useState( false );

	// Fetch quizzes when component mounts or filters change
	useEffect( () => {
		const fetchQuizzes = async () => {
			try {
				if ( goalId ) {
					// Fetch quizzes for a specific goal
					const response = await get( `/api/quizzes/goal/${goalId}` );
					setQuizzes( response || [] );

					// If we have quizzes, get the goal title from the first quiz
					if ( response && response.length > 0 ) {
						setGoalTitle( response[0].goalTitle );
					}
					else {
						// If no quizzes, fetch the goal to get its title
						try {
							const goalResponse = await get( `/api/goals/${goalId}` );
							setGoalTitle( goalResponse.title );
						}
						catch {
							// If we can't get the goal, just use a generic title
							setGoalTitle( "Goal" );
						}
					}
				}
				else {
					// Fetch all quizzes with filters
					const params = new URLSearchParams();
					if ( dateRange.from ) params.append( "from", dateRange.from );
					if ( dateRange.to ) params.append( "to", dateRange.to );
					params.append( "completed", showCompleted.toString() );
					params.append( "page", "0" );
					params.append( "size", "100" );

					const response = await get( `/api/quizzes?${params.toString()}` );
					setQuizzes( response.content || [] );
					setGoalTitle( "" );
				}
			}
			catch {
				// Error is handled by useFetch
			}
		};

		fetchQuizzes();
	}, [get, dateRange, showCompleted, goalId] );

	const handleCreateQuiz = () => {
		navigate( goalId ? `/quizzes/new?goalId=${goalId}` : "/quizzes/new" );
	};

	return (
		<div className="container">
			<div className="page-header">
				<h2>{goalTitle ? `Quizzes for ${goalTitle}` : "My Quizzes"}</h2>
				{goalId && (
					<button
						className="back-button"
						onClick={() => navigate( "/quizzes" )}
					>
						View All Quizzes
					</button>
				)}
			</div>

			{!goalId && (
				<div className="filters">
					<DateFilter
						dateRange={dateRange}
						setDateRange={setDateRange}
						presetRange={presetRange}
						setPresetRange={setPresetRange}
					/>

					<div className="filter-group">
						<label>
							<input
								type="checkbox"
								checked={showCompleted}
								onChange={( e ) => setShowCompleted( e.target.checked )}
							/>
							Show Completed Quizzes
						</label>
					</div>
				</div>
			)}

			{error && <ErrorMessage message={error}/>}

			<QuizList
				quizzes={quizzes}
				loading={loading}
				error={error}
				onCreateQuiz={handleCreateQuiz}
				showGoalInfo={true}
			/>
		</div>
	);
}
