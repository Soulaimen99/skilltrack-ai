package com.sou.api;

import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class AbstractDatabaseTest {

	private static final String DEFAULT_DB_NAME = "skilltrack";
	private static final String DEFAULT_DB_USER = "skilluser";
	private static final String DEFAULT_DB_PASSWORD = "fireball";

	@Container
	protected static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
			new PostgreSQLContainer<>( "postgres:15" )
					.withDatabaseName( System.getenv( "DB_NAME" ) != null ? System.getenv( "DB_NAME" ) : DEFAULT_DB_NAME )
					.withUsername( System.getenv( "DB_USER" ) != null ? System.getenv( "DB_USER" ) : DEFAULT_DB_USER )
					.withPassword( System.getenv( "DB_PASSWORD" ) != null ? System.getenv( "DB_PASSWORD" ) : DEFAULT_DB_PASSWORD )
					.withClasspathResourceMapping( "db/schema.sql", "/docker-entrypoint-initdb.d/schema.sql", org.testcontainers.containers.BindMode.READ_ONLY )
					.withClasspathResourceMapping( "db/init.sql", "/docker-entrypoint-initdb.d/init.sql", org.testcontainers.containers.BindMode.READ_ONLY );

	@BeforeAll
	public static void startContainerAndSetProperties() {
		System.setProperty( "quarkus.datasource.jdbc.url", POSTGRES_CONTAINER.getJdbcUrl() );
		System.setProperty( "quarkus.datasource.username", POSTGRES_CONTAINER.getUsername() );
		System.setProperty( "quarkus.datasource.password", POSTGRES_CONTAINER.getPassword() );
	}
}