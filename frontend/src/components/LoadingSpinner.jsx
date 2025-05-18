import React from 'react';

/**
 * Component for displaying a loading spinner
 * @param {Object} props - Component props
 * @param {string} props.size - Size of the spinner (small, medium, large)
 * @param {string} props.label - Accessible label for the spinner
 * @returns {JSX.Element} The loading spinner component
 */
export default function LoadingSpinner( { size = 'medium', label = 'Loading...' } ) {
	const sizeClass = size === 'small' ? 'spinner-sm' :
		size === 'large' ? 'spinner-lg' : '';

	return (
		<div className="loading-container" role="status" aria-live="polite">
			<span className={`spinner ${sizeClass}`} aria-hidden="true"></span>
			<span className="sr-only">{label}</span>
		</div>
	);
}