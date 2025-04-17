package com.skilltrack.ai.controller;

import com.skilltrack.ai.model.User;
import com.skilltrack.ai.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest( AuthController.class )
@Import( AuthControllerTest.TestConfig.class )
public class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserService userService;

	@BeforeEach
	void setup() {
		Mockito.reset( userService );
	}

	@Test
	void testMeEndpoint() throws Exception {
		String username = "testuser";
		String email = "test@example.com";

		User user = new User();
		user.setUsername( username );
		user.setEmail( email );

		Mockito.when( userService.getOrUpdate( username, email ) ).thenReturn( user );

		Jwt jwt = Jwt.withTokenValue( "fake-token" )
				.header( "alg", "none" )
				.claim( "preferred_username", username )
				.claim( "email", email )
				.build();

		mockMvc.perform( get( "/api/auth/me" )
						.with( SecurityMockMvcRequestPostProcessors.jwt().jwt( jwt ) ) )
				.andExpect( status().isOk() )
				.andExpect( jsonPath( "$.username" ).value( username ) )
				.andExpect( jsonPath( "$.email" ).value( email ) );
	}

	@TestConfiguration
	static class TestConfig {

		@Bean
		public UserService userService() {
			return Mockito.mock( UserService.class );
		}
	}
}
