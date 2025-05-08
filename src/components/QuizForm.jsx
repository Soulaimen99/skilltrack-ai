import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import useFetch from "../hooks/useFetch";
import ErrorMessage from "./ErrorMessage";
import LoadingSpinner from "./LoadingSpinner";

/**
 * Component for creating a new quiz
 * @param {Object} props - Component props
 * @param {string} props.goalId - Goal ID (optional, if provided will pre-select the goal)
 * @returns {JSX.Element} QuizForm component
 */
export default function QuizForm({ goalId }) {
  const navigate = useNavigate();
  const { get, post, loading, error } = useFetch();

  const [goals, setGoals] = useState([]);
  const [selectedGoalId, setSelectedGoalId] = useState(goalId || "");
  const [loadingGoals, setLoadingGoals] = useState(false);
  const [goalsError, setGoalsError] = useState(null);

  // Fetch goals when component mounts
  useEffect(() => {
    const fetchGoals = async () => {
      setLoadingGoals(true);
      setGoalsError(null);
      try {
        const response = await get("/api/goals");
        setGoals(response.content || []);
      } catch {
        setGoalsError("Failed to load goals. Please try again.");
      } finally {
        setLoadingGoals(false);
      }
    };

    fetchGoals();
  }, [get]);

  // Set selected goal ID if provided as prop
  useEffect(() => {
    if (goalId) {
      setSelectedGoalId(goalId);
    }
  }, [goalId]);

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!selectedGoalId) {
      return;
    }

    try {
      const quiz = await post(`/api/quizzes/goal/${selectedGoalId}`);
      // Navigate to the quiz page
      navigate(`/quizzes/${quiz.id}`);
    } catch {
      // Error is handled by useFetch
    }
  };

  return (
    <div className="quiz-form">
      <h2>Create New Quiz</h2>

      {(error || goalsError) && (
        <ErrorMessage message={error || goalsError} />
      )}

      {loadingGoals ? (
        <LoadingSpinner label="Loading goals..." />
      ) : (
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="goal-select">Select Learning Goal:</label>
            <select
              id="goal-select"
              value={selectedGoalId}
              onChange={(e) => setSelectedGoalId(e.target.value)}
              required
              disabled={loading || goals.length === 0}
            >
              <option value="">-- Select a Goal --</option>
              {goals.map((goal) => (
                <option key={goal.id} value={goal.id}>
                  {goal.title}
                </option>
              ))}
            </select>
          </div>

          <div className="form-actions">
            <button
              type="button"
              onClick={() => navigate(-1)}
              disabled={loading}
              className="secondary-button"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={!selectedGoalId || loading}
              className="primary-button"
            >
              {loading ? <LoadingSpinner small label="Creating..." /> : "Create Quiz"}
            </button>
          </div>
        </form>
      )}
    </div>
  );
}
