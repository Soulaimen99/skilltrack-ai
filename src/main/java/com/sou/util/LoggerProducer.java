package com.sou.util;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class LoggerProducer {
	
	@Produces
	public Logger produceLogger( InjectionPoint injectionPoint ) {
		return LoggerFactory.getLogger( injectionPoint.getMember().getDeclaringClass().getName() );
	}
}