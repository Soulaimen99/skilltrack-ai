package com.skilltrack.backend.service;

import com.skilltrack.backend.dto.LearningLogDto;
import com.skilltrack.backend.dto.SummaryDto;
import com.skilltrack.backend.entity.LearningGoal;
import com.skilltrack.backend.entity.SummaryUsage;
import com.skilltrack.backend.entity.User;
import com.skilltrack.backend.repository.InstructionRepository;
import com.skilltrack.backend.repository.LearningGoalRepository;
import com.skilltrack.backend.repository.SummaryRepository;
import com.skilltrack.backend.repository.SummaryUsageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith( MockitoExtension.class )
class ServicesTest {

	@Mock
	OpenAiChatModel chatModel;

	@Mock
	SummaryUsageRepository summaryUsageRepository;

	@Mock
	SummaryRepository summaryRepository;

	@Mock
	SummaryUsageService summaryUsageService;

	@Mock
	InstructionRepository instructionRepository;

	@Mock
	LearningGoalRepository learningGoalRepository;

	SummaryService summaryService;

	InstructionService instructionService;

	LearningGoalService learningGoalService;

	User user;

	@BeforeEach
	void setUp() {
		summaryService = new SummaryService( chatModel, summaryRepository, summaryUsageRepository, summaryUsageService );
		instructionService = new InstructionService( chatModel, instructionRepository );
		learningGoalService = new LearningGoalService( learningGoalRepository );
		user = new User( UUID.randomUUID(), "tester", "test@example.com", null );
		ReflectionTestUtils.setField( summaryService, "dailyLimit", 10 );
	}

	@Test
	void testAllow_whenTryIncrementSucceeds() {
		when( summaryUsageRepository.tryIncrement( eq( user ), any(), eq( 10 ) ) ).thenReturn( 1 );
		assertTrue( summaryService.allow( user ) );
	}

	@Test
	void testAllow_whenInsertThenRetrySucceeds() {
		when( summaryUsageRepository.tryIncrement( eq( user ), any(), eq( 10 ) ) ).thenReturn( 0 ).thenReturn( 1 );
		doNothing().when( summaryUsageService ).insertNewUsage( eq( user ) );
		assertTrue( summaryService.allow( user ) );
		verify( summaryUsageService, times( 1 ) ).insertNewUsage( eq( user ) );
	}

	@Test
	void testAllow_whenInsertFails_thenRetrySucceeds() {
		when( summaryUsageRepository.tryIncrement( eq( user ), any(), eq( 10 ) ) )
				.thenReturn( 0 )
				.thenReturn( 1 );

		doThrow( DataIntegrityViolationException.class ).when( summaryUsageService ).insertNewUsage( user );

		assertTrue( summaryService.allow( user ) );
		verify( summaryUsageService, times( 1 ) ).insertNewUsage( user );
	}

	@Test
	void testRemaining_withExistingUsage() {
		when( summaryUsageRepository.findByUserAndUsageDate( eq( user ), any() ) )
				.thenReturn( Optional.of( new SummaryUsage( null, user, LocalDate.now(), 3 ) ) );

		assertEquals( 7, summaryService.remaining( user ) );
	}

	@Test
	void testRemaining_withNoUsageYet() {
		when( summaryUsageRepository.findByUserAndUsageDate( eq( user ), any() ) )
				.thenReturn( Optional.empty() );

		assertEquals( 10, summaryService.remaining( user ) );
	}

	@Test
	void testSummarizeWithLimitCheck_success() {
		when( summaryUsageRepository.tryIncrement( eq( user ), any(), eq( 10 ) ) ).thenReturn( 1 );

		AssistantMessage message = new AssistantMessage( "mocked summary" );
		Generation generation = new Generation( message );
		ChatResponse response = new ChatResponse( List.of( generation ), new ChatResponseMetadata() );
		when( chatModel.call( any( Prompt.class ) ) ).thenReturn( response );

		List<LearningLogDto> logs = List.of(
				new LearningLogDto( null, user.getUsername(), "Learned Java Streams", "java", null, null, null ),
				new LearningLogDto( null, user.getUsername(), "Practiced Spring Boot", "spring", null, null, null )
		);
		when( summaryRepository.save( any() ) ).thenAnswer( invocation -> invocation.getArgument( 0 ) );
		SummaryDto summaryDto = summaryService.summarizeWithLimitCheck( user, logs );

		assertEquals( "mocked summary", summaryDto.content() );
		assertEquals( user.getUsername(), summaryDto.username() );
		assertNotNull( summaryDto.createdAt() );
	}

	@Test
	void testGenerateInstruction_success() {
		when( chatModel.call( any( Prompt.class ) ) )
				.thenReturn( new ChatResponse(
						List.of( new Generation( new AssistantMessage( "Mocked advice" ) ) ),
						new ChatResponseMetadata()
				) );
		when( instructionRepository.save( any() ) )
				.thenAnswer( invocation -> invocation.getArgument( 0 ) );

		var goal = new LearningGoal( UUID.randomUUID(), user, "Learn React", "Frontend goal", null );
		var logs = List.of(
				new LearningLogDto( null, user.getUsername(), "Studied hooks", "react", null, null, null )
		);

		var result = instructionService.generateInstruction( user, goal, logs );

		assertNotNull( result );
		assertEquals( user.getUsername(), result.username() );
		assertEquals( "Mocked advice", result.advice() );
	}

	@Test
	void testGetAllInstructions_emptyList() {
		when( instructionRepository.findByUser( user ) ).thenReturn( List.of() );

		var result = instructionService.getAllInstructions( user );

		assertNotNull( result );
		assertTrue( result.isEmpty() );
	}

	@Test
	void testAddAndGetGoal_success() {
		var goal = new LearningGoal( UUID.randomUUID(), user, "Learn Spring", "Backend goal", null );
		when( learningGoalRepository.save( any() ) ).thenReturn( goal );

		var saved = learningGoalService.addGoal( goal );

		assertNotNull( saved );
		assertEquals( "Learn Spring", saved.getTitle() );
	}

	@Test
	void testDeleteGoal_success() {
		var goal = new LearningGoal( UUID.randomUUID(), user, "Learn Python", "New goal", null );
		when( learningGoalRepository.findById( goal.getId() ) ).thenReturn( Optional.of( goal ) );

		var result = learningGoalService.deleteGoal( user, goal.getId() );

		assertTrue( result );
	}

	@Test
	void testDeleteGoal_notFound() {
		when( learningGoalRepository.findById( any() ) ).thenReturn( Optional.empty() );

		var result = learningGoalService.deleteGoal( user, UUID.randomUUID() );

		assertFalse( result );
	}
}
