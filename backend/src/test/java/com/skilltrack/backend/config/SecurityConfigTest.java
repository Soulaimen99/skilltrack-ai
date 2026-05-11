package com.skilltrack.backend.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityConfigTest {

	private final SecurityConfig securityConfig = new SecurityConfig();

	@Test
	void jwtAuthenticationConverter_mapsRealmRoles() {
		Jwt jwt = Jwt.withTokenValue( "token" )
				.header( "alg", "none" )
				.claim( "realm_access", Map.of( "roles", List.of( "user", "admin" ) ) )
				.build();

		AbstractAuthenticationToken auth = securityConfig.jwtAuthenticationConverter().convert( jwt );

		assertTrue( auth.getAuthorities().contains( new SimpleGrantedAuthority( "ROLE_user" ) ) );
		assertTrue( auth.getAuthorities().contains( new SimpleGrantedAuthority( "ROLE_admin" ) ) );
	}

	@Test
	void jwtAuthenticationConverter_mapsClientRoles() {
		Jwt jwt = Jwt.withTokenValue( "token" )
				.header( "alg", "none" )
				.claim( "resource_access", Map.of(
						"skilltrack-client", Map.of( "roles", List.of( "user" ) ),
						"skilltrack-frontend", Map.of( "roles", List.of( "admin" ) )
				) )
				.build();

		AbstractAuthenticationToken auth = securityConfig.jwtAuthenticationConverter().convert( jwt );

		assertTrue( auth.getAuthorities().contains( new SimpleGrantedAuthority( "ROLE_user" ) ) );
		assertTrue( auth.getAuthorities().contains( new SimpleGrantedAuthority( "ROLE_admin" ) ) );
	}
}
