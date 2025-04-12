package com.skilltrack.ai.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skilltrack.ai.model.LearningLog;
import com.skilltrack.ai.model.User;
import com.skilltrack.ai.service.LearningLogService;
import com.skilltrack.ai.service.SummaryService;
import com.skilltrack.ai.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles( "test" )
public class LearningLogControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private LearningLogService learningLogService;

	@MockBean
	private UserService userService;

	@MockBean
	private SummaryService summaryService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@WithMockUser( username = "testuser" )
	void shouldReturnEmptyLogsInitially() throws Exception {
		when( userService.getOrCreate( "testuser" ) ).thenReturn( new User() );
		when( learningLogService.getLogs( any() ) ).thenReturn( List.of() );

		mockMvc.perform( get( "/logs" ) )
				.andExpect( status().isOk() )
				.andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) );
	}

	@Test
	@WithMockUser( username = "testuser" )
	void shouldAddAndReturnLog() throws Exception {
		String content = "Test log content";
		String tags = "test";

		User mockUser = new User();
		LearningLog mockLog = createMockLog( 1L, content, tags );

		when( userService.getOrCreate( "testuser" ) ).thenReturn( mockUser );
		when( learningLogService.addLog( eq( mockUser ), any() ) ).thenReturn( mockLog );

		String body = generateLearningLogJson( content, tags );

		mockMvc.perform( post( "/logs" )
						.contentType( MediaType.APPLICATION_JSON )
						.content( body ) )
				.andExpect( status().isOk() )
				.andExpect( jsonPath( "$.content" ).value( content ) )
				.andExpect( jsonPath( "$.tags" ).value( tags ) );
	}

	@Test
	@WithMockUser( username = "testuser" )
	void shouldDeleteLogIfExists() throws Exception {
		String content = "To be deleted";
		String tags = "delete";

		User mockUser = new User();
		LearningLog mockLog = createMockLog( 99L, content, tags );

		when( userService.getOrCreate( "testuser" ) ).thenReturn( mockUser );
		when( learningLogService.addLog( eq( mockUser ), any() ) ).thenReturn( mockLog );

		String body = generateLearningLogJson( content, tags );

		String response = mockMvc.perform( post( "/logs" )
						.contentType( MediaType.APPLICATION_JSON )
						.content( body ) )
				.andExpect( status().isOk() )
				.andReturn().getResponse().getContentAsString();

		long logId = objectMapper.readTree( response ).get( "id" ).asLong();

		mockMvc.perform( delete( "/logs/" + logId ) )
				.andExpect( status().isOk() );
	}

	@Test
	@WithMockUser( username = "testuser" )
	void shouldGenerateSummaryFromLogs() throws Exception {
		LearningLog log = createMockLog( null, "Learned H2 testing", "springboot" );

		String logsJson = objectMapper.writeValueAsString( List.of( log ) );

		when( summaryService.summarize( eq( "testuser" ), anyList() ) )
				.thenReturn( "testuser has been learning about H2 testing with Spring Boot." );

		mockMvc.perform( post( "/logs/summarize" )
						.contentType( MediaType.APPLICATION_JSON )
						.content( logsJson ) )
				.andExpect( status().isOk() )
				.andExpect( jsonPath( "$.summary" ).exists() )
				.andExpect( jsonPath( "$.summary" ).isString() )
				.andExpect( jsonPath( "$.summary", containsString( "H2 testing" ) ) );
	}

	private String generateLearningLogJson( String content, String tags ) throws JsonProcessingException {
		LearningLog log = new LearningLog();
		log.setContent( content );
		log.setTags( tags );
		log.setDate( LocalDate.now() );
		return objectMapper.writeValueAsString( log );
	}

	private LearningLog createMockLog( Long id, String content, String tags ) {
		LearningLog log = new LearningLog();
		log.setId( id );
		log.setContent( content );
		log.setTags( tags );
		log.setDate( LocalDate.now() );
		return log;
	}
}
