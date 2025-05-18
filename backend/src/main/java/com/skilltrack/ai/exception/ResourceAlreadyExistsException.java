package com.skilltrack.ai.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when attempting to create a resource that already exists.
 */
@ResponseStatus( HttpStatus.CONFLICT )
public class ResourceAlreadyExistsException extends RuntimeException {

	public ResourceAlreadyExistsException( String message ) {
		super( message );
	}

	public ResourceAlreadyExistsException( String resourceType, String identifier ) {
		super( resourceType + " with identifier " + identifier + " already exists" );
	}

	public ResourceAlreadyExistsException( String resourceType, Object identifier ) {
		super( resourceType + " with identifier " + identifier + " already exists" );
	}
}