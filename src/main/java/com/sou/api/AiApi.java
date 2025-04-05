package com.sou.api;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import com.sou.model.LearningLog;
import com.sou.model.SummaryResponse;

@Path( "/logs/summarize" )
@Produces( MediaType.APPLICATION_JSON )
@Consumes( MediaType.APPLICATION_JSON )
public interface AiApi {
	
	@POST
	SummaryResponse summarizeLogs( @Valid List<@Valid LearningLog> learningLogs );
}
