package com.skilltrack.ai.controller;

import com.skilltrack.ai.dto.QuizAnswerDto;
import com.skilltrack.ai.dto.QuizDto;
import com.skilltrack.ai.dto.QuizQuestionDto;
import com.skilltrack.ai.entity.User;
import com.skilltrack.ai.service.QuizService;
import com.skilltrack.ai.service.UserService;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping( "/api/quizzes" )
@SecurityRequirement( name = "bearerAuth" )
@PreAuthorize( "hasRole('user')" )
public class QuizController {

	private final QuizService quizService;

	private final UserService userService;

	public QuizController( QuizService quizService, UserService userService ) {
		this.quizService = quizService;
		this.userService = userService;
	}

	@GetMapping
	public ResponseEntity<QuizDto.PagedQuizzesResponse> getQuizzes(
			@RequestParam( required = false ) String from,
			@RequestParam( required = false ) String to,
			@RequestParam( defaultValue = "false" ) boolean completed,
			@RequestParam( defaultValue = "0" ) int page,
			@RequestParam( required = false ) Integer size,
			Authentication auth ) {
		User user = userService.getCurrentUser( auth );
		return ResponseEntity.ok( quizService.getPagedQuizzesResponse( from, to, completed, page, size, user ) );
	}

	@GetMapping( "/goal/{goalId}" )
	public ResponseEntity<List<QuizDto>> getQuizzesByGoal(
			@PathVariable UUID goalId,
			Authentication auth ) {
		User user = userService.getCurrentUser( auth );
		return ResponseEntity.ok( quizService.getQuizzesByGoal( user, goalId ) );
	}

	@GetMapping( "/{quizId}" )
	public ResponseEntity<QuizDto> getQuiz(
			@PathVariable UUID quizId,
			Authentication auth ) {
		User user = userService.getCurrentUser( auth );
		return ResponseEntity.ok( quizService.getQuizDtoByIdForUser( user, quizId ) );
	}

	@PostMapping( "/goal/{goalId}" )
	public ResponseEntity<QuizDto> createQuiz(
			@PathVariable UUID goalId,
			Authentication auth ) {
		User user = userService.getCurrentUser( auth );
		return ResponseEntity.status( HttpStatus.CREATED )
				.body( quizService.createQuiz( user, goalId ) );
	}

	@PostMapping( "/{quizId}/questions" )
	public ResponseEntity<QuizDto> addQuestion(
			@PathVariable UUID quizId,
			@RequestBody QuizQuestionDto questionDto,
			Authentication auth ) {
		User user = userService.getCurrentUser( auth );
		return ResponseEntity.status( HttpStatus.CREATED )
				.body( quizService.addQuestionToQuiz( user, quizId, questionDto ) );
	}

	@PostMapping( "/{quizId}/questions/{questionId}/answer" )
	public ResponseEntity<QuizDto> submitAnswer(
			@PathVariable UUID quizId,
			@PathVariable UUID questionId,
			@RequestBody QuizAnswerDto answerDto,
			Authentication auth ) {
		User user = userService.getCurrentUser( auth );
		return ResponseEntity.ok( quizService.submitAnswer( user, quizId, questionId, answerDto ) );
	}

	@PutMapping( "/{quizId}/complete" )
	public ResponseEntity<QuizDto> completeQuiz(
			@PathVariable UUID quizId,
			Authentication auth ) {
		User user = userService.getCurrentUser( auth );
		return ResponseEntity.ok( quizService.completeQuiz( user, quizId ) );
	}

	@DeleteMapping( "/{quizId}" )
	public ResponseEntity<Void> deleteQuiz(
			@PathVariable UUID quizId,
			Authentication auth ) {
		User user = userService.getCurrentUser( auth );
		return quizService.deleteQuiz( user, quizId )
				? ResponseEntity.noContent().build()
				: ResponseEntity.notFound().build();
	}
}
