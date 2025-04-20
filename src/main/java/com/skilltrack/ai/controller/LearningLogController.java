package com.skilltrack.ai.controller;

import com.skilltrack.ai.dto.LearningLogDto;
import com.skilltrack.ai.entity.LearningLog;
import com.skilltrack.ai.entity.User;
import com.skilltrack.ai.service.LearningLogService;
import com.skilltrack.ai.service.SummaryService;
import com.skilltrack.ai.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping( "/logs" )
@SecurityRequirement( name = "bearerAuth" )
public class LearningLogController {

	private final LearningLogService learningLogService;

	private final UserService userService;

	private final SummaryService summaryService;

	public LearningLogController( LearningLogService learningLogService, UserService userService, SummaryService summaryService ) {
		this.learningLogService = learningLogService;
		this.userService = userService;
		this.summaryService = summaryService;
	}


	private User getUser( Authentication auth ) {
		if ( auth instanceof JwtAuthenticationToken jwt ) {
			String username = jwt.getToken().getClaimAsString( "preferred_username" );
			String email = jwt.getToken().getClaimAsString( "email" );
			return userService.get( username, email );
		}
		return null;
	}

	@GetMapping
	public ResponseEntity<LearningLogDto.PagedLogsResponse> getLogs( @RequestParam( required = false ) String from,
	                                                                 @RequestParam( required = false ) String to,
	                                                                 @RequestParam( defaultValue = "0" ) int page,
	                                                                 @RequestParam( defaultValue = "10" ) int size,
	                                                                 Authentication auth ) {
		User user = getUser( auth );
		return ResponseEntity.ok( learningLogService.getPagedLogsResponse( from, to, page, size, user ) );
	}

	@PostMapping
	public ResponseEntity<LearningLogDto> createLog( @RequestBody LearningLogDto logDto, Authentication auth ) {
		User user = getUser( auth );
		LearningLog created = learningLogService.addLog( logDto.toEntity( user ) );
		return ResponseEntity.status( HttpStatus.CREATED )
				.body( LearningLogDto.from( learningLogService.addLog( created ) ) );
	}

	@PostMapping( "/{id}" )
	public ResponseEntity<LearningLogDto> updateLog( @PathVariable UUID id, @RequestBody LearningLogDto logDto, Authentication auth ) {
		User user = getUser( auth );
		LearningLog updated = learningLogService.editLog( id, logDto.toEntity( user ) );
		return ResponseEntity.status( HttpStatus.CREATED )
				.body( LearningLogDto.from( learningLogService.addLog( updated ) ) );
	}

	@DeleteMapping( "/{id}" )
	public ResponseEntity<Void> deleteLog( @PathVariable UUID id, Authentication auth ) {
		return learningLogService.deleteLog( getUser( auth ), id ) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
	}

	@PostMapping( "/summarize" )
	public ResponseEntity<Map<String, String>> summarize( @RequestBody List<LearningLogDto> logs, Authentication auth ) {
		User user = getUser( auth );
		List<String> content = logs.stream().map( LearningLogDto::content ).filter( s -> s != null && !s.isBlank() ).toList();
		SummaryService.SummaryResult result = summaryService.summarizeWithLimitCheck( user, content );
		return ResponseEntity.ok().header( "X-RateLimit-Remaining", String.valueOf( result.remaining() ) )
				.body( Map.of( "summary", result.summary() ) );
	}
}
