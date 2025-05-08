import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import useFetch from "../hooks/useFetch";
import QuizQuestion from "../components/QuizQuestion";
import LoadingSpinner from "../components/LoadingSpinner";
import ErrorMessage from "../components/ErrorMessage";

export default function QuizPage() {
  const { quizId } = useParams();
  const navigate = useNavigate();
  const { get, post, put, loading, error } = useFetch();
  
  const [quiz, setQuiz] = useState(null);
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
  const [submittingAnswer, setSubmittingAnswer] = useState(false);
  const [completingQuiz, setCompletingQuiz] = useState(false);

  // Fetch quiz data
  useEffect(() => {
    const fetchQuiz = async () => {
      try {
        const data = await get(`/api/quizzes/${quizId}`);
        setQuiz(data);
        
        // Find the first unanswered question
        if (data.questions && data.questions.length > 0) {
          const firstUnansweredIndex = data.questions.findIndex(q => !q.answer);
          setCurrentQuestionIndex(firstUnansweredIndex >= 0 ? firstUnansweredIndex : 0);
        }
      } catch {
        // Error is handled by useFetch
      }
    };

    if (quizId) {
      fetchQuiz();
    }
  }, [quizId, get]);

  // Handle submitting an answer
  const handleSubmitAnswer = async (answerData) => {
    if (!quiz || submittingAnswer) return;
    
    setSubmittingAnswer(true);
    try {
      const updatedQuiz = await post(
        `/api/quizzes/${quizId}/questions/${answerData.questionId}/answer`,
        { answer: answerData.answer }
      );
      setQuiz(updatedQuiz);
      
      // Move to the next unanswered question
      if (updatedQuiz.questions) {
        const nextUnansweredIndex = updatedQuiz.questions.findIndex(
          (q, index) => index > currentQuestionIndex && !q.answer
        );
        
        if (nextUnansweredIndex >= 0) {
          setCurrentQuestionIndex(nextUnansweredIndex);
        }
      }
    } catch {
      // Error is handled by useFetch
    } finally {
      setSubmittingAnswer(false);
    }
  };

  // Handle completing the quiz
  const handleCompleteQuiz = async () => {
    if (!quiz || completingQuiz) return;
    
    setCompletingQuiz(true);
    try {
      const updatedQuiz = await put(`/api/quizzes/${quizId}/complete`);
      setQuiz(updatedQuiz);
    } catch {
      // Error is handled by useFetch
    } finally {
      setCompletingQuiz(false);
    }
  };

  // Get current question
  const currentQuestion = quiz?.questions?.[currentQuestionIndex] || null;
  
  // Check if all questions are answered
  const allQuestionsAnswered = quiz?.questions?.every(q => q.answer) || false;
  
  // Calculate progress
  const answeredCount = quiz?.questions?.filter(q => q.answer).length || 0;
  const totalQuestions = quiz?.questions?.length || 0;
  const progressPercentage = totalQuestions > 0 ? (answeredCount / totalQuestions) * 100 : 0;

  if (loading && !quiz) {
    return <LoadingSpinner label="Loading quiz..." />;
  }

  if (error) {
    return <ErrorMessage message={error} />;
  }

  if (!quiz) {
    return <p>Quiz not found.</p>;
  }

  return (
    <div className="container">
      <div className="quiz-header">
        <h2>{quiz.goalTitle} Quiz</h2>
        <button 
          className="back-button"
          onClick={() => navigate("/quizzes")}
        >
          Back to Quizzes
        </button>
      </div>

      {quiz.completed ? (
        <div className="quiz-completed">
          <div className="quiz-summary">
            <h3>Quiz Completed</h3>
            <p className="quiz-score">Score: {quiz.score}</p>
            <p className="quiz-feedback">{quiz.feedback}</p>
            <p>
              Completed on: {new Date(quiz.endedAt).toLocaleString()}
            </p>
            {quiz.duration > 0 && (
              <p>
                Duration: {Math.floor(quiz.duration / 60)} min {quiz.duration % 60} sec
              </p>
            )}
          </div>

          <div className="quiz-questions-review">
            <h3>Questions Review</h3>
            {quiz.questions.map((question, index) => (
              <div key={question.id} className="quiz-question-review">
                <h4>Question {index + 1}</h4>
                <QuizQuestion
                  question={question}
                  readOnly={true}
                />
              </div>
            ))}
          </div>
        </div>
      ) : (
        <div className="quiz-in-progress">
          <div className="quiz-progress">
            <div className="progress-bar">
              <div 
                className="progress-fill" 
                style={{ width: `${progressPercentage}%` }}
              ></div>
            </div>
            <span className="progress-text">
              {answeredCount} of {totalQuestions} questions answered
            </span>
          </div>

          {currentQuestion ? (
            <div className="current-question">
              <h3>Question {currentQuestionIndex + 1}</h3>
              <QuizQuestion
                question={currentQuestion}
                onSubmitAnswer={handleSubmitAnswer}
                loading={submittingAnswer}
                readOnly={!!currentQuestion.answer}
              />
              
              <div className="question-navigation">
                <button
                  onClick={() => setCurrentQuestionIndex(Math.max(0, currentQuestionIndex - 1))}
                  disabled={currentQuestionIndex === 0}
                  className="nav-button"
                >
                  Previous
                </button>
                <button
                  onClick={() => setCurrentQuestionIndex(Math.min(totalQuestions - 1, currentQuestionIndex + 1))}
                  disabled={currentQuestionIndex === totalQuestions - 1}
                  className="nav-button"
                >
                  Next
                </button>
              </div>
            </div>
          ) : (
            <p>No questions available.</p>
          )}

          {allQuestionsAnswered && !quiz.completed && (
            <div className="complete-quiz">
              <button
                onClick={handleCompleteQuiz}
                disabled={completingQuiz}
                className="primary-button"
              >
                {completingQuiz ? <LoadingSpinner small label="Completing..." /> : "Complete Quiz"}
              </button>
            </div>
          )}
        </div>
      )}
    </div>
  );
}