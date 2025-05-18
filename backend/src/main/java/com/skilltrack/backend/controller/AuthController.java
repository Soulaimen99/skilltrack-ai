package com.skilltrack.backend.controller;

import com.skilltrack.backend.dto.UserDto;
import com.skilltrack.backend.entity.User;
import com.skilltrack.backend.service.LearningLogService;
import com.skilltrack.backend.service.SummaryService;
import com.skilltrack.backend.service.UserLookupService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping( "/api/auth" )
public class AuthController {

	private final UserLookupService userLookupService;

	private final SummaryService summaryService;

	private final LearningLogService learningLogService;

	public AuthController( UserLookupService userLookupService, SummaryService summaryService, LearningLogService learningLogService ) {
		this.userLookupService = userLookupService;
		this.summaryService = summaryService;
		this.learningLogService = learningLogService;
	}

	@GetMapping( "/me" )
	public ResponseEntity<UserDto> me( @AuthenticationPrincipal Jwt jwt ) {
		String username = jwt.getClaimAsString( "preferred_username" );
		String email = jwt.getClaimAsString( "email" );
		User user = userLookupService.getOrCreate( username, email );

		int remainingSummaries = summaryService.remaining( user );
		LocalDate lastLocalDate = learningLogService.getLastLogDate( user );

		return ResponseEntity.ok( UserDto.from( user, remainingSummaries, lastLocalDate ) );
	}
}
