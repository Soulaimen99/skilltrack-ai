import { useState, useEffect } from "react";
import LoadingSpinner from "./LoadingSpinner";

/**
 * Component for displaying and answering a quiz question
 * @param {Object} props - Component props
 * @param {Object} props.question - Question object
 * @param {Function} props.onSubmitAnswer - Function to call when submitting an answer
 * @param {boolean} props.loading - Loading state
 * @param {boolean} props.readOnly - Whether the question is in read-only mode
 * @returns {JSX.Element} QuizQuestion component
 */
export default function QuizQuestion({ 
  question, 
  onSubmitAnswer, 
  loading = false,
  readOnly = false
}) {
  const [answer, setAnswer] = useState("");
  const [options, setOptions] = useState([]);

  // Parse options for MCQ questions
  useEffect(() => {
    if (question?.type === "MCQ" && question.options) {
      try {
        // Options might be stored as a JSON string or array
        const parsedOptions = typeof question.options === 'string' 
          ? JSON.parse(question.options) 
          : question.options;
        setOptions(Array.isArray(parsedOptions) ? parsedOptions : []);
      } catch (e) {
        console.error("Error parsing question options:", e);
        setOptions([]);
      }
    }
  }, [question]);

  // Set initial answer if in read-only mode
  useEffect(() => {
    if (readOnly && question?.answer?.answer) {
      setAnswer(question.answer.answer);
    } else {
      setAnswer("");
    }
  }, [question, readOnly]);

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!loading && answer.trim() && onSubmitAnswer) {
      onSubmitAnswer({
        questionId: question.id,
        answer: answer.trim()
      });
    }
  };

  if (!question) {
    return <p>No question available.</p>;
  }

  return (
    <div className={`quiz-question ${question.type.toLowerCase()}`}>
      <div className="question-header">
        <h3>{question.question}</h3>
        {question.type !== "TEXT" && (
          <span className="question-type">{question.type === "MCQ" ? "Multiple Choice" : "True/False"}</span>
        )}
      </div>

      <form onSubmit={handleSubmit}>
        {question.type === "MCQ" && (
          <div className="question-options">
            {options.map((option, index) => (
              <div key={index} className="option">
                <input
                  type="radio"
                  id={`option-${index}`}
                  name="mcq-answer"
                  value={option}
                  checked={answer === option}
                  onChange={(e) => setAnswer(e.target.value)}
                  disabled={loading || readOnly}
                />
                <label htmlFor={`option-${index}`}>{option}</label>
              </div>
            ))}
          </div>
        )}

        {question.type === "BOOLEAN" && (
          <div className="question-options boolean">
            <div className="option">
              <input
                type="radio"
                id="option-true"
                name="boolean-answer"
                value="true"
                checked={answer === "true"}
                onChange={(e) => setAnswer(e.target.value)}
                disabled={loading || readOnly}
              />
              <label htmlFor="option-true">True</label>
            </div>
            <div className="option">
              <input
                type="radio"
                id="option-false"
                name="boolean-answer"
                value="false"
                checked={answer === "false"}
                onChange={(e) => setAnswer(e.target.value)}
                disabled={loading || readOnly}
              />
              <label htmlFor="option-false">False</label>
            </div>
          </div>
        )}

        {question.type === "TEXT" && (
          <textarea
            value={answer}
            onChange={(e) => setAnswer(e.target.value)}
            placeholder="Type your answer here..."
            disabled={loading || readOnly}
            rows={4}
          />
        )}

        {readOnly && question.answer && (
          <div className={`answer-feedback ${question.answer.correct ? "correct" : "incorrect"}`}>
            <p>
              <strong>Your answer:</strong> {question.answer.answer}
            </p>
            <p>
              <strong>Correct answer:</strong> {question.correctAnswer}
            </p>
            <p>
              <strong>Result:</strong> {question.answer.correct ? "Correct" : "Incorrect"}
            </p>
          </div>
        )}

        {!readOnly && (
          <button 
            type="submit" 
            disabled={!answer.trim() || loading}
            className="submit-answer"
          >
            {loading ? <LoadingSpinner small label="Submitting..." /> : "Submit Answer"}
          </button>
        )}
      </form>
    </div>
  );
}