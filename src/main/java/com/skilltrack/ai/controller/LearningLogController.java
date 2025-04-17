package com.skilltrack.ai.controller;

import com.skilltrack.ai.dto.LearningLogDto;
import com.skilltrack.ai.model.LearningLog;
import com.skilltrack.ai.model.User;
import com.skilltrack.ai.service.LearningLogService;
import com.skilltrack.ai.service.SummaryRateLimiter;
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

@SecurityRequirement( name = "bearerAuth" )
@RestController
@RequestMapping( "/logs" )
public class LearningLogController {

	private final LearningLogService learningLogService;

	private final UserService userService;

	private final SummaryService summaryService;

	private final SummaryRateLimiter summaryRateLimiter;

	public LearningLogController( LearningLogService learningLogService, UserService userService, SummaryService summaryService, SummaryRateLimiter summaryRateLimiter ) {
		this.learningLogService = learningLogService;
		this.userService = userService;
		this.summaryService = summaryService;
		this.summaryRateLimiter = summaryRateLimiter;
	}

	private static String getClaim( JwtAuthenticationToken jwt, String key ) {
		return jwt.getToken().getClaimAsString( key );
	}

	private User getUserFromAuth( Authentication auth ) {
		if ( auth instanceof JwtAuthenticationToken jwt ) {
			String username = getClaim( jwt, "preferred_username" );
			String email = getClaim( jwt, "email" );
			return userService.getOrUpdate( username, email );
		}
		return userService.getOrUpdate( auth.getName(), null );
	}

	@GetMapping
	public ResponseEntity<List<LearningLogDto>> getLogs(
			@RequestParam( required = false ) String from,
			@RequestParam( required = false ) String to,
			Authentication auth ) {
		User user = getUserFromAuth( auth );
		LocalDateTime dtFrom = from != null ? LocalDate.parse( from ).atStartOfDay() : null;
		LocalDateTime dtTo = to != null ? LocalDate.parse( to ).atTime( LocalTime.MAX ) : null;
		List<LearningLogDto> logs = learningLogService.getLogs( user, dtFrom, dtTo ).stream()
				.map( LearningLogDto::from )
				.toList();

		return ResponseEntity.ok( logs );
	}

	@PostMapping
	public ResponseEntity<LearningLogDto> addLog( @RequestBody LearningLogDto logDto, Authentication auth ) {
		User user = getUserFromAuth( auth );
		LearningLog log = learningLogService.addLog( user, logDto.toEntity() );

		return ResponseEntity.status( HttpStatus.CREATED ).body( LearningLogDto.from( log ) );
	}

	@DeleteMapping( "/{id}" )
	public ResponseEntity<Void> deleteLog( @PathVariable UUID id, Authentication auth ) {
		User user = getUserFromAuth( auth );
		boolean deleted = learningLogService.deleteLog( user, id );

		return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
	}

	@PostMapping( "/summarize" )
	public ResponseEntity<Map<String, String>> summarizeLogs( @RequestBody List<LearningLogDto> logsDto, Authentication auth ) {
		User user = getUserFromAuth( auth );
		String username = user.getUsername();

		if ( !summaryRateLimiter.allow( username ) ) {
			return ResponseEntity.status( HttpStatus.TOO_MANY_REQUESTS )
					.body( Map.of( "error", "You have reached your daily summary limit. Try again tomorrow." ) );
		}

		List<String> contents = logsDto.stream()
				.map( LearningLogDto::content )
				.filter( line -> line != null && !line.isBlank() )
				.toList();
		String summary = summaryService.summarize( user.getUsername(), contents );

		return ResponseEntity.ok().header( "X-RateLimit-Remaining", String.valueOf( summaryRateLimiter.remaining( username ) ) )
				.body( Map.of( "summary", summary ) );
	}
}
