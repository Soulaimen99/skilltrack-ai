package com.skilltrack.ai.service;

import com.skilltrack.ai.entity.SummaryUsage;
import com.skilltrack.ai.entity.User;
import com.skilltrack.ai.repository.SummaryUsageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@ExtendWith( MockitoExtension.class )
class SummaryTest {

	@Mock
	SummaryUsageRepository usageRepository;

	@Mock
	SummaryUsageInsertService insertService;

	@Mock
	OpenAiChatModel chatModel;

	@InjectMocks
	SummaryRateLimiter rateLimiter;

	SummaryService summaryService;

	User user;

	@BeforeEach
	void setUp() {
		summaryService = new SummaryService( chatModel, rateLimiter );
		user = new User( UUID.randomUUID(), "tester", "test@example.com", null );
		ReflectionTestUtils.setField( rateLimiter, "dailyLimit", 10 );
	}

	@Test
	void testRateLimiter_allowsIfTryIncrementSucceeds() {
		when( usageRepository.tryIncrement( eq( user ), any(), anyInt() ) ).thenReturn( 1 );
		assertTrue( rateLimiter.allow( user ) );
	}

	@Test
	void testRateLimiter_allowsIfInsertSucceedsAfterInitialFail() {
		when( usageRepository.tryIncrement( eq( user ), any(), anyInt() ) ).thenReturn( 0 ).thenReturn( 1 );
		doNothing().when( insertService ).insertNewUsage( eq( user ) );
		assertTrue( rateLimiter.allow( user ) );
	}

	@Test
	void testRateLimiter_retriesIfInsertFailsWithConstraint() {
		when( usageRepository.tryIncrement( eq( user ), any(), anyInt() ) ).thenReturn( 0 ).thenReturn( 1 );
		doThrow( DataIntegrityViolationException.class ).when( insertService ).insertNewUsage( eq( user ) );
		assertTrue( rateLimiter.allow( user ) );
	}

	@Test
	void testRemainingUsage() {
		LocalDate today = LocalDate.now();
		when( usageRepository.findByUserAndUsageDate( user, today ) ).thenReturn( Optional.of( new SummaryUsage( null, user, user.getUsername(), today, 3 ) ) );
		assertEquals( 7, rateLimiter.remaining( user ) );
	}

	@Test
	void testSummarizeWithLimitCheck_success() {
		when( usageRepository.tryIncrement( eq( user ), any(), anyInt() ) ).thenReturn( 1 );

		AssistantMessage message = new AssistantMessage( "mocked summary" );
		Generation generation = new Generation( message );
		ChatResponse response = new ChatResponse( List.of( generation ), new ChatResponseMetadata() );

		when( chatModel.call( any( Prompt.class ) ) ).thenReturn( response );

		SummaryService.SummaryResult result = summaryService.summarizeWithLimitCheck( user, List.of( "Log 1", "Log 2" ) );
		assertTrue( result.summary().contains( "mocked" ) );
		assertTrue( result.remaining() >= 0 );
	}
}
