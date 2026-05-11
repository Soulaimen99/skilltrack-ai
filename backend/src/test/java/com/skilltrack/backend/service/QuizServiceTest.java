package com.skilltrack.backend.service;

import com.skilltrack.backend.dto.QuizDto;
import com.skilltrack.backend.entity.LearningGoal;
import com.skilltrack.backend.entity.Quiz;
import com.skilltrack.backend.entity.QuizQuestion;
import com.skilltrack.backend.entity.User;
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

	private List<QuizQuestion> toList( Iterable<QuizQuestion> iterable ) {
		return iterable == null
				? List.of()
				: java.util.stream.StreamSupport.stream( iterable.spliterator(), false ).toList();
	}
}
