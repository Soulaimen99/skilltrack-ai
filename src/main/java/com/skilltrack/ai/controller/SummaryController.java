package com.skilltrack.ai.controller;

import com.skilltrack.ai.dto.SummaryDto;
import com.skilltrack.ai.model.LearningLog;
import com.skilltrack.ai.model.User;
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
@RequestMapping( "/summaries" )
public class SummaryController {

	private final SummaryService summaryService;
	private final UserService userService;

	public SummaryController( SummaryService summaryService, UserService userService ) {
		this.summaryService = summaryService;
		this.userService = userService;
	}

	@GetMapping
	public List<SummaryDto> getSummaries( Authentication auth ) {
		String username;
		if ( auth instanceof JwtAuthenticationToken jwtAuth ) {
			username = jwtAuth.getToken().getClaimAsString( "preferred_username" );
		}
		else {
			username = auth.getName();
		}

		User user = userService.getOrCreate( username );
		return summaryService.getSummaries( user ).stream()
				.map( summary -> new SummaryDto(
						summary.getId(),
						summary.getUser().getUsername(),
						summary.getContent(),
						summary.getCreatedAt()
				) )
				.toList();
	}

	@PostMapping
	public ResponseEntity<Map<String, String>> summarizeLogs( @RequestBody List<LearningLog> logs, Authentication auth ) {
		String username;
		if ( auth instanceof JwtAuthenticationToken jwtAuth ) {
			username = jwtAuth.getToken().getClaimAsString( "preferred_username" );
		}
		else {
			username = auth.getName();
		}

		User user = userService.getOrCreate( username );
		StringBuilder summary = new StringBuilder( "Here's a summary of your recent learning:\n\n" );
		for ( LearningLog log : logs ) {
			summary.append( "• " )
					.append( log.getContent() );
			if ( log.getTags() != null && !log.getTags().isBlank() ) {
				summary.append( " (Tags: " ).append( log.getTags() ).append( ")" );
			}
			summary.append( "\n" );
		}
		summaryService.addSummary( user, summary.toString() );
		return ResponseEntity.ok( Map.of( "summary", summary.toString() ) );
	}


	@DeleteMapping( "/{id}" )
	public void deleteSummary( @PathVariable Long id, Authentication auth ) {
		String username;
		if ( auth instanceof JwtAuthenticationToken jwtAuth ) {
			username = jwtAuth.getToken().getClaimAsString( "preferred_username" );
		}
		else {
			username = auth.getName();
		}
		
		User user = userService.getOrCreate( username );
		summaryService.deleteSummary( user, id );
	}


}
