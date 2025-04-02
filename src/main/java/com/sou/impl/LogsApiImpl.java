package com.sou.impl;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Path;

import com.sou.api.LogsApi;
import com.sou.model.LearningLog;
import com.sou.model.LearningLogInput;
import org.joda.time.LocalDate;

@ApplicationScoped
@Path( "/logs" )
public class LogsApiImpl implements LogsApi {
	private static final List<LearningLog> logs = new ArrayList<>();
	private static long idCounter = 1;
	
	@Override
	public List<LearningLog> getLearningLogs() {
		return logs;
	}
	
	@Override
	public LearningLog addLearningLog( LearningLogInput input ) {
		LearningLog log = new LearningLog();
		log.setId( idCounter++ );
		log.setContent( input.getContent() );
		log.setDate( LocalDate.parse( java.time.LocalDate.now().toString() ) );
		logs.add( log );
		return log;
	}
}
