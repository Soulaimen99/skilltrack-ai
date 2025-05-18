package com.skilltrack.backend.exception;

import com.skilltrack.backend.dto.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

/**
 * Global exception handler for the application.
 * Provides consistent error responses for different types of exceptions.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler( ResponseStatusException.class )
	public ResponseEntity<ErrorResponseDto> handleResponseStatusException(
			ResponseStatusException ex, HttpServletRequest request ) {

		logger.error("ResponseStatusException: {} - {}", ex.getStatusCode(), ex.getReason());

		ErrorResponseDto errorResponse = ErrorResponseDto.builder()
				.message( ex.getReason() )
				.status( ex.getStatusCode().value() )
				.error( ex.getStatusCode().toString() )
				.path( request.getRequestURI() )
				.timestamp( LocalDateTime.now() )
				.build();

		return new ResponseEntity<>( errorResponse, ex.getStatusCode() );
	}

	@ExceptionHandler( AccessDeniedException.class )
	public ResponseEntity<ErrorResponseDto> handleAccessDeniedException(
			AccessDeniedException ex, HttpServletRequest request ) {

		logger.error("Access denied: {} - {}", request.getRequestURI(), ex.getMessage());

		ErrorResponseDto errorResponse = ErrorResponseDto.builder()
				.message( "Access denied: " + ex.getMessage() )
				.status( HttpStatus.FORBIDDEN.value() )
				.error( HttpStatus.FORBIDDEN.toString() )
				.path( request.getRequestURI() )
				.timestamp( LocalDateTime.now() )
				.build();

		return new ResponseEntity<>( errorResponse, HttpStatus.FORBIDDEN );
	}

	@ExceptionHandler( IllegalArgumentException.class )
	public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(
			IllegalArgumentException ex, HttpServletRequest request ) {

		logger.error("Illegal argument: {} - {}", request.getRequestURI(), ex.getMessage());

		ErrorResponseDto errorResponse = ErrorResponseDto.builder()
				.message( ex.getMessage() )
				.status( HttpStatus.BAD_REQUEST.value() )
				.error( HttpStatus.BAD_REQUEST.toString() )
				.path( request.getRequestURI() )
				.timestamp( LocalDateTime.now() )
				.build();

		return new ResponseEntity<>( errorResponse, HttpStatus.BAD_REQUEST );
	}

	@ExceptionHandler( DataIntegrityViolationException.class )
	public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolationException(
			DataIntegrityViolationException ex, HttpServletRequest request ) {

		logger.error("Data integrity violation: {} - {}", request.getRequestURI(), ex.getMostSpecificCause().getMessage());

		ErrorResponseDto errorResponse = ErrorResponseDto.builder()
				.message( "Data integrity violation: " + ex.getMostSpecificCause().getMessage() )
				.status( HttpStatus.CONFLICT.value() )
				.error( HttpStatus.CONFLICT.toString() )
				.path( request.getRequestURI() )
				.timestamp( LocalDateTime.now() )
				.build();

		return new ResponseEntity<>( errorResponse, HttpStatus.CONFLICT );
	}

	@ExceptionHandler( ResourceNotFoundException.class )
	public ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(
			ResourceNotFoundException ex, HttpServletRequest request ) {

		// Additional logging with request context
		logger.debug("Resource not found: {} - {}", request.getRequestURI(), ex.getMessage());

		ErrorResponseDto errorResponse = ErrorResponseDto.builder()
				.message( ex.getMessage() )
				.status( HttpStatus.NOT_FOUND.value() )
				.error( HttpStatus.NOT_FOUND.toString() )
				.path( request.getRequestURI() )
				.timestamp( LocalDateTime.now() )
				.build();

		return new ResponseEntity<>( errorResponse, HttpStatus.NOT_FOUND );
	}

	@ExceptionHandler( ResourceAlreadyExistsException.class )
	public ResponseEntity<ErrorResponseDto> handleResourceAlreadyExistsException(
			ResourceAlreadyExistsException ex, HttpServletRequest request ) {

		// Additional logging with request context
		logger.debug("Resource already exists: {} - {}", request.getRequestURI(), ex.getMessage());

		ErrorResponseDto errorResponse = ErrorResponseDto.builder()
				.message( ex.getMessage() )
				.status( HttpStatus.CONFLICT.value() )
				.error( HttpStatus.CONFLICT.toString() )
				.path( request.getRequestURI() )
				.timestamp( LocalDateTime.now() )
				.build();

		return new ResponseEntity<>( errorResponse, HttpStatus.CONFLICT );
	}

	@ExceptionHandler( InvalidRequestException.class )
	public ResponseEntity<ErrorResponseDto> handleInvalidRequestException(
			InvalidRequestException ex, HttpServletRequest request ) {

		// Additional logging with request context
		logger.debug("Invalid request: {} - {} - Errors: {}", request.getRequestURI(), ex.getMessage(), ex.getErrors());

		ErrorResponseDto errorResponse = ErrorResponseDto.builder()
				.message( ex.getMessage() )
				.status( HttpStatus.BAD_REQUEST.value() )
				.error( HttpStatus.BAD_REQUEST.toString() )
				.path( request.getRequestURI() )
				.timestamp( LocalDateTime.now() )
				.details( ex.getErrors() )
				.build();

		return new ResponseEntity<>( errorResponse, HttpStatus.BAD_REQUEST );
	}

	@ExceptionHandler( RuntimeException.class )
	public ResponseEntity<ErrorResponseDto> handleRuntimeException(
			RuntimeException ex, HttpServletRequest request ) {

		logger.error("Runtime exception: {} - {}", request.getRequestURI(), ex.getMessage(), ex);

		ErrorResponseDto errorResponse = ErrorResponseDto.builder()
				.message( ex.getMessage() )
				.status( HttpStatus.INTERNAL_SERVER_ERROR.value() )
				.error( HttpStatus.INTERNAL_SERVER_ERROR.toString() )
				.path( request.getRequestURI() )
				.timestamp( LocalDateTime.now() )
				.build();

		return new ResponseEntity<>( errorResponse, HttpStatus.INTERNAL_SERVER_ERROR );
	}

	@ExceptionHandler( Exception.class )
	public ResponseEntity<ErrorResponseDto> handleGenericException(
			Exception ex, HttpServletRequest request ) {

		logger.error("Unexpected exception: {} - {}", request.getRequestURI(), ex.getMessage(), ex);

		ErrorResponseDto errorResponse = ErrorResponseDto.builder()
				.message( "An unexpected error occurred: " + ex.getMessage() )
				.status( HttpStatus.INTERNAL_SERVER_ERROR.value() )
				.error( HttpStatus.INTERNAL_SERVER_ERROR.toString() )
				.path( request.getRequestURI() )
				.timestamp( LocalDateTime.now() )
				.build();

		return new ResponseEntity<>( errorResponse, HttpStatus.INTERNAL_SERVER_ERROR );
	}
}
