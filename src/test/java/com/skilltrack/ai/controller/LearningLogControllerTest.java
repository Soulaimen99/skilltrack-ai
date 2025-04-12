package com.skilltrack.ai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skilltrack.ai.model.LearningLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@WithMockUser( username = "testuser" )
	void shouldReturnEmptyLogsInitially() throws Exception {
		mockMvc.perform( get( "/logs" ) )
				.andExpect( status().isOk() )
				.andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) );
	}

	@Test
	@WithMockUser( username = "testuser" )
	void shouldAddAndReturnLog() throws Exception {
		LearningLog log = new LearningLog();
		log.setContent( "Test log content" );
		log.setTags( "test" );
		log.setDate( LocalDate.from( LocalDateTime.now() ) );

		String body = objectMapper.writeValueAsString( log );

		mockMvc.perform( post( "/logs" )
						.contentType( MediaType.APPLICATION_JSON )
						.content( body ) )
				.andExpect( status().isOk() )
				.andExpect( jsonPath( "$.content" ).value( "Test log content" ) )
				.andExpect( jsonPath( "$.tags" ).value( "test" ) );
	}

	@Test
	@WithMockUser( username = "testuser" )
	void shouldDeleteLogIfExists() throws Exception {
		// First add a log
		LearningLog log = new LearningLog();
		log.setContent( "To be deleted" );
		log.setTags( "delete" );
		log.setDate( LocalDate.from( LocalDateTime.now() ) );

		String body = objectMapper.writeValueAsString( log );

		String response = mockMvc.perform( post( "/logs" )
						.contentType( MediaType.APPLICATION_JSON )
						.content( body ) )
				.andExpect( status().isOk() )
				.andReturn().getResponse().getContentAsString();

		long logId = objectMapper.readTree( response ).get( "id" ).asLong();

		// Delete it
		mockMvc.perform( delete( "/logs/" + logId ) )
				.andExpect( status().isOk() );
	}
}
