package com.skilltrack.ai.controller;

import com.skilltrack.ai.dto.LearningLogDto;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping( "/logs" )
@SecurityRequirement( name = "bearerAuth" )
public class LearningLogController {

	private final LearningLogService logService;

	private final UserService userService;

	private final SummaryService summaryService;

	public LearningLogController( LearningLogService logService, UserService userService, SummaryService summaryService ) {
		this.logService = logService;
		this.userService = userService;
		this.summaryService = summaryService;
	}

	private User getUser( Authentication auth ) {
		if ( auth instanceof JwtAuthenticationToken jwt ) {
			String username = jwt.getToken().getClaimAsString( "preferred_username" );
			String email = jwt.getToken().getClaimAsString( "email" );
			return userService.getOrCreate( username, email );
		}
		return userService.getOrCreate( auth.getName(), null );
	}

	@GetMapping
	public ResponseEntity<List<LearningLogDto>> getLogs( @RequestParam( required = false ) String from,
	                                                     @RequestParam( required = false ) String to,
	                                                     Authentication auth ) {
		User user = getUser( auth );
		LocalDateTime fromDate = from != null ? LocalDate.parse( from ).atStartOfDay() : null;
		LocalDateTime toDate = to != null ? LocalDate.parse( to ).atTime( LocalTime.MAX ) : null;
		return ResponseEntity.ok( logService.getLogs( user, fromDate, toDate ).stream().map( LearningLogDto::from ).toList() );
	}

	@PostMapping
	public ResponseEntity<LearningLogDto> addLog( @RequestBody LearningLogDto dto, Authentication auth ) {
		return ResponseEntity.status( HttpStatus.CREATED )
				.body( LearningLogDto.from( logService.addLog( dto.toEntity( getUser( auth ) ) ) ) );
	}

	@DeleteMapping( "/{id}" )
	public ResponseEntity<Void> deleteLog( @PathVariable UUID id, Authentication auth ) {
		return logService.deleteLog( getUser( auth ), id ) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
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
