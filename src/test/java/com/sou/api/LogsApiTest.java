package com.sou.api;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.sou.model.LearningLogInput;
import com.sou.repository.LearningLogRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static com.sou.api.Authentication.PASSWORD;
import static com.sou.api.Authentication.USERNAME;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

@QuarkusTest
public class LogsApiTest {
	
	@Inject
	LearningLogRepository logRepo;
	
	@AfterEach
	@Transactional
	public void cleanUpTestData() {
		logRepo.deleteTestData();
	}
	
	@Test
	void testAddLog() {
		given()
				.auth().basic( USERNAME, PASSWORD )
				.contentType( ContentType.JSON )
				.body( new LearningLogInput( "Studied JPA and Quarkus", "TEST_DATA" ) )
				.when()
				.post( "/logs" )
				.then()
				.statusCode( 200 )
				.contentType( ContentType.JSON )
				.body( "content", equalTo( "Studied JPA and Quarkus" ) )
				.body( "tags", equalTo( "TEST_DATA" ) );
	}
	
	@Test
	void testAddInvalidLog() {
		given()
				.auth().basic( USERNAME, PASSWORD )
				.contentType( ContentType.JSON )
				.body( new LearningLogInput( null ) )
				.when()
				.post( "/logs" )
				.then()
				.statusCode( 400 )
				.body( equalTo( "Content must not be null or blank" ) );
		
	}
	
	@Test
	void testGetLogs() {
		given()
				.auth().basic( USERNAME, PASSWORD )
				.contentType( ContentType.JSON )
				.body( new LearningLogInput( "Test log before get", "TEST_DATA" ) )
				.when()
				.post( "/logs" )
				.then()
				.statusCode( 200 );
		
		given()
				.auth().basic( USERNAME, PASSWORD )
				.when()
				.get( "/logs" )
				.then()
				.statusCode( 200 )
				.contentType( ContentType.JSON )
				.body( "$", not( empty() ) ); // Ensure logs are returned
	}
}