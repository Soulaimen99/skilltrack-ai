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
@Consumes("application/json")
@Produces("application/json")
public class AiApiService implements AiApi {
	@Override
	public SummaryResponse  summarizeLogs( List<LearningLog> logs ) {
		String summary = "Summary (" + logs.size() + " logs): " + logs.stream()
																		  .map( LearningLog::getContent )
																		  .reduce( "", ( a, b ) -> a + " " + b );
		SummaryResponse res = new SummaryResponse();
		res.setSummary( summary.trim() );
		return res;
	}
}
