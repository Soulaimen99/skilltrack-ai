package com.skilltrack.ai.exception;

import com.skilltrack.ai.dto.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
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

	@ExceptionHandler( ResponseStatusException.class )
	public ResponseEntity<ErrorResponseDto> handleResponseStatusException(
			ResponseStatusException ex, HttpServletRequest request ) {

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
