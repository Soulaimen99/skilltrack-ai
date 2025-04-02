package com.sou.api;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.sou.model.LearningLog;
import com.sou.model.SummaryResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Represents a collection of functions to interact with the API endpoints.
 */
@Path( "/logs/summarize" )
@Api( description = "the Ai API" )
@javax.annotation.Generated( value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-04-02T21:26:15.796988500+02:00[Europe/Berlin]", comments = "Generator version: 7.4.0" )
public interface AiApi {
	
	/**
	 * @param learningLog
	 * @return Summary generated
	 */
	@POST
	@Consumes( { "application/json" } )
	@Produces( { "application/json" } )
	@ApiOperation( value = "Get AI summary of recent logs", notes = "", tags = { "ai" } )
	@ApiResponses( value = {
			@ApiResponse( code = 200, message = "Summary generated", response = SummaryResponse.class ) } )
	SummaryResponse summarizeLogs( @Valid @NotNull List<@Valid LearningLog> learningLog );
	
}
