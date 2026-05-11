package com.skilltrack.backend.service;

import com.skilltrack.backend.dto.QuizDto;
import com.skilltrack.backend.dto.QuizAnswerDto;
import com.skilltrack.backend.entity.LearningGoal;
import com.skilltrack.backend.entity.Quiz;
import com.skilltrack.backend.entity.QuizAnswer;
import com.skilltrack.backend.entity.QuizQuestion;
import com.skilltrack.backend.entity.User;
import com.skilltrack.backend.entity.QuizType;
import com.skilltrack.backend.repository.QuizAnswerRepository;
import com.skilltrack.backend.repository.QuizQuestionRepository;
import com.skilltrack.backend.repository.QuizRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith( MockitoExtension.class )
class QuizServiceTest {

	@Mock
	QuizRepository quizRepository;

	@Mock
	QuizQuestionRepository quizQuestionRepository;

	@Mock
	QuizAnswerRepository quizAnswerRepository;

	@Mock
	LearningGoalService learningGoalService;

	@Mock
	OpenAiChatModel chatModel;

	QuizService quizService;

	User user;

	@BeforeEach
	void setUp() {
		quizService = new QuizService( quizRepository, quizQuestionRepository, quizAnswerRepository, learningGoalService, chatModel );
		user = new User( UUID.randomUUID(), "tester", "test@example.com", null );
	}

	@Test
	void createQuiz_usesFallbackQuestions_whenAiResponseIsInvalid() {
		LearningGoal goal = new LearningGoal( UUID.randomUUID(), user, "Learn Spring", "Understand the Spring Boot basics", null );
		Quiz savedQuiz = new Quiz( UUID.randomUUID(), user, goal, null, 0, 0, null, false, null, null );

		when( learningGoalService.getByIdForUser( user, goal.getId() ) ).thenReturn( goal );
		when( quizRepository.save( any( Quiz.class ) ) ).thenReturn( savedQuiz );
		when( chatModel.call( any( Prompt.class ) ) ).thenReturn( new ChatResponse(
				List.of( new Generation( new AssistantMessage( "not a valid quiz format" ) ) ),
				new ChatResponseMetadata()
		) );

		when( quizQuestionRepository.saveAll( any( Iterable.class ) ) ).thenAnswer( invocation -> invocation.getArgument( 0 ) );

		QuizDto result = quizService.createQuiz( user, goal.getId() );

		assertNotNull( result );
		assertNotNull( savedQuiz.getQuizQuestions() );
		assertNotNull( result.questions() );
		assertEquals( 3, result.questions().size() );

		ArgumentCaptor<Iterable<QuizQuestion>> questionsCaptor = ArgumentCaptor.forClass( Iterable.class );
		verify( quizQuestionRepository ).saveAll( questionsCaptor.capture() );
		List<QuizQuestion> questions = toList( questionsCaptor.getValue() );
		assertEquals( 3, questions.size() );
		assertEquals( goal.getId(), questions.get( 0 ).getQuiz().getLearningGoal().getId() );
		assertEquals( "MCQ", questions.get( 0 ).getType().name() );
		assertEquals( "BOOLEAN", questions.get( 1 ).getType().name() );
		assertEquals( "TEXT", questions.get( 2 ).getType().name() );
	}

	@Test
	void submitAnswer_marksTextAnswerCorrectWhenAiApprovesSemanticMatch() {
		UUID quizId = UUID.randomUUID();
		UUID questionId = UUID.randomUUID();
		LearningGoal goal = new LearningGoal( UUID.randomUUID(), user, "Learn AI ethics", "Understand ethical concerns in agentic AI", null );
		Quiz quiz = new Quiz( quizId, user, goal, List.of(), 0, 0, null, false, null, null );
		QuizQuestion question = new QuizQuestion();
		question.setId( questionId );
		question.setQuiz( quiz );
		question.setType( QuizType.TEXT );
		question.setQuestion( "Describe a potential ethical concern associated with the deployment of agentic AI in society." );
		question.setCorrectAnswer( "One potential ethical concern is the risk of decision-making bias and unfair outcomes." );
		question.setScore( 10 );
		quiz.setQuizQuestions( List.of( question ) );

		when( quizRepository.findById( quizId ) ).thenReturn( Optional.of( quiz ) );
		when( quizQuestionRepository.findById( questionId ) ).thenReturn( Optional.of( question ) );
		when( chatModel.call( any( Prompt.class ) ) ).thenReturn( new ChatResponse(
				List.of( new Generation( new AssistantMessage( "CORRECT" ) ) ),
				new ChatResponseMetadata()
		) );
		when( quizAnswerRepository.save( any( QuizAnswer.class ) ) ).thenAnswer( invocation -> invocation.getArgument( 0 ) );

		QuizDto result = quizService.submitAnswer(
				user,
				quizId,
				questionId,
				new QuizAnswerDto( null, questionId, "I could go out of control", 0, false, null )
		);

		assertNotNull( result );
		ArgumentCaptor<QuizAnswer> answerCaptor = ArgumentCaptor.forClass( QuizAnswer.class );
		verify( quizAnswerRepository ).save( answerCaptor.capture() );
		assertEquals( "I could go out of control", answerCaptor.getValue().getAnswer() );
		assertEquals( true, answerCaptor.getValue().isCorrect() );
		assertEquals( 10, answerCaptor.getValue().getScore() );
	}

