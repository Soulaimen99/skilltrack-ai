package com.sou.api;

import com.sou.model.dto.LearningLogInput;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.sou.api.Authentication.PASSWORD;
import static com.sou.api.Authentication.USERNAME;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;


@QuarkusTest
public class AiApiTest {

	@Test
	void testSummarizeLogs() {
		LearningLogInput log1 = new LearningLogInput( "Learned Quarkus basics", null );
		LearningLogInput log2 = new LearningLogInput( "Explored JPA entities", null );

		given()
				.auth().basic( USERNAME, PASSWORD )
				.contentType( ContentType.JSON )
				.body( List.of( log1, log2 ) )
				.when()
				.post( "/logs/summarize" )
				.then()
				.statusCode( 200 )
				.contentType( ContentType.JSON )
				.body( "summary", containsString( "Learned Quarkus basics" ) )
				.body( "summary", containsString( "Explored JPA entities" ) );
	}

	@Test
	void testSummarizeLogsEmptyInput() {
		given()
				.auth().basic( USERNAME, PASSWORD )
				.contentType( ContentType.JSON )
				.body( List.of() ) // Empty logs
				.when()
				.post( "/logs/summarize" )
				.then()
				.statusCode( 400 )
				.body( equalTo( "Logs cannot be null or empty" ) );

	}
}