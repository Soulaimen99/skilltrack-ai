import React from 'react';

/**
 * Pagination component for navigating through paginated data
 * @param {Object} props - Component props
 * @param {number} props.currentPage - Current page number (0-based)
 * @param {number} props.totalPages - Total number of pages
 * @param {Function} props.onPageChange - Function to call when page changes
 * @param {number} props.pageSize - Current page size
 * @param {Function} props.onPageSizeChange - Function to call when page size changes
 * @param {number[]} props.pageSizeOptions - Available page size options
 * @returns {JSX.Element} Pagination component
 */
export default function Pagination( {
	                                    currentPage,
	                                    totalPages,
	                                    onPageChange,
	                                    pageSize,
	                                    onPageSizeChange,
	                                    pageSizeOptions = [10, 25, 50, 100]
                                    } ) {
	// Don't render if there's only one page or no pages
	if ( totalPages <= 1 ) return null;

	// Calculate page numbers to display
	const getPageNumbers = () => {
		const pages = [];
		const maxVisiblePages = 5;

		if ( totalPages <= maxVisiblePages ) {
			// Show all pages if there are few
			for ( let i = 0; i < totalPages; i++ ) {
				pages.push( i );
			}
		}
		else {
			// Always show first page
			pages.push( 0 );

			// Calculate middle pages
			let startPage = Math.max( 1, currentPage - 1 );
			let endPage = Math.min( totalPages - 2, currentPage + 1 );

			// Add ellipsis after first page if needed
			if ( startPage > 1 ) {
				pages.push( '...' );
			}

			// Add middle pages
			for ( let i = startPage; i <= endPage; i++ ) {
				pages.push( i );
			}

			// Add ellipsis before last page if needed
			if ( endPage < totalPages - 2 ) {
				pages.push( '...' );
			}

			// Always show last page
			pages.push( totalPages - 1 );
		}

		return pages;
	};

	return (
		<div className="pagination" role="navigation" aria-label="Pagination">
			<div className="pagination-controls">
				<button
					onClick={() => onPageChange( 0 )}
					disabled={currentPage === 0}
					aria-label="Go to first page"
					className="pagination-button"
				>
					&laquo;
				</button>

				<button
					onClick={() => onPageChange( currentPage - 1 )}
					disabled={currentPage === 0}
					aria-label="Go to previous page"
					className="pagination-button"
				>
					&lsaquo;
				</button>

				{getPageNumbers().map( ( page, index ) => (
					page === '...' ? (
						<span key={`ellipsis-${index}`} className="pagination-ellipsis">...</span>
					) : (
						<button
							key={page}
							onClick={() => onPageChange( page )}
							className={`pagination-button ${currentPage === page ? 'active' : ''}`}
							aria-current={currentPage === page ? 'page' : undefined}
							aria-label={`Page ${page + 1}`}
						>
							{page + 1}
						</button>
					)
				) )}

				<button
					onClick={() => onPageChange( currentPage + 1 )}
					disabled={currentPage === totalPages - 1}
					aria-label="Go to next page"
					className="pagination-button"
				>
					&rsaquo;
				</button>

				<button
					onClick={() => onPageChange( totalPages - 1 )}
					disabled={currentPage === totalPages - 1}
					aria-label="Go to last page"
					className="pagination-button"
				>
					&raquo;
				</button>
			</div>

			{onPageSizeChange && (
				<div className="pagination-size">
					<label>
						Items per page:
						<select
							value={pageSize}
							onChange={( e ) => onPageSizeChange( Number( e.target.value ) )}
							aria-label="Select number of items per page"
						>
							{pageSizeOptions.map( size => (
								<option key={size} value={size}>
									{size}
								</option>
							) )}
						</select>
					</label>
				</div>
			)}
		</div>
	);
}