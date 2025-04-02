package com.sou.api;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.sou.model.LearningLog;
import com.sou.model.LearningLogInput;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Represents a collection of functions to interact with the API endpoints.
 */
@Path( "/logs" )
@Api( description = "the Logs API" )
@javax.annotation.Generated( value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-04-02T21:26:15.796988500+02:00[Europe/Berlin]", comments = "Generator version: 7.4.0" )
public interface LogsApi {
	
	/**
	 * @param learningLogInput
	 * @return Created log
	 */
	@POST
	@Consumes( { "application/json" } )
	@Produces( { "application/json" } )
	@ApiOperation( value = "Add a new learning log", notes = "", tags = { "logs" } )
	@ApiResponses( value = {
			@ApiResponse( code = 201, message = "Created log", response = LearningLog.class ) } )
	LearningLog addLearningLog( @Valid @NotNull LearningLogInput learningLogInput );
	
	
	/**
	 * @return A list of learning logs
	 */
	@GET
	@Produces( { "application/json" } )
	@ApiOperation( value = "Get all learning logs", notes = "", tags = { "logs" } )
	@ApiResponses( value = {
			@ApiResponse( code = 200, message = "A list of learning logs", response = LearningLog.class, responseContainer = "List" ) } )
	List<LearningLog> getLearningLogs();
	
}
