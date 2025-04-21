package com.skilltrack.ai.controller;

import com.skilltrack.ai.dto.LearningLogDto;
import com.skilltrack.ai.dto.SummaryDto;
import com.skilltrack.ai.dto.UserDto;
import com.skilltrack.ai.entity.User;
import com.skilltrack.ai.service.LearningLogService;
import com.skilltrack.ai.service.SummaryService;
import com.skilltrack.ai.service.UserLookupService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping( "/admin" )
@SecurityRequirement( name = "bearerAuth" )
@PreAuthorize( "hasRole( 'admin' )" )
public class AdminController {

	private final LearningLogService learningLogService;

	private final SummaryService summaryService;

	private final UserLookupService userLookupService;

	public AdminController( LearningLogService learningLogService, SummaryService summaryService, UserLookupService userLookupService ) {
		this.learningLogService = learningLogService;
		this.summaryService = summaryService;
		this.userLookupService = userLookupService;
	}

	@GetMapping( "/users" )
	public ResponseEntity<List<UserDto>> getAllUsers() {
		List<UserDto> userDto = userLookupService.getAllUsers().stream()
				.map( user -> {
					int remainingSummaries = summaryService.remaining( user );
					LocalDate lastLocalDate = learningLogService.getLastLogDate( user );
					return UserDto.from( user, remainingSummaries, lastLocalDate );
				} )
				.toList();
		return ResponseEntity.ok( userDto );
	}

	@GetMapping( "/users/{user_id}/logs" )
	public ResponseEntity<LearningLogDto.PagedLogsResponse> getUserLogs(
			@PathVariable UUID user_id,
			@RequestParam( required = false ) String from,
			@RequestParam( required = false ) String to,
			@RequestParam( defaultValue = "0" ) int page,
			@RequestParam( defaultValue = "10" ) int size ) {
		User user = userLookupService.getById( user_id );

		return ResponseEntity.ok( learningLogService.getPagedLogsResponse( from, to, page, size, user ) );
	}

	@GetMapping( "/users/{user_id}/summaries" )
	public ResponseEntity<SummaryDto.PagedSummariesResponse> summarize(
			@PathVariable UUID user_id,
			@RequestParam( required = false ) String from,
			@RequestParam( required = false ) String to,
			@RequestParam( defaultValue = "0" ) int page,
			@RequestParam( defaultValue = "10" ) int size ) {
		User user = userLookupService.getById( user_id );

		return ResponseEntity.ok( summaryService.getPagedSummariesResponse( from, to, page, size, user ) );
	}
}
