package com.skilltrack.backend.config;

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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	private static final String[] SWAGGER_WHITELIST = {
			"/swagger-ui.html",
			"/swagger-ui/**",
			"/v3/api-docs/**",
			"/v3/api-docs",
			"/webjars/**"
	};

	@Bean
	public SecurityFilterChain securityFilterChain( HttpSecurity http ) throws Exception {
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
			Map<String, Object> realmAccess = jwt.getClaim( "realm_access" );
			if ( realmAccess == null || !( realmAccess.get( "roles" ) instanceof List<?> roles ) ) {
				return Collections.emptyList();
			}

			return roles.stream()
					.filter( role -> role instanceof String )
					.map( role -> new SimpleGrantedAuthority( "ROLE_" + role ) )
					.collect( Collectors.toList() );
		} );

		return converter;
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
