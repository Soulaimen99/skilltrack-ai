package com.sou.service;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import com.sou.api.AiApi;
import com.sou.model.dto.LearningLogInput;
import com.sou.model.dto.SummaryResponse;
import org.slf4j.Logger;

@ApplicationScoped
@Path( "/logs/summarize" )
@Consumes( "application/json" )
@Produces( "application/json" )
public class AiApiService implements AiApi {
	
	@Inject
	Logger logger;
	
	@Override
	public SummaryResponse summarizeLogs( List<LearningLogInput> logs ) {
		logger.info( "Received request to summarize logs. Total logs: {}", logs != null ? logs.size() : 0 );
		if ( logs == null || logs.isEmpty() ) {
			logger.warn( "Empty or null logs provided for summarization." );
			throw new IllegalArgumentException( "Logs cannot be null or empty" );
		}
		
		List<String> items = logs.stream()
									 .map( LearningLogInput::getContent )
									 .map( String::trim )
									 .filter( s -> !s.isEmpty() )
									 .toList();
		logger.debug( "Filtered and extracted non-empty log contents. Items: {}", items );
		
		if ( items.isEmpty() ) {
			return SummaryResponse.builder().summary( "The logs were empty." ).build();
		}
		
		String summary;
		if ( items.size() == 1 ) {
			summary = "You worked on: " + items.getFirst() + ".";
		}
		else if ( items.size() == 2 ) {
			summary = "You worked on: " + items.get( 0 ) + " and " + items.get( 1 ) + ".";
		}
		else {
			String allButLast = String.join( ", ", items.subList( 0, items.size() - 1 ) );
			String last = items.getLast();
			summary = "You worked on: " + allButLast + ", and " + last + ".";
		}
		
		logger.info( "Summary generated successfully: {}", summary );
		return SummaryResponse.builder().summary( summary ).build();
	}
}