import React from 'react';

/**
 * Component for displaying error messages
 * @param {Object} props - Component props
 * @param {string} props.message - The error message to display
 * @param {Function} props.onDismiss - Optional callback for dismissing the error
 * @returns {JSX.Element} The error message component
 */
export default function ErrorMessage({ message, onDismiss }) {
  if (!message) return null;
  
  return (
    <div className="error-message" role="alert">
      <p>{message}</p>
      {onDismiss && (
        <button 
          onClick={onDismiss} 
          className="error-dismiss" 
          aria-label="Dismiss error message"
        >
          ✕
        </button>
      )}
    </div>
  );
}