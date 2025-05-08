import { useState, useCallback } from 'react';

/**
 * Custom hook for handling pagination
 * @param {Object} options - Pagination options
 * @param {number} options.initialPage - Initial page number (default: 0)
 * @param {number} options.initialSize - Initial page size (default: 10)
 * @returns {Object} Pagination state and handlers
 */
export default function usePagination({ 
  initialPage = 0, 
  initialSize = 10 
} = {}) {
  const [page, setPage] = useState(initialPage);
  const [size, setSize] = useState(initialSize);
  const [totalItems, setTotalItems] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  /**
   * Update pagination information based on API response
   * @param {Object} data - API response with pagination info
   */
  const updatePaginationInfo = useCallback((data) => {
    if (data && typeof data === 'object') {
      if ('totalElements' in data) setTotalItems(data.totalElements);
      if ('totalPages' in data) setTotalPages(data.totalPages);
    }
  }, []);

  /**
   * Go to next page if available
   */
  const nextPage = useCallback(() => {
    if (page < totalPages - 1) {
      setPage(prev => prev + 1);
    }
  }, [page, totalPages]);

  /**
   * Go to previous page if available
   */
  const prevPage = useCallback(() => {
    if (page > 0) {
      setPage(prev => prev - 1);
    }
  }, [page]);

  /**
   * Go to a specific page
   * @param {number} pageNumber - Page number to go to
   */
  const goToPage = useCallback((pageNumber) => {
    if (pageNumber >= 0 && pageNumber < totalPages) {
      setPage(pageNumber);
    }
  }, [totalPages]);

  /**
   * Change page size
   * @param {number} newSize - New page size
   */
  const changePageSize = useCallback((newSize) => {
    if (newSize > 0) {
      setSize(newSize);
      setPage(0); // Reset to first page when changing page size
    }
  }, []);

  /**
   * Get pagination parameters for API requests
   * @returns {Object} Pagination parameters
   */
  const getPaginationParams = useCallback(() => {
    return {
      page,
      size
    };
  }, [page, size]);

  /**
   * Add pagination parameters to a URL
   * @param {string} url - Base URL
   * @returns {string} URL with pagination parameters
   */
  const addPaginationToUrl = useCallback((url) => {
    const params = new URLSearchParams();
    params.append('page', page.toString());
    params.append('size', size.toString());
    
    return `${url}${url.includes('?') ? '&' : '?'}${params.toString()}`;
  }, [page, size]);

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