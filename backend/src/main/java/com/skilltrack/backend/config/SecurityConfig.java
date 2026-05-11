package com.skilltrack.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	private static final Logger logger = LoggerFactory.getLogger( SecurityConfig.class );

	private static final String[] SWAGGER_WHITELIST = {
			"/swagger-ui.html",
			"/swagger-ui/**",
			"/v3/api-docs/**",
			"/v3/api-docs",
			"/webjars/**"
	};

	@Bean
	public SecurityFilterChain securityFilterChain( HttpSecurity http ) throws Exception {
		logger.info( "Configuring SecurityFilterChain with swagger whitelist and API protection" );

		http
				.cors( withDefaults() )
				.csrf( AbstractHttpConfigurer::disable )
				.authorizeHttpRequests( auth -> auth
						.requestMatchers( SWAGGER_WHITELIST ).permitAll()
						.requestMatchers( "/api/**" ).authenticated()
						.anyRequest().authenticated()
				)
				.oauth2ResourceServer( oauth2 -> oauth2
						.jwt( jwt -> jwt.jwtAuthenticationConverter( jwtAuthenticationConverter() ) )
				);

		return http.build();
	}

	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

		converter.setJwtGrantedAuthoritiesConverter( ( Jwt jwt ) -> {
			Set<String> roles = new LinkedHashSet<>();

			addRolesFromClaim( roles, jwt.getClaim( "realm_access" ) );
			addClientRolesFromClaim( roles, jwt.getClaim( "resource_access" ) );

			return roles.stream()
					.map( role -> new SimpleGrantedAuthority( "ROLE_" + role ) )
					.collect( Collectors.toList() );
		} );

		logger.debug( "Configured JwtAuthenticationConverter to extract granted authorities from JWT" );
		return converter;
	}

	private void addRolesFromClaim( Set<String> roles, Object claim ) {
		if ( !( claim instanceof Map<?, ?> access ) ) {
			return;
		}

		Object rawRoles = access.get( "roles" );
		if ( rawRoles instanceof List<?> list ) {
			list.stream()
					.filter( role -> role instanceof String )
					.map( role -> ( String ) role )
					.forEach( roles::add );
		}
	}

	private void addClientRolesFromClaim( Set<String> roles, Object claim ) {
		if ( !( claim instanceof Map<?, ?> resourceAccess ) ) {
			return;
		}

		resourceAccess.values().forEach( clientAccess -> addRolesFromClaim( roles, clientAccess ) );
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings( @NonNull CorsRegistry registry ) {
				registry.addMapping( "/**" )
						.allowedOrigins( "http://localhost:3000" )
						.allowedMethods( "GET", "POST", "PUT", "DELETE", "OPTIONS" )
						.allowedHeaders( "*" )
						.exposedHeaders( "Authorization" )
						.allowCredentials( true )
						.maxAge( 3600 );
			}
		};
	}
}
