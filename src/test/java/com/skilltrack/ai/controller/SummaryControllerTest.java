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
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles( "test" )
public class SummaryControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@WithMockUser( username = "testuser" )
	void shouldGenerateSummaryFromLogs() throws Exception {
		LearningLog log = new LearningLog();
		log.setContent( "Learned H2 testing" );
		log.setTags( "springboot" );
		log.setDate( LocalDate.from( LocalDateTime.now() ) );

		String logsJson = objectMapper.writeValueAsString( List.of( log ) );

		mockMvc.perform( post( "/summaries" )
						.contentType( MediaType.APPLICATION_JSON )
						.content( logsJson ) )
				.andExpect( status().isOk() )
				.andExpect( jsonPath( "$.summary" ).exists() )
				.andExpect( jsonPath( "$.summary" ).value( org.hamcrest.Matchers.containsString( "Learned H2 testing" ) ) );
	}
}