	@Test
	void submitAnswer_marksTextAnswerCorrectWhenHeuristicMatchesConcernKeyword() {
		UUID quizId = UUID.randomUUID();
		UUID questionId = UUID.randomUUID();
		LearningGoal goal = new LearningGoal( UUID.randomUUID(), user, "Learn AI ethics", "Understand ethical concerns in agentic AI", null );
		Quiz quiz = new Quiz( quizId, user, goal, List.of(), 0, 0, null, false, null, null );
		QuizQuestion question = new QuizQuestion();
		question.setId( questionId );
		question.setQuiz( quiz );
		question.setType( QuizType.TEXT );
		question.setQuestion( "Describe a key ethical consideration when developing agentic AI systems." );
		question.setCorrectAnswer( "Ensuring accountability for decisions made by AI, particularly in areas like safety, privacy, and bias." );
		question.setScore( 10 );
		quiz.setQuizQuestions( List.of( question ) );

		when( quizRepository.findById( quizId ) ).thenReturn( Optional.of( quiz ) );
		when( quizQuestionRepository.findById( questionId ) ).thenReturn( Optional.of( question ) );
		when( chatModel.call( any( Prompt.class ) ) ).thenReturn( new ChatResponse(
				List.of( new Generation( new AssistantMessage( "INCORRECT" ) ) ),
				new ChatResponseMetadata()
		) );
		when( quizAnswerRepository.save( any( QuizAnswer.class ) ) ).thenAnswer( invocation -> invocation.getArgument( 0 ) );

		QuizDto result = quizService.submitAnswer(
				user,
				quizId,
				questionId,
				new QuizAnswerDto( null, questionId, "It could go out of control", 0, false, null )
		);

		assertNotNull( result );
		ArgumentCaptor<QuizAnswer> answerCaptor = ArgumentCaptor.forClass( QuizAnswer.class );
		verify( quizAnswerRepository ).save( answerCaptor.capture() );
		assertEquals( true, answerCaptor.getValue().isCorrect() );
	}

	@Test
	void submitAnswer_savesAnswer_forSameQuestion() {
		UUID quizId = UUID.randomUUID();
		UUID questionId = UUID.randomUUID();
		LearningGoal goal = new LearningGoal( UUID.randomUUID(), user, "Learn Spring", "Understand Spring basics", null );
		Quiz quiz = new Quiz( quizId, user, goal, List.of(), 0, 0, null, false, null, null );
		QuizQuestion question = new QuizQuestion();
		question.setId( questionId );
		question.setQuiz( quiz );
		question.setType( QuizType.BOOLEAN );
		question.setQuestion( "Is Spring a Java framework?" );
		question.setCorrectAnswer( "true" );
		question.setScore( 10 );

		when( quizRepository.findById( quizId ) ).thenReturn( Optional.of( quiz ) );
		when( quizQuestionRepository.findById( questionId ) ).thenReturn( Optional.of( question ) );
		when( quizAnswerRepository.save( any( QuizAnswer.class ) ) ).thenAnswer( invocation -> invocation.getArgument( 0 ) );

		QuizDto result = quizService.submitAnswer(
				user,
				quizId,
				questionId,
				new QuizAnswerDto( null, questionId, "true", 0, false, null )
		);

		assertNotNull( result );
		verify( quizAnswerRepository ).save( any( QuizAnswer.class ) );
	}

	private List<QuizQuestion> toList( Iterable<QuizQuestion> iterable ) {
		return iterable == null
				? List.of()
				: java.util.stream.StreamSupport.stream( iterable.spliterator(), false ).toList();
	}
}
