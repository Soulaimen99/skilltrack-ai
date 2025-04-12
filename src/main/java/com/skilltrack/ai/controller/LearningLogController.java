package com.skilltrack.ai.controller;

import com.skilltrack.ai.dto.LearningLogDto;
import com.skilltrack.ai.model.LearningLog;
import com.skilltrack.ai.model.User;
import com.skilltrack.ai.service.LearningLogService;
import com.skilltrack.ai.service.UserService;
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

@RestController
@RequestMapping( "/logs" )
public class LearningLogController {

	private final LearningLogService learningLogService;
	private final UserService userService;


	public LearningLogController( LearningLogService learningLogService, UserService userService ) {
		this.learningLogService = learningLogService;
		this.userService = userService;
	}

	@GetMapping
	public List<LearningLogDto> getLogs( Authentication auth ) {
		String username;
		if ( auth instanceof JwtAuthenticationToken jwtAuth ) {
			username = jwtAuth.getToken().getClaimAsString( "preferred_username" );
		}
		else {
			username = auth.getName();
		}

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
		String username;
		if ( auth instanceof JwtAuthenticationToken jwtAuth ) {
			username = jwtAuth.getToken().getClaimAsString( "preferred_username" );
		}
		else {
			username = auth.getName(); // for @WithMockUser or other types
		}

		User user = userService.getOrCreate( username );
		return learningLogService.addLog( user, log );
	}

	@DeleteMapping( "/{id}" )
	public void deleteLog( @PathVariable Long id, Authentication auth ) {
		String username;
		if ( auth instanceof JwtAuthenticationToken jwtAuth ) {
			username = jwtAuth.getToken().getClaimAsString( "preferred_username" );
		}
		else {
			username = auth.getName(); // for @WithMockUser or other types
		}

		User user = userService.getOrCreate( username );
		learningLogService.deleteLog( user, id );
	}
}
