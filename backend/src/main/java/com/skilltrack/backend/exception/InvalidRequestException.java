package com.skilltrack.backend.exception;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Exception thrown when a request is invalid.
 */
@Getter
@ResponseStatus( HttpStatus.BAD_REQUEST )
public class InvalidRequestException extends RuntimeException {

	private static final Logger logger = LoggerFactory.getLogger( InvalidRequestException.class );

	private final List<String> errors;

	public InvalidRequestException( String message, String error ) {
		super( message );
		this.errors = new ArrayList<>();
		this.errors.add( error );
		logger.error( "Invalid request: {} - Error: {}", message, error );
	}
}
