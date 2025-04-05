package com.sou.api;

import java.time.LocalDate;
import java.util.List;

import com.sou.model.LearningLog;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@QuarkusTest
public class AiApiTest {
	
	@Test
	void testSummarizeLogs() {
		LearningLog log1 = new LearningLog();
		log1.setContent( "Learned Quarkus basics" );
		log1.setDate( LocalDate.now() );
		
		LearningLog log2 = new LearningLog();
		log2.setContent( "Explored JPA entities" );
		log2.setDate( LocalDate.now() );
		
		given()
				.contentType( ContentType.JSON )
				.body( List.of( log1, log2 ) )
				.when()
				.post( "/logs/summarize" )
				.then()
				.statusCode( 200 )
				.body( "summary", containsString( "Learned Quarkus basics" ) )
				.body( "summary", containsString( "Explored JPA entities" ) );
	}
}
