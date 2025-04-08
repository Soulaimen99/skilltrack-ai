package com.sou.api;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import com.sou.model.dto.LearningLogInput;
import com.sou.model.dto.LearningLogOutput;

@Path( "/logs" )
@Produces( MediaType.APPLICATION_JSON )
@Consumes( MediaType.APPLICATION_JSON )
@RolesAllowed( { "user", "admin" } )
public interface LogsApi {
	
	@GET
	List<LearningLogOutput> getLearningLogs();
	
	@POST
	LearningLogOutput addLearningLog( @Valid @NotNull LearningLogInput input );
	
}
