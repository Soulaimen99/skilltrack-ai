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
import com.sou.repository.LearningLogRepository;

@ApplicationScoped
@Path( "/logs" )
@Consumes( "application/json" )
@Produces( "application/json" )
public class LogsApiService implements LogsApi {
	
	@Inject
	LearningLogRepository repository;
	
	@Override
	public List<LearningLog> getLearningLogs() {
		return repository.listAll();
	}
	
	@Override
	@Transactional
	public LearningLog addLearningLog( LearningLogInput input ) {
		LearningLog log = new LearningLog();
		log.setContent( input.getContent() );
		log.setTags( input.getTags() );
		log.setDate( LocalDate.parse( LocalDate.now().toString() ) );
		
		repository.persist( log );
		return log;
	}
}
