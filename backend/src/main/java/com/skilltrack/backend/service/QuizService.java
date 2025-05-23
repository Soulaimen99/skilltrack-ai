package com.skilltrack.backend.service;

import com.skilltrack.backend.dto.QuizAnswerDto;
import com.skilltrack.backend.dto.QuizDto;
import com.skilltrack.backend.dto.QuizQuestionDto;
import com.skilltrack.backend.entity.LearningGoal;
import com.skilltrack.backend.entity.Quiz;
import com.skilltrack.backend.entity.QuizAnswer;
import com.skilltrack.backend.entity.QuizQuestion;
import com.skilltrack.backend.entity.QuizType;
import com.skilltrack.backend.entity.User;
import com.skilltrack.backend.repository.QuizAnswerRepository;
import com.skilltrack.backend.repository.QuizQuestionRepository;
import com.skilltrack.backend.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class QuizService {

	private static final Logger logger = LoggerFactory.getLogger( QuizService.class );

	private final QuizRepository quizRepository;

	private final QuizQuestionRepository quizQuestionRepository;

	private final QuizAnswerRepository quizAnswerRepository;

	private final LearningGoalService learningGoalService;

	private final OpenAiChatModel chatModel;

	@Transactional
	public Quiz getByIdForUser( User user, UUID quizId ) {
		Quiz quiz = quizRepository.findById( quizId )
				.orElseThrow( () -> new ResponseStatusException( HttpStatus.NOT_FOUND, "Quiz not found" ) );

		if ( !quiz.getUser().getId().equals( user.getId() ) ) {
			throw new AccessDeniedException( "You cannot access this quiz" );
		}

		logger.debug( "Successfully retrieved quiz with ID: {} for user: {}", quizId, user.getId() );
		return quiz;
	}

	@Transactional
	public QuizDto getQuizDtoByIdForUser( User user, UUID quizId ) {
		Quiz quiz = getByIdForUser( user, quizId );
		logger.debug( "Successfully converted quiz to DTO for ID: {}", quizId );
		return QuizDto.from( quiz );
	}

	@Transactional
	public List<QuizDto> getQuizzesByGoal( User user, UUID goalId ) {
		LearningGoal goal = learningGoalService.getByIdForUser( user, goalId );
		List<Quiz> quizzes = quizRepository.findByLearningGoal( goal );
		logger.info( "Retrieved {} quizzes for goal: {} and user: {}", quizzes.size(), goalId, user.getId() );
		return quizzes.stream().map( QuizDto::from ).toList();
	}

	@Transactional
	public QuizDto.PagedQuizzesResponse getPagedQuizzesResponse( String from, String to, boolean completed, int page, Integer size, User user ) {
		LocalDateTime dtFrom = from != null ? LocalDate.parse( from ).atStartOfDay() : null;
		LocalDateTime dtTo = to != null ? LocalDate.parse( to ).atTime( LocalTime.MAX ) : null;

		Pageable pageable;
		if ( size == null ) {
			logger.debug( "Using unpaged pageable" );
			pageable = Pageable.unpaged();
		}
		else {
			logger.debug( "Using paged pageable with page: {} and size: {}", page, size );
			pageable = PageRequest.of( page, size, Sort.by( "startedAt" ).descending() );
		}

		Page<Quiz> quizPage = getQuizzes( user, dtFrom, dtTo, completed, pageable );
		List<QuizDto> content = quizPage.getContent().stream()
				.map( QuizDto::from )
				.toList();

		logger.info( "Retrieved {} quizzes (page {} of {}) for user: {}",
				quizPage.getNumberOfElements(), quizPage.getNumber() + 1, quizPage.getTotalPages(), user.getId() );

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
			logger.debug( "Using date range filter for quizzes" );
			return quizRepository.findByUserAndCompletedAndStartedAtBetween( user, completed, from, to, pageable );
		}

		logger.debug( "Fetching quizzes without date range filter" );
		return quizRepository.findByUser( user, pageable );
	}

	@Transactional
	public QuizDto createQuiz( User user, UUID goalId ) {
		LearningGoal goal = learningGoalService.getByIdForUser( user, goalId );
		logger.debug( "Retrieved goal with title: {}", goal.getTitle() );

		Quiz quiz = new Quiz();
		quiz.setUser( user );
		quiz.setLearningGoal( goal );
		quiz.setCompleted( false );
		quiz.setStartedAt( LocalDateTime.now() );

		Quiz savedQuiz = quizRepository.save( quiz );
		logger.debug( "Successfully saved quiz with ID: {}", savedQuiz.getId() );

		// Generate AI questions for the quiz
		generateAIQuestions( savedQuiz, goal );

		logger.info( "Successfully created quiz with ID: {} for user: {}", savedQuiz.getId(), user.getId() );
		return QuizDto.from( savedQuiz );
	}

	/**
	 * Generates quiz questions using AI based on the learning goal
	 *
	 * @param quiz The quiz to add questions to
	 * @param goal The learning goal to generate questions about
	 */
	private void generateAIQuestions( Quiz quiz, LearningGoal goal ) {
		// Create a prompt for the AI to generate questions
		String promptText = String.format( """
				You are an educational quiz generator. Create 3 quiz questions about the following learning goal:
				
				Title: %s
				Description: %s
				
				For each question, provide:
				1. The question text
				2. The question type (MCQ for multiple choice, BOOLEAN for true/false, or TEXT for free text)
				3. For MCQ questions, provide 4 options separated by semicolons
				4. The correct answer
				
				Format your response exactly as follows for each question:
				QUESTION: [question text]
				TYPE: [MCQ/BOOLEAN/TEXT]
				OPTIONS: [option1;option2;option3;option4] (only for MCQ)
				ANSWER: [correct answer]
				
				Make sure the questions test understanding of the topic, not just memorization.
				""", goal.getTitle(), goal.getDescription() );

		// Call the OpenAI API to generate questions
		String aiResponse = chatModel.call( new Prompt( promptText ) ).getResult().getOutput().getText();
		logger.debug( "Received response from AI model" );

		// Parse the AI response to extract questions
		List<QuizQuestion> questions = parseAIResponse( aiResponse, quiz );
		logger.debug( "Created {} questions from AI response", questions.size() );

		// Save the questions
		quizQuestionRepository.saveAll( questions );
		logger.info( "Successfully generated and saved {} AI questions for quiz: {}", questions.size(), quiz.getId() );
	}

	/**
	 * Parses the AI response to extract quiz questions
	 *
	 * @param aiResponse The AI response text
	 * @param quiz       The quiz to associate the questions with
	 * @return A list of QuizQuestion entities
	 */
	private List<QuizQuestion> parseAIResponse( String aiResponse, Quiz quiz ) {
		List<QuizQuestion> questions = new ArrayList<>();

		// Define patterns to extract question components
		logger.debug( "Setting up regex patterns for parsing" );
		Pattern questionPattern = Pattern.compile( "QUESTION:\\s*(.+?)\\s*(?=TYPE:|$)", Pattern.DOTALL );
		Pattern typePattern = Pattern.compile( "TYPE:\\s*(\\w+)\\s*", Pattern.DOTALL );
		Pattern optionsPattern = Pattern.compile( "OPTIONS:\\s*(.+?)\\s*(?=ANSWER:|$)", Pattern.DOTALL );
		Pattern answerPattern = Pattern.compile( "ANSWER:\\s*(.+?)\\s*(?=QUESTION:|$)", Pattern.DOTALL );

		// Split the response by "QUESTION:" to get individual question blocks
		String[] questionBlocks = aiResponse.split( "(?=QUESTION:)" );

		for ( String block : questionBlocks ) {
			if ( block.trim().isEmpty() || !block.contains( "QUESTION:" ) ) {
				logger.debug( "Skipping empty or invalid block" );
				continue;
			}

			// Extract question components
			Matcher questionMatcher = questionPattern.matcher( block );
			Matcher typeMatcher = typePattern.matcher( block );
			Matcher optionsMatcher = optionsPattern.matcher( block );
			Matcher answerMatcher = answerPattern.matcher( block );

			if ( questionMatcher.find() && typeMatcher.find() && answerMatcher.find() ) {
				String questionText = questionMatcher.group( 1 ).trim();
				String typeStr = typeMatcher.group( 1 ).trim();
				String answer = answerMatcher.group( 1 ).trim();

				// Create a new question entity
				QuizQuestion question = new QuizQuestion();
				question.setQuiz( quiz );
				question.setQuestion( questionText );
				question.setCorrectAnswer( answer );
				question.setScore( 10 ); // Default score

				// Set the question type
				try {
					QuizType type = QuizType.valueOf( typeStr );
					question.setType( type );
					logger.debug( "Set question type to: {}", type );

					// Handle options for MCQ questions
					if ( type == QuizType.MCQ && optionsMatcher.find() ) {
						String optionsStr = optionsMatcher.group( 1 ).trim();
						logger.debug( "Set MCQ options: {}", optionsStr );
						question.setOptions( optionsStr );
					}
				}
				catch ( IllegalArgumentException e ) {
					// Default to TEXT if type is invalid
					logger.warn( "Invalid question type '{}', defaulting to TEXT", typeStr );
					question.setType( QuizType.TEXT );
				}

				questions.add( question );
			}
			else {
				logger.debug( "Could not extract all required components from block, skipping" );
			}
		}

		logger.info( "Successfully parsed {} questions from AI response for quiz: {}", questions.size(), quiz.getId() );
		return questions;
	}

	@Transactional
	public QuizDto addQuestionToQuiz( User user, UUID quizId, QuizQuestionDto questionDto ) {
		Quiz quiz = getByIdForUser( user, quizId );
		QuizQuestion question = questionDto.toEntity( quiz );

		QuizQuestion savedQuestion = quizQuestionRepository.save( question );
		logger.info( "Successfully added question with ID: {} to quiz: {}", savedQuestion.getId(), quizId );

		return QuizDto.from( quizRepository.findById( quizId ).orElseThrow() );
	}

	@Transactional
	public QuizDto submitAnswer( User user, UUID quizId, UUID questionId, QuizAnswerDto answerDto ) {
		if ( quizRepository.findById( quizId ).filter( quiz -> quiz.getUser().getId().equals( user.getId() ) ).isEmpty() ) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Quiz does not belong to this user" );
		}

		QuizQuestion question = quizQuestionRepository.findById( questionId )
				.orElseThrow( () -> new ResponseStatusException( HttpStatus.NOT_FOUND, "Question not found" ) );

		if ( !question.getQuiz().getId().equals( quizId ) ) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Question does not belong to this quiz" );
		}

		QuizAnswer answer = answerDto.toEntity( question );

		// Check if the answer is correct
		boolean isCorrect = question.getCorrectAnswer().equalsIgnoreCase( answer.getAnswer() );
		answer.setCorrect( isCorrect );
		logger.debug( "Answer is {}", isCorrect ? "correct" : "incorrect" );

		// Set the score based on correctness
		int score = isCorrect ? question.getScore() : 0;
		answer.setScore( score );
		logger.debug( "Set score to: {}", score );

		QuizAnswer savedAnswer = quizAnswerRepository.save( answer );
		logger.info( "Successfully submitted answer with ID: {} for question: {} in quiz: {}",
				savedAnswer.getId(), questionId, quizId );

		return QuizDto.from( quizRepository.findById( quizId ).orElseThrow() );
	}

	@Transactional
	public QuizDto completeQuiz( User user, UUID quizId ) {
		Quiz quiz = getByIdForUser( user, quizId );
		logger.debug( "Retrieved quiz with ID: {}", quizId );

		// Calculate total score
		List<QuizQuestion> questions = quizQuestionRepository.findByQuiz( quiz );
		logger.debug( "Found {} questions for quiz", questions.size() );

		int totalScore = 0;
		int totalPossibleScore = 0;

		for ( QuizQuestion question : questions ) {
			totalPossibleScore += question.getScore();
			if ( question.getAnswer() != null && question.getAnswer().isCorrect() ) {
				totalScore += question.getAnswer().getScore();
				logger.debug( "Question: {} - score: {}/{}", question.getId(), question.getAnswer().getScore(), question.getScore() );
			}
			else {
				logger.debug( "Question: {} - no correct answer", question.getId() );
			}
		}
		logger.debug( "Calculated raw score: {}/{}", totalScore, totalPossibleScore );

		quiz.setScore( totalScore );
		quiz.setCompleted( true );
		quiz.setEndedAt( LocalDateTime.now() );

		// Calculate duration in seconds
		if ( quiz.getStartedAt() != null ) {
			long durationSeconds = java.time.Duration.between( quiz.getStartedAt(), quiz.getEndedAt() ).getSeconds();
			quiz.setDuration( ( int ) durationSeconds );
			logger.debug( "Quiz duration: {} seconds", durationSeconds );
		}

		// Generate feedback based on score
		String feedback;
		double scorePercentage = totalPossibleScore > 0 ? ( double ) totalScore / totalPossibleScore * 100 : 0;
		logger.debug( "Score percentage: {}%", scorePercentage );

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
		logger.debug( "Generated feedback: {}", feedback );

		quiz.setFeedback( feedback );

		Quiz savedQuiz = quizRepository.save( quiz );
		logger.info( "Successfully completed quiz: {} with score: {}/{} ({}%)",
				quizId, totalScore, totalPossibleScore, scorePercentage );

		return QuizDto.from( savedQuiz );
	}

	@Transactional
	public boolean deleteQuiz( User user, UUID quizId ) {
		boolean result = quizRepository.findById( quizId )
				.filter( quiz -> quiz.getUser().getId().equals( user.getId() ) )
				.map( quiz -> {
					quizRepository.delete( quiz );
					logger.info( "Successfully deleted quiz with ID: {}", quizId );
					return true;
				} )
				.orElse( false );

		if ( !result ) {
			logger.warn( "Failed to delete quiz with ID: {} - quiz not found or user not authorized", quizId );
		}

		return result;
	}
}
