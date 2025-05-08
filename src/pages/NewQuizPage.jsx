import { useLocation } from "react-router-dom";
import QuizForm from "../components/QuizForm";

export default function NewQuizPage() {
  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const goalId = queryParams.get("goalId");

  return (
    <div className="container">
      <QuizForm goalId={goalId} />
    </div>
  );
}