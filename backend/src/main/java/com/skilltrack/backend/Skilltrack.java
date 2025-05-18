package com.skilltrack.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Skilltrack {

	private static final Logger logger = LoggerFactory.getLogger( Skilltrack.class );

	public static void main( String[] args ) {
		logger.info( "Starting SkillTrack application" );
		SpringApplication.run( Skilltrack.class, args );
		logger.info( "SkillTrack application started successfully" );
	}
}
