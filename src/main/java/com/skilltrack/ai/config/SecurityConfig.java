package com.skilltrack.ai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
						.requestMatchers( "/logs/**", "/api/auth/**" ).authenticated()
						.anyRequest().authenticated()
				)
				.oauth2ResourceServer( oauth2 -> oauth2
						.jwt( jwt -> jwt.jwtAuthenticationConverter( jwtAuthenticationConverter() ) )
				);
		return http.build();
	}

	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
		converter.setAuthorityPrefix( "ROLE_" );
		converter.setAuthoritiesClaimName( "realm_access.roles" );

		JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
		jwtConverter.setJwtGrantedAuthoritiesConverter( converter );
		return jwtConverter;
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
