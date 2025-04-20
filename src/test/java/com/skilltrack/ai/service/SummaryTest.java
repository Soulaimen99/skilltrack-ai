package com.skilltrack.ai.service;

import com.skilltrack.ai.entity.SummaryUsage;
import com.skilltrack.ai.entity.User;
import com.skilltrack.ai.repository.SummaryRepository;
import com.skilltrack.ai.repository.SummaryUsageRepository;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith( MockitoExtension.class )
class SummaryTest {

	@Mock
	OpenAiChatModel chatModel;

	@Mock
	SummaryUsageRepository summaryUsageRepository;

	@Mock
	SummaryRepository summaryRepository;

	@Mock
	SummaryUsageService summaryUsageService;

	SummaryService summaryService;

	User user;

	@BeforeEach
	void setUp() {
		summaryService = new SummaryService( chatModel, summaryRepository, summaryUsageRepository, summaryUsageService );
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
				.thenReturn( Optional.of( new SummaryUsage( null, user, user.getUsername(), LocalDate.now(), 3 ) ) );

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

		SummaryService.SummaryResult result = summaryService.summarizeWithLimitCheck( user, List.of( "Log 1", "Log 2" ) );
		assertTrue( result.summary().contains( "mocked" ) );
		assertTrue( result.remaining() >= 0 );
	}
}
