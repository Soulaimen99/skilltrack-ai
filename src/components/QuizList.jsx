import { useState } from "react";
import { Link } from "react-router-dom";
import LoadingSpinner from "./LoadingSpinner";
import ErrorMessage from "./ErrorMessage";

/**
 * Component for displaying a list of quizzes
 * @param {Object} props - Component props
 * @param {Array} props.quizzes - Array of quiz objects
 * @param {boolean} props.loading - Loading state
 * @param {string} props.error - Error message
 * @param {Function} props.onCreateQuiz - Function to call when creating a new quiz
 * @param {boolean} props.showGoalInfo - Whether to show goal information
 * @returns {JSX.Element} QuizList component
 */
export default function QuizList({ 
  quizzes = [], 
  loading = false, 
  error = null,
  onCreateQuiz,
  showGoalInfo = true
}) {
  const [expandedQuizId, setExpandedQuizId] = useState(null);

  const toggleExpand = (quizId) => {
    setExpandedQuizId(expandedQuizId === quizId ? null : quizId);
  };

  if (loading) {
    return <LoadingSpinner label="Loading quizzes..." />;
  }

  if (error) {
    return <ErrorMessage message={error} />;
  }

  if (quizzes.length === 0) {
    return (
      <div className="empty-state">
        <p>No quizzes found.</p>
        {onCreateQuiz && (
          <button onClick={onCreateQuiz} className="primary-button">
            Create New Quiz
          </button>
        )}
      </div>
    );
  }

  return (
    <div className="quiz-list">
      {onCreateQuiz && (
        <div className="quiz-list-header">
          <button onClick={onCreateQuiz} className="primary-button">
            Create New Quiz
          </button>
        </div>
      )}
      <ul>
        {quizzes.map((quiz) => (
          <li key={quiz.id} className={`quiz-item ${quiz.completed ? "completed" : "in-progress"}`}>
            <div className="quiz-item-header" onClick={() => toggleExpand(quiz.id)}>
              <div className="quiz-item-title">
                <span className="quiz-status-indicator"></span>
                {showGoalInfo && <span className="quiz-goal">{quiz.goalTitle}</span>}
                <span className="quiz-date">
                  {new Date(quiz.startedAt).toLocaleDateString()}
                </span>
              </div>
              <div className="quiz-item-stats">
                <span className="quiz-score">Score: {quiz.score}</span>
                <span className="quiz-expand-icon">
                  {expandedQuizId === quiz.id ? "▼" : "►"}
                </span>
              </div>
            </div>
            
            {expandedQuizId === quiz.id && (
              <div className="quiz-item-details">
                <div className="quiz-item-info">
                  <p>
                    <strong>Status:</strong> {quiz.completed ? "Completed" : "In Progress"}
                  </p>
                  <p>
                    <strong>Started:</strong> {new Date(quiz.startedAt).toLocaleString()}
                  </p>
                  {quiz.endedAt && (
                    <p>
                      <strong>Completed:</strong> {new Date(quiz.endedAt).toLocaleString()}
                    </p>
                  )}
                  {quiz.duration > 0 && (
                    <p>
                      <strong>Duration:</strong> {Math.floor(quiz.duration / 60)} min {quiz.duration % 60} sec
                    </p>
                  )}
                  {quiz.feedback && (
                    <p className="quiz-feedback">
                      <strong>Feedback:</strong> {quiz.feedback}
                    </p>
                  )}
                </div>
                <div className="quiz-item-actions">
                  <Link to={`/quizzes/${quiz.id}`} className="button">
                    {quiz.completed ? "Review Quiz" : "Continue Quiz"}
                  </Link>
                </div>
              </div>
            )}
          </li>
        ))}
      </ul>
    </div>
  );
}