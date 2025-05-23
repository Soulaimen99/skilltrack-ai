package com.skilltrack.backend.controller;

import com.skilltrack.backend.dto.LearningInsightsDto;
import com.skilltrack.backend.dto.LearningLogDto;
import com.skilltrack.backend.entity.LearningGoal;
import com.skilltrack.backend.entity.LearningLog;
import com.skilltrack.backend.entity.User;
import com.skilltrack.backend.service.ExportService;
import com.skilltrack.backend.service.LearningGoalService;
import com.skilltrack.backend.service.LearningLogService;
import com.skilltrack.backend.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
@RequestMapping( "/api/logs" )
@SecurityRequirement( name = "bearerAuth" )
@PreAuthorize( "hasRole( 'user' )" )
@RequiredArgsConstructor
public class LearningLogController {

	private final LearningLogService learningLogService;

	private final UserService userService;

	private final ExportService exportService;

	private final LearningGoalService learningGoalService;

	@GetMapping
	public ResponseEntity<LearningLogDto.PagedLogsResponse> readLogs( @RequestParam( required = false ) String from,
	                                                                  @RequestParam( required = false ) String to,
	                                                                  @RequestParam( defaultValue = "0" ) int page,
	                                                                  @RequestParam( required = false ) Integer size,
	                                                                  @RequestParam( required = false ) UUID goalId,
	                                                                  Authentication auth ) {
		User user = userService.getCurrentUser( auth );

		return ResponseEntity.ok( learningLogService.getPagedLogsResponse( from, to, page, size, goalId, user ) );
	}

	@PostMapping
	public ResponseEntity<LearningLogDto> createLog( @RequestBody LearningLogDto logDto, Authentication auth ) {
		User user = userService.getCurrentUser( auth );
		LearningGoal goal = null;
		if ( logDto.goalId() != null ) {
			goal = learningGoalService.getByIdForUser( user, logDto.goalId() );
		}
		LearningLog created = learningLogService.addLog( logDto.toEntity( user, goal ) );

		return ResponseEntity.status( HttpStatus.CREATED )
				.body( LearningLogDto.from( learningLogService.addLog( created ) ) );
	}

	@PutMapping( "/{id}" )
	public ResponseEntity<LearningLogDto> updateLog( @PathVariable UUID id, @RequestBody LearningLogDto logDto, Authentication auth ) {
		User user = userService.getCurrentUser( auth );
		LearningGoal goal = null;
		if ( logDto.goalId() != null ) {
			goal = learningGoalService.getByIdForUser( user, logDto.goalId() );
		}
		LearningLog updated = learningLogService.editLog( id, logDto.toEntity( user, goal ) );

		return ResponseEntity.ok( LearningLogDto.from( updated ) );
	}

	@DeleteMapping( "/{id}" )
	public ResponseEntity<Void> deleteLog( @PathVariable UUID id, Authentication auth ) {
		return learningLogService.deleteLog( userService.getCurrentUser( auth ), id ) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
	}

	@GetMapping( "/insights" )
	public ResponseEntity<LearningInsightsDto> getInsights( Authentication auth ) {
		User user = userService.getCurrentUser( auth );
		return ResponseEntity.ok( learningLogService.getInsights( user ) );
	}

	@GetMapping( "/export" )
	public ResponseEntity<byte[]> exportLogs( @RequestParam( defaultValue = "json" ) String format, Authentication auth ) {
		User user = userService.getCurrentUser( auth );
		String content = exportService.exportLogs( user, format );

		String contentType = format.equalsIgnoreCase( "txt" ) ? "text/plain" : "application/json";
		String filename = "learning_logs." + format;

		return ResponseEntity.ok()
				.header( HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"" )
				.header( HttpHeaders.CONTENT_TYPE, contentType )
				.body( content.getBytes( StandardCharsets.UTF_8 ) );
	}
}
