package com.skilltrack.ai.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Exception thrown when a request is invalid.
 */
@ResponseStatus( HttpStatus.BAD_REQUEST )
public class InvalidRequestException extends RuntimeException {

	private final List<String> errors;

	public InvalidRequestException( String message ) {
		super( message );
		this.errors = new ArrayList<>();
	}

	public InvalidRequestException( String message, List<String> errors ) {
		super( message );
		this.errors = errors;
	}

	public InvalidRequestException( String message, String error ) {
		super( message );
		this.errors = new ArrayList<>();
		this.errors.add( error );
	}

	public List<String> getErrors() {
		return errors;
	}
}