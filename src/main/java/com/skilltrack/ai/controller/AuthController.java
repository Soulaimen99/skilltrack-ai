package com.skilltrack.ai.controller;

import com.skilltrack.ai.dto.UserDto;
import com.skilltrack.ai.entity.User;
import com.skilltrack.ai.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/api/auth" )
public class AuthController {

	private final UserService userService;

	public AuthController( UserService userService ) {
		this.userService = userService;
	}

	@GetMapping( "/me" )
	public ResponseEntity<UserDto> me( @AuthenticationPrincipal Jwt jwt ) {
		String username = jwt.getClaimAsString( "preferred_username" );
		String email = jwt.getClaimAsString( "email" );
		User user = userService.getOrCreate( username, email );
		
		return ResponseEntity.ok( UserDto.from( user ) );
	}
}
