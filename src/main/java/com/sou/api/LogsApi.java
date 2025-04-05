package com.sou.api;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import com.sou.model.LearningLog;
import com.sou.model.LearningLogInput;

@Path( "/logs" )
@Produces( MediaType.APPLICATION_JSON )
@Consumes( MediaType.APPLICATION_JSON )
public interface LogsApi {
	
	@GET
	List<LearningLog> getLearningLogs();
	
	@POST
	LearningLog addLearningLog( @Valid @NotNull LearningLogInput input );
}
