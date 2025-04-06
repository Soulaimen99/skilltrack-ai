package com.sou.util;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {
	
	private static final Logger logger = LoggerFactory.getLogger( IllegalArgumentExceptionMapper.class );
	
	@Override
	public Response toResponse( IllegalArgumentException exception ) {
		logger.error( "Validation error: {}", exception.getMessage() );
		return Response.status( Response.Status.BAD_REQUEST )
					   .entity( exception.getMessage() )
					   .build();
	}
}