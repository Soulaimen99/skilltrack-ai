package com.skilltrack.ai.service;

import com.skilltrack.ai.dto.QuizAnswerDto;
import com.skilltrack.ai.dto.QuizDto;
import com.skilltrack.ai.dto.QuizQuestionDto;
import com.skilltrack.ai.entity.LearningGoal;
import com.skilltrack.ai.entity.Quiz;
import com.skilltrack.ai.entity.QuizAnswer;
import com.skilltrack.ai.entity.QuizQuestion;
import com.skilltrack.ai.entity.User;
import com.skilltrack.ai.repository.QuizAnswerRepository;
import com.skilltrack.ai.repository.QuizQuestionRepository;
import com.skilltrack.ai.repository.QuizRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class QuizService {

	private final QuizRepository quizRepository;

	private final QuizQuestionRepository quizQuestionRepository;

	private final QuizAnswerRepository quizAnswerRepository;

	private final LearningGoalService learningGoalService;

	public QuizService(
			QuizRepository quizRepository,
			QuizQuestionRepository quizQuestionRepository,
			QuizAnswerRepository quizAnswerRepository,
			LearningGoalService learningGoalService
	) {
		this.quizRepository = quizRepository;
		this.quizQuestionRepository = quizQuestionRepository;
		this.quizAnswerRepository = quizAnswerRepository;
		this.learningGoalService = learningGoalService;
	}

	@Transactional
	public Quiz getByIdForUser( User user, UUID quizId ) {
		Quiz quiz = quizRepository.findById( quizId )
				.orElseThrow( () -> new ResponseStatusException( HttpStatus.NOT_FOUND, "Quiz not found" ) );

		if ( !quiz.getUser().getId().equals( user.getId() ) ) {
			throw new AccessDeniedException( "You cannot access this quiz" );
		}

		return quiz;
	}

	@Transactional
	public QuizDto getQuizDtoByIdForUser( User user, UUID quizId ) {
		Quiz quiz = getByIdForUser( user, quizId );
		return QuizDto.from( quiz );
	}

	@Transactional
	public List<QuizDto> getAllQuizzes( User user ) {
		List<Quiz> quizzes = quizRepository.findByUser( user );
		return quizzes.stream().map( QuizDto::from ).toList();
	}

	@Transactional
	public List<QuizDto> getQuizzesByGoal( User user, UUID goalId ) {
		LearningGoal goal = learningGoalService.getByIdForUser( user, goalId );
		List<Quiz> quizzes = quizRepository.findByLearningGoal( goal );
		return quizzes.stream().map( QuizDto::from ).toList();
	}

	@Transactional
	public QuizDto.PagedQuizzesResponse getPagedQuizzesResponse( String from, String to, boolean completed, int page, Integer size, User user ) {
		LocalDateTime dtFrom = from != null ? LocalDate.parse( from ).atStartOfDay() : null;
		LocalDateTime dtTo = to != null ? LocalDate.parse( to ).atTime( LocalTime.MAX ) : null;
		Pageable pageable;
		if ( size == null ) {
			pageable = Pageable.unpaged();
		}
		else {
			pageable = PageRequest.of( page, size, Sort.by( "startedAt" ).descending() );
		}
		Page<Quiz> quizPage = getQuizzes( user, dtFrom, dtTo, completed, pageable );
		List<QuizDto> content = quizPage.getContent().stream()
				.map( QuizDto::from )
				.toList();

		return new QuizDto.PagedQuizzesResponse(
				content,
				quizPage.getNumber(),
				quizPage.getSize(),
				quizPage.getTotalPages(),
				quizPage.getTotalElements()
		);
	}

	@Transactional
	public Page<Quiz> getQuizzes( User user, LocalDateTime from, LocalDateTime to, boolean completed, Pageable pageable ) {
		if ( from != null && to != null ) {
			return quizRepository.findByUserAndCompletedAndStartedAtBetween( user, completed, from, to, pageable );
		}
		return quizRepository.findByUser( user, pageable );
	}

	@Transactional
	public QuizDto createQuiz( User user, UUID goalId ) {
		LearningGoal goal = learningGoalService.getByIdForUser( user, goalId );

		Quiz quiz = new Quiz();
		quiz.setUser( user );
		quiz.setLearningGoal( goal );
		quiz.setCompleted( false );
		quiz.setStartedAt( LocalDateTime.now() );

		Quiz savedQuiz = quizRepository.save( quiz );
		return QuizDto.from( savedQuiz );
	}

	@Transactional
	public QuizDto addQuestionToQuiz( User user, UUID quizId, QuizQuestionDto questionDto ) {
		Quiz quiz = getByIdForUser( user, quizId );

		QuizQuestion question = questionDto.toEntity( quiz );
		quizQuestionRepository.save( question );

		return QuizDto.from( quizRepository.findById( quizId ).orElseThrow() );
	}

	@Transactional
	public QuizDto submitAnswer( User user, UUID quizId, UUID questionId, QuizAnswerDto answerDto ) {
		Quiz quiz = getByIdForUser( user, quizId );

		QuizQuestion question = quizQuestionRepository.findById( questionId )
				.orElseThrow( () -> new ResponseStatusException( HttpStatus.NOT_FOUND, "Question not found" ) );

		if ( !question.getQuiz().getId().equals( quizId ) ) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Question does not belong to this quiz" );
		}

		QuizAnswer answer = answerDto.toEntity( question );

		// Check if the answer is correct
		boolean isCorrect = question.getCorrectAnswer().equalsIgnoreCase( answer.getAnswer() );
		answer.setCorrect( isCorrect );

		// Set the score based on correctness
		answer.setScore( isCorrect ? question.getScore() : 0 );

		quizAnswerRepository.save( answer );

		return QuizDto.from( quizRepository.findById( quizId ).orElseThrow() );
	}

	@Transactional
	public QuizDto completeQuiz( User user, UUID quizId ) {
		Quiz quiz = getByIdForUser( user, quizId );

		// Calculate total score
		List<QuizQuestion> questions = quizQuestionRepository.findByQuiz( quiz );
		int totalScore = 0;
		int totalPossibleScore = 0;

		for ( QuizQuestion question : questions ) {
			totalPossibleScore += question.getScore();
			if ( question.getAnswer() != null && question.getAnswer().isCorrect() ) {
				totalScore += question.getAnswer().getScore();
			}
		}

		quiz.setScore( totalScore );
		quiz.setCompleted( true );
		quiz.setEndedAt( LocalDateTime.now() );

		// Calculate duration in seconds
		if ( quiz.getStartedAt() != null ) {
			long durationSeconds = java.time.Duration.between( quiz.getStartedAt(), quiz.getEndedAt() ).getSeconds();
			quiz.setDuration( ( int ) durationSeconds );
		}

		// Generate feedback based on score
		String feedback;
		double scorePercentage = totalPossibleScore > 0 ? ( double ) totalScore / totalPossibleScore * 100 : 0;

		if ( scorePercentage >= 90 ) {
			feedback = "Excellent! You've mastered this topic.";
		}
		else if ( scorePercentage >= 70 ) {
			feedback = "Good job! You have a solid understanding of this topic.";
		}
		else if ( scorePercentage >= 50 ) {
			feedback = "You're making progress, but there's room for improvement.";
		}
		else {
			feedback = "You might need to review this topic more thoroughly.";
		}

		quiz.setFeedback( feedback );

		Quiz savedQuiz = quizRepository.save( quiz );
		return QuizDto.from( savedQuiz );
	}

	@Transactional
	public boolean deleteQuiz( User user, UUID quizId ) {
		return quizRepository.findById( quizId )
				.filter( quiz -> quiz.getUser().getId().equals( user.getId() ) )
				.map( quiz -> {
					quizRepository.delete( quiz );
					return true;
				} )
				.orElse( false );
	}
}
