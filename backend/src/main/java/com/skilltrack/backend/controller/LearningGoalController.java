package com.skilltrack.backend.controller;

import com.skilltrack.backend.dto.LearningGoalDto;
import com.skilltrack.backend.entity.LearningGoal;
import com.skilltrack.backend.entity.User;
import com.skilltrack.backend.service.LearningGoalService;
import com.skilltrack.backend.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

import java.util.UUID;

@RestController
@RequestMapping( "/api/goals" )
@SecurityRequirement( name = "bearerAuth" )
@PreAuthorize( "hasRole( 'user' )" )
public class LearningGoalController {

	private final LearningGoalService learningGoalService;

	private final UserService userService;

	public LearningGoalController( LearningGoalService learningGoalService, UserService userService ) {
		this.learningGoalService = learningGoalService;
		this.userService = userService;
	}

	@GetMapping
	public ResponseEntity<LearningGoalDto.PagedGoalsResponse> readGoals( @RequestParam( required = false ) String from,
	                                                                     @RequestParam( required = false ) String to,
	                                                                     @RequestParam( defaultValue = "0" ) int page,
	                                                                     @RequestParam( required = false ) Integer size,
	                                                                     Authentication auth ) {
		User user = userService.getCurrentUser( auth );

		return ResponseEntity.ok( learningGoalService.getPagedGoalsResponse( from, to, page, size, user ) );
	}

	@PostMapping
	public ResponseEntity<LearningGoalDto> createGoal( @RequestBody LearningGoalDto goalDto, Authentication auth ) {
		User user = userService.getCurrentUser( auth );
		LearningGoal created = learningGoalService.addGoal( goalDto.toEntity( user ) );

		return ResponseEntity.status( HttpStatus.CREATED )
				.body( LearningGoalDto.from( learningGoalService.addGoal( created ) ) );
	}

	@PutMapping( "/{id}" )
	public ResponseEntity<LearningGoalDto> updateGoal( @PathVariable UUID id, @RequestBody LearningGoalDto goalDto, Authentication auth ) {
		User user = userService.getCurrentUser( auth );
		LearningGoal updated = learningGoalService.editGoal( id, goalDto.toEntity( user ) );

		return ResponseEntity.ok( LearningGoalDto.from( updated ) );
	}

	@DeleteMapping( "/{id}" )
	public ResponseEntity<Void> deleteGoal( @PathVariable UUID id, Authentication auth ) {
		return learningGoalService.deleteGoal( userService.getCurrentUser( auth ), id ) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
	}
}
