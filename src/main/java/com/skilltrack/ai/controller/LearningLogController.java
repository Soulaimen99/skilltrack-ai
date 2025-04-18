package com.skilltrack.ai.controller;

import com.skilltrack.ai.dto.LearningLogDto;
import com.skilltrack.ai.dto.PagedLogsResponse;
import com.skilltrack.ai.entity.LearningLog;
import com.skilltrack.ai.entity.User;
import com.skilltrack.ai.service.LearningLogService;
import com.skilltrack.ai.service.SummaryService;
import com.skilltrack.ai.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
			return userService.getOrCreate( username, email );
		}
		return userService.getOrCreate( auth.getName(), null );
	}

	@GetMapping
	public ResponseEntity<PagedLogsResponse> getLogs( @RequestParam( required = false ) String from,
	                                                  @RequestParam( required = false ) String to,
	                                                  @RequestParam( defaultValue = "0" ) int page,
	                                                  @RequestParam( defaultValue = "10" ) int size,
	                                                  Authentication auth ) {
		User user = getUser( auth );
		LocalDateTime dtFrom = from != null ? LocalDate.parse( from ).atStartOfDay() : null;
		LocalDateTime dtTo = to != null ? LocalDate.parse( to ).atTime( LocalTime.MAX ) : null;
		Pageable pageable = PageRequest.of( page, size, Sort.by( "createdAt" ).descending() );
		Page<LearningLog> logPage = learningLogService.getLogs( user, dtFrom, dtTo, pageable );

		List<LearningLogDto> content = logPage.getContent().stream()
				.map( LearningLogDto::from )
				.toList();

		return ResponseEntity.ok( new PagedLogsResponse(
				content,
				logPage.getNumber(),
				logPage.getSize(),
				logPage.getTotalPages(),
				logPage.getTotalElements()
		) );
	}

	@PostMapping
	public ResponseEntity<LearningLogDto> addLog( @RequestBody LearningLogDto dto, Authentication auth ) {
		return ResponseEntity.status( HttpStatus.CREATED )
				.body( LearningLogDto.from( learningLogService.addLog( dto.toEntity( getUser( auth ) ) ) ) );
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
