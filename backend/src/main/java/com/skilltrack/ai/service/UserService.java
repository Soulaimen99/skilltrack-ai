package com.skilltrack.ai.service;

import com.skilltrack.ai.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserLookupService userLookupService;

	public User getCurrentUser( Authentication auth ) {
		if ( auth instanceof JwtAuthenticationToken jwt ) {
			String username = jwt.getToken().getClaimAsString( "preferred_username" );
			String email = jwt.getToken().getClaimAsString( "email" );
			return userLookupService.get( username, email );
		}
		throw new IllegalArgumentException( "Invalid authentication token" );
	}
}
