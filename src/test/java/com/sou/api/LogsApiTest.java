package com.sou.api;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.sou.model.LearningLogInput;
import com.sou.repository.LearningLogRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

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
				.contentType( ContentType.JSON )
				.body( new LearningLogInput( "Studied JPA and Quarkus", "TEST" ) )
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
				.body( new LearningLogInput( "Test log before get", "TEST" ) )
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
