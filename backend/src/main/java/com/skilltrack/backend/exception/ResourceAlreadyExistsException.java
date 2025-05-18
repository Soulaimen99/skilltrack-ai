package com.skilltrack.backend.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when attempting to create a resource that already exists.
 */
@ResponseStatus( HttpStatus.CONFLICT )
public class ResourceAlreadyExistsException extends RuntimeException {

	private static final Logger logger = LoggerFactory.getLogger( ResourceAlreadyExistsException.class );

	public ResourceAlreadyExistsException( String resourceType, Object identifier ) {
		super( resourceType + " with identifier " + identifier + " already exists" );
		logger.error( "{} with identifier {} already exists", resourceType, identifier );
	}
}
