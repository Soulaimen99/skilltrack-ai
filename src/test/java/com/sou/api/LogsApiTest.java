package com.sou.api;

import com.sou.model.LearningLogInput;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

@QuarkusTest
public class LogsApiTest {
	
	@Test
	void testAddLog() {
		given()
				.contentType( ContentType.JSON )
				.body( new LearningLogInput( "Studied JPA and Quarkus" ) )
				.when()
				.post( "/logs" )
				.then()
				.statusCode( 200 )
				.body( "content", equalTo( "Studied JPA and Quarkus" ) );
	}
	
	@Test
	void testGetLogs() {
		given()
				.contentType( ContentType.JSON )
				.body( new LearningLogInput( "Test log before get" ) )
				.when()
				.post( "/logs" )
				.then()
				.statusCode( 200 );
		
		given()
				.when()
				.get( "/logs" )
				.then()
				.statusCode( 200 )
				.body( "$", not( empty() ) );
	}
}
