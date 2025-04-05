package com.sou.service;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import com.sou.api.AiApi;
import com.sou.model.LearningLog;
import com.sou.model.SummaryResponse;

@ApplicationScoped
@Path( "/logs/summarize" )
@Consumes( "application/json" )
@Produces( "application/json" )
public class AiApiService implements AiApi {
	@Override
	public SummaryResponse summarizeLogs( List<LearningLog> logs ) {
		SummaryResponse res = new SummaryResponse();
		
		if ( logs == null || logs.isEmpty() ) {
			res.setSummary( "No logs to summarize." );
			return res;
		}
		
		// Extract content and trim
		List<String> items = logs.stream()
									 .map( LearningLog::getContent )
									 .map( String::trim )
									 .filter( s -> !s.isEmpty() )
									 .toList();
		
		if ( items.isEmpty() ) {
			res.setSummary( "The logs were empty." );
			return res;
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
		
		res.setSummary( summary );
		return res;
	}
	
}
