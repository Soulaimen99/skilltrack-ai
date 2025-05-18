import {useLocation} from "react-router-dom";
import QuizForm from "../components/QuizForm";

/**
 * Represents a page component for creating a new quiz.
 * It extracts the `goalId` from the query parameters of the current URL
 * and passes it as a prop to the `QuizForm` component.
 *
 * @return {JSX.Element} A container element rendering the `QuizForm` component with the extracted `goalId`.
 */
export default function NewQuizPage() {
	const location = useLocation();
	const queryParams = new URLSearchParams( location.search );
	const goalId = queryParams.get( "goalId" );

	console.log( "NewQuizPage - Component rendered with goalId:", goalId );

	return (
		<div className="container">
			<QuizForm goalId={goalId}/>
		</div>
	);
}
