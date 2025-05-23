package com.skilltrack.backend.controller;

import com.skilltrack.backend.dto.LearningGoalDto;
import com.skilltrack.backend.dto.LearningInsightsDto;
import com.skilltrack.backend.dto.LearningLogDto;
import com.skilltrack.backend.dto.SummaryDto;
import com.skilltrack.backend.entity.User;
import com.skilltrack.backend.service.LearningGoalService;
import com.skilltrack.backend.service.LearningLogService;
import com.skilltrack.backend.service.SummaryService;
import com.skilltrack.backend.service.UserLookupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles( "test" )
@Import( ControllersTest.MockedBeans.class )
class ControllersTest {

	private final User testUser = new User( UUID.randomUUID(), "test_user", "test@example.com", null );

	@Autowired
	MockMvc mockMvc;

	@Autowired
	UserLookupService userLookupService;

	@Autowired
	LearningLogService learningLogService;

	@Autowired
	LearningGoalService learningGoalService;

	@Autowired
	SummaryService summaryService;

	@BeforeEach
	void setup() {
		when( userLookupService.get( any(), any() ) ).thenReturn( testUser );
		when( userLookupService.getById( testUser.getId() ) ).thenReturn( testUser );
	}

	@Test
	void testGetLogs() throws Exception {
		when( learningLogService.getPagedLogsResponse( any(), any(), anyInt(), anyInt(), any(), any( User.class ) ) )
				.thenReturn( new LearningLogDto.PagedLogsResponse( Collections.emptyList(), 0, 10, 1, 0 ) );

		mockMvc.perform( get( "/api/logs" )
						.with( jwt().jwt( jwt -> jwt
										.claim( "preferred_username", "test_user" )
										.claim( "email", "test@example.com" ) )
								.authorities( new SimpleGrantedAuthority( "ROLE_user" ) ) ) )
				.andExpect( status().isOk() );
	}

	@Test
	void testAdminAccessWithRole() throws Exception {
		when( learningLogService.getPagedLogsResponse( any(), any(), anyInt(), anyInt(), any(), any( User.class ) ) )
				.thenReturn( new LearningLogDto.PagedLogsResponse( Collections.emptyList(), 0, 10, 1, 0 ) );

		mockMvc.perform( get( "/api/admin/users/" + testUser.getId() + "/logs" )
						.with( jwt().jwt( jwt -> jwt
										.claim( "preferred_username", "admin" )
										.claim( "email", "admin@example.com" ) )
								.authorities( new SimpleGrantedAuthority( "ROLE_admin" ) ) ) )
				.andExpect( status().isOk() );
	}

	@Test
	void testAdminSummariesAccess_withAdminRole_shouldReturnOk() throws Exception {
		when( summaryService.getPagedSummariesResponse( any(), any(), anyInt(), anyInt(), any( User.class ) ) )
				.thenReturn( new SummaryDto.PagedSummariesResponse( Collections.emptyList(), 0, 10, 1, 0 ) );

		mockMvc.perform( get( "/api/admin/users/" + testUser.getId() + "/summaries" )
						.with( jwt().jwt( jwt -> jwt
										.claim( "preferred_username", "admin" )
										.claim( "email", "admin@example.com" ) )
								.authorities( new SimpleGrantedAuthority( "ROLE_admin" ) )
						) )
				.andExpect( status().isOk() );
	}

	@Test
	void testGetInsights() throws Exception {
		when( learningLogService.getInsights( any( User.class ) ) )
				.thenReturn( new LearningInsightsDto( 0, 0, null, 0 ) );

		mockMvc.perform( get( "/api/logs/insights" )
						.with( jwt().jwt( jwt -> jwt
										.claim( "preferred_username", "test_user" )
										.claim( "email", "test@example.com" ) )
								.authorities( new SimpleGrantedAuthority( "ROLE_user" ) ) )
				)
				.andExpect( status().isOk() );
	}

	@Test
	void testGetGoals() throws Exception {
		when( learningGoalService.getPagedGoalsResponse( any(), any(), anyInt(), anyInt(), any( User.class ) ) )
				.thenReturn( new LearningGoalDto.PagedGoalsResponse( Collections.emptyList(), 0, 10, 1, 0 ) );

		mockMvc.perform( get( "/api/goals" )
						.with( jwt().jwt( jwt -> jwt
										.claim( "preferred_username", "test_user" )
										.claim( "email", "test@example.com" ) )
								.authorities( new SimpleGrantedAuthority( "ROLE_user" ) ) )
				)
				.andExpect( status().isOk() );
	}

	@TestConfiguration
	static class MockedBeans {

		@Bean
		UserLookupService userLookupService() {
			return Mockito.mock( UserLookupService.class );
		}

		@Bean
		LearningLogService learningLogService() {
			return Mockito.mock( LearningLogService.class );
		}

		@Bean
		SummaryService summaryService() {
			return Mockito.mock( SummaryService.class );
		}

		@Bean
		LearningGoalService learningGoalService() {
			return Mockito.mock( LearningGoalService.class );
		}
	}
}
