import {useCallback, useState} from 'react';

/**
 * Custom hook for handling pagination
 * @param {Object} options - Pagination options
 * @param {number} options.initialPage - Initial page number (default: 0)
 * @param {number} options.initialSize - Initial page size (default: 10)
 * @returns {Object} Pagination state and handlers
 */
export default function usePagination( {
	                                       initialPage = 0,
	                                       initialSize = 10
                                       } = {} ) {
	const [page, setPage] = useState( initialPage );
	const [size, setSize] = useState( initialSize );
	const [totalItems, setTotalItems] = useState( 0 );
	const [totalPages, setTotalPages] = useState( 0 );

	const updatePaginationInfo = useCallback( ( data ) => {
		if ( data && typeof data === 'object' ) {
			if ( 'totalElements' in data ) setTotalItems( data.totalElements );
			if ( 'totalPages' in data ) setTotalPages( data.totalPages );
		}
	}, [] );

	const nextPage = useCallback( () => {
		if ( page < totalPages - 1 ) {
			setPage( prev => prev + 1 );
		}
	}, [page, totalPages] );

	const prevPage = useCallback( () => {
		if ( page > 0 ) {
			setPage( prev => prev - 1 );
		}
	}, [page] );

	const goToPage = useCallback( ( pageNumber ) => {
		if ( pageNumber >= 0 && pageNumber < totalPages ) {
			setPage( pageNumber );
		}
	}, [totalPages] );

	const changePageSize = useCallback( ( newSize ) => {
		if ( newSize > 0 ) {
			setSize( newSize );
			setPage( 0 ); // Reset to first page when changing page size
		}
	}, [] );

	const getPaginationParams = useCallback( () => {
		return {
			page,
			size
		};
	}, [page, size] );

	const addPaginationToUrl = useCallback( ( url ) => {
		const params = new URLSearchParams();
		params.append( 'page', page.toString() );
		params.append( 'size', size.toString() );

		return `${url}${url.includes( '?' ) ? '&' : '?'}${params.toString()}`;
	}, [page, size] );

	return {
		page,
		size,
		totalItems,
		totalPages,
		updatePaginationInfo,
		nextPage,
		prevPage,
		goToPage,
		changePageSize,
		getPaginationParams,
		addPaginationToUrl
	};
}
