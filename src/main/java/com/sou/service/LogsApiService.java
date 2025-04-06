package com.sou.service;

import java.time.LocalDate;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import com.sou.api.LogsApi;
import com.sou.model.LearningLog;
import com.sou.model.LearningLogInput;
import com.sou.model.User;
import com.sou.repository.LearningLogRepository;
import com.sou.repository.UserRepository;
import io.quarkus.security.identity.SecurityIdentity;
import org.slf4j.Logger;

@ApplicationScoped
@Path( "/logs" )
@Consumes( "application/json" )
@Produces( "application/json" )
public class LogsApiService implements LogsApi {
	
	@Inject
	Logger logger;
	
	@Inject
	LearningLogRepository learningLogRepository;
	
	@Inject
	UserRepository userRepository;
	
	@Inject
	SecurityIdentity identity;
	
	@Override
	public List<LearningLog> getLearningLogs() {
		String username = identity.getPrincipal().getName();
		logger.info( "Fetching learning logs for user: {}", username );
		
		User user = userRepository.findByUsername( username );
		if ( user == null ) {
			logger.warn( "User not found for username: {}", username );
			return List.of();
		}
		
		List<LearningLog> logs = learningLogRepository.retrieveUserLogs( user.id );
		logger.debug( "Retrieved {} logs for user: {}", logs.size(), username );
		
		return logs;
	}
	
	@Override
	@Transactional
	public LearningLog addLearningLog( LearningLogInput input ) {
		if ( input == null || input.getContent() == null || input.getContent().isBlank() ) {
			logger.error( "Invalid log input: content is null or blank" );
			throw new IllegalArgumentException( "Content must not be null or blank" );
		}
		
		String username = identity.getPrincipal().getName();
		logger.info( "Adding new log for user: {}. Content: {}", username, input.getContent() );
		User user = userRepository.findByUsername( username );
		if ( user == null ) {
			logger.error( "Failed to add log. User not found: {}", username );
			throw new IllegalStateException( "User not found" );
		}
		
		LearningLog log = new LearningLog(
				user,
				input.getContent(),
				input.getTags(),
				LocalDate.now()
		);
		
		logger.debug( "Persisting new log entry: {}", log );
		learningLogRepository.persist( log );
		
		logger.info( "Log added successfully for user: {}", username );
		return log;
	}
}
