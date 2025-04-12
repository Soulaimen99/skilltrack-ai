package com.skilltrack.ai.controller;

import com.skilltrack.ai.dto.LearningLogDto;
import com.skilltrack.ai.model.LearningLog;
import com.skilltrack.ai.model.User;
import com.skilltrack.ai.service.LearningLogService;
import com.skilltrack.ai.service.SummaryService;
import com.skilltrack.ai.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping( "/logs" )
public class LearningLogController {

	private final LearningLogService learningLogService;
	private final UserService userService;
	private final SummaryService summaryService;

	public LearningLogController( LearningLogService learningLogService, UserService userService, SummaryService summaryService ) {
		this.learningLogService = learningLogService;
		this.userService = userService;
		this.summaryService = summaryService;
	}

	private static String getUsernameFromAuthentication( Authentication auth ) {
		String username;
		if ( auth instanceof JwtAuthenticationToken jwtAuth ) {
			username = jwtAuth.getToken().getClaimAsString( "preferred_username" );
		}
		else {
			username = auth.getName();
		}
		return username;
	}

	@GetMapping
	public List<LearningLogDto> getLogs( Authentication auth ) {
		String username = getUsernameFromAuthentication( auth );

		User user = userService.getOrCreate( username );
		return learningLogService.getLogs( user ).stream()
				.map( log -> new LearningLogDto(
						log.getId(),
						log.getUser().getUsername(),
						log.getContent(),
						log.getTags(),
						log.getDate()
				) )
				.toList();
	}

	@PostMapping
	public LearningLog addLog( @RequestBody LearningLog log, Authentication auth ) {
		String username = getUsernameFromAuthentication( auth );

		User user = userService.getOrCreate( username );
		return learningLogService.addLog( user, log );
	}

	@DeleteMapping( "/{id}" )
	public void deleteLog( @PathVariable Long id, Authentication auth ) {
		String username = getUsernameFromAuthentication( auth );

		User user = userService.getOrCreate( username );
		learningLogService.deleteLog( user, id );
	}

	@PostMapping( "/summarize" )
	public ResponseEntity<Map<String, String>> summarizeLogs( @RequestBody List<LearningLog> logs, Authentication auth ) {
		String username = getUsernameFromAuthentication( auth );
		if ( username == null ) {
			return ResponseEntity.badRequest().build();
		}

		List<String> contents = logs.stream()
				.map( LearningLog::getContent )
				.filter( line -> line != null && !line.isBlank() )
				.toList();

		String summary = summaryService.summarize( username, contents );
		return ResponseEntity.ok( Map.of( "summary", summary ) );
	}
}
