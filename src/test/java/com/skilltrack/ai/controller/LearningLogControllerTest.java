package com.skilltrack.ai.controller;

import com.skilltrack.ai.model.LearningLog;
import com.skilltrack.ai.model.User;
import com.skilltrack.ai.service.LearningLogService;
import com.skilltrack.ai.service.SummaryRateLimiter;
import com.skilltrack.ai.service.SummaryService;
import com.skilltrack.ai.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest( LearningLogController.class )
@Import( LearningLogControllerTest.TestConfig.class )
public class LearningLogControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private LearningLogService learningLogService;

	@Autowired
	private SummaryService summaryService;

	@Autowired
	private SummaryRateLimiter summaryRateLimiter;

	@Autowired
	private UserService userService;

	@BeforeEach
	void setup() {
		Mockito.reset( learningLogService, summaryService, summaryRateLimiter, userService );
	}

	private Jwt getMockJwt( String username, String email ) {
		return Jwt.withTokenValue( "fake-token" )
				.header( "alg", "none" )
				.claim( "preferred_username", username )
				.claim( "email", email )
				.build();
	}

	@Test
	void testGetLogsReturnsList() throws Exception {
		String username = "testuser";
		String email = "test@example.com";
		User user = new User();
		user.setUsername( username );
		user.setEmail( email );

		LearningLog log = new LearningLog();
		log.setContent( "Studied testing" );
		log.setUser( user );

		Mockito.when( userService.getOrUpdate( username, email ) ).thenReturn( user );
		Mockito.when( learningLogService.getLogs( Mockito.eq( user ), Mockito.any(), Mockito.any() ) )
				.thenReturn( List.of( log ) );

		mockMvc.perform( get( "/logs" )
						.with( SecurityMockMvcRequestPostProcessors.jwt().jwt( getMockJwt( username, email ) ) ) )
				.andExpect( status().isOk() )
				.andExpect( jsonPath( "$[0].content" ).value( "Studied testing" ) );
	}

	@Test
	void testAddLogCreatesLog() throws Exception {
		String username = "testuser";
		String email = "test@example.com";
		User user = new User();
		user.setUsername( username );
		user.setEmail( email );

		LearningLog inputLog = new LearningLog();
		inputLog.setContent( "New log" );
		inputLog.setUser( user );

		Mockito.when( userService.getOrUpdate( username, email ) ).thenReturn( user );
		Mockito.when( learningLogService.addLog( Mockito.eq( user ), Mockito.any() ) ).thenReturn( inputLog );

		mockMvc.perform( post( "/logs" )
						.contentType( MediaType.APPLICATION_JSON )
						.content( """
								{
								    "content": "New log"
								}
								""" )
						.with( SecurityMockMvcRequestPostProcessors.jwt().jwt( getMockJwt( username, email ) ) ) )
				.andExpect( status().isCreated() )
				.andExpect( jsonPath( "$.content" ).value( "New log" ) );
	}

	@Test
	void testDeleteLog() throws Exception {
		String username = "testuser";
		String email = "test@example.com";
		UUID id = UUID.randomUUID();
		User user = new User();
		user.setUsername( username );
		user.setEmail( email );

		Mockito.when( userService.getOrUpdate( username, email ) ).thenReturn( user );
		Mockito.when( learningLogService.deleteLog( user, id ) ).thenReturn( true );

		mockMvc.perform( delete( "/logs/" + id )
						.with( SecurityMockMvcRequestPostProcessors.jwt().jwt( getMockJwt( username, email ) ) ) )
				.andExpect( status().isNoContent() );
	}

	@Test
	void testSummarizeLogsLimitExceeded() throws Exception {
		String username = "testuser";
		String email = "test@example.com";
		User user = new User();
		user.setUsername( username );
		user.setEmail( email );

		Mockito.when( userService.getOrUpdate( username, email ) ).thenReturn( user );
		Mockito.when( summaryRateLimiter.allow( username ) ).thenReturn( false );

		mockMvc.perform( post( "/logs/summarize" )
						.contentType( MediaType.APPLICATION_JSON )
						.content( """
								[
								    { "content": "Line 1" },
								    { "content": "Line 2" }
								]
								""" )
						.with( SecurityMockMvcRequestPostProcessors.jwt().jwt( getMockJwt( username, email ) ) ) )
				.andExpect( status().isTooManyRequests() )
				.andExpect( jsonPath( "$.error" ).exists() );
	}

	@TestConfiguration
	static class TestConfig {

		@Bean
		public LearningLogService learningLogService() {
			return Mockito.mock( LearningLogService.class );
		}

		@Bean
		public SummaryService summaryService() {
			return Mockito.mock( SummaryService.class );
		}

		@Bean
		public SummaryRateLimiter summaryRateLimiter() {
			return Mockito.mock( SummaryRateLimiter.class );
		}

		@Bean
		public UserService userService() {
			return Mockito.mock( UserService.class );
		}
	}
}
