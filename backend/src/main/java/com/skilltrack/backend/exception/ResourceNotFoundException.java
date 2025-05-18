package com.skilltrack.backend.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested resource is not found.
 */
@ResponseStatus( HttpStatus.NOT_FOUND )
public class ResourceNotFoundException extends RuntimeException {

	private static final Logger logger = LoggerFactory.getLogger( ResourceNotFoundException.class );

	public ResourceNotFoundException( String resourceType, Object identifier ) {
		super( resourceType + " with identifier " + identifier + " not found" );
		logger.error( "{} with identifier {} not found", resourceType, identifier );
	}
}
