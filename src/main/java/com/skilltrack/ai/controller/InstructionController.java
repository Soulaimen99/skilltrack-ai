package com.skilltrack.ai.controller;

import com.skilltrack.ai.dto.InstructionDto;
import com.skilltrack.ai.dto.InstructionRequestDto;
import com.skilltrack.ai.entity.LearningGoal;
import com.skilltrack.ai.entity.User;
import com.skilltrack.ai.service.InstructionService;
import com.skilltrack.ai.service.LearningGoalService;
import com.skilltrack.ai.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping( "/api/instructions" )
@SecurityRequirement( name = "bearerAuth" )
@PreAuthorize( "hasRole('user')" )
public class InstructionController {

	private final InstructionService instructionService;

	private final UserService userService;

	private final LearningGoalService learningGoalService;

	public InstructionController( InstructionService instructionService, UserService userService, LearningGoalService learningGoalService ) {
		this.instructionService = instructionService;
		this.userService = userService;
		this.learningGoalService = learningGoalService;
	}

	@GetMapping
	public ResponseEntity<List<InstructionDto>> listInstructions( Authentication auth ) {
		User user = userService.getCurrentUser( auth );
		return ResponseEntity.ok( instructionService.getAllInstructions( user ) );
	}


	@PostMapping
	public ResponseEntity<InstructionDto> generateInstruction( @RequestBody InstructionRequestDto request, Authentication auth ) {
		User user = userService.getCurrentUser( auth );
		LearningGoal goal = learningGoalService.getByIdForUser( user, request.goalId() );
		return ResponseEntity.ok( instructionService.generateInstruction( user, goal, request.logs() ) );
	}
}
