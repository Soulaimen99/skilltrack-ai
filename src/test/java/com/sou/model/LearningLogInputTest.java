package com.sou.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class LearningLogInputTest {
	
	@Test
	void testContentField() {
		LearningLogInput input = new LearningLogInput();
		input.setContent( "Read about OpenAPI" );
		assertEquals( "Read about OpenAPI", input.getContent() );
	}
	
	@Test
	void testConstructorInitialization() {
		LearningLogInput input1 = new LearningLogInput( "Content only" );
		assertEquals( "Content only", input1.getContent() );
		assertNull( input1.getTags() );
		
		LearningLogInput input2 = new LearningLogInput( "Content and tags", "java" );
		assertEquals( "Content and tags", input2.getContent() );
		assertEquals( "java", input2.getTags() );
		
		assertNotNull( input2.getDate(), "Date should be initialized to the current date by default" );
	}
	
	@Test
	void testEqualsAndHashCode() {
		LearningLogInput input1 = new LearningLogInput( "test" );
		LearningLogInput input2 = new LearningLogInput( "test" );
		
		assertEquals( input1, input2, "Objects with identical content should be equal" );
		assertEquals( input1.hashCode(), input2.hashCode(), "HashCodes should match for identical objects" );
	}
}