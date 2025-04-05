package com.sou.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LearningLogInputTest {
	
	@Test
	void testContentField() {
		LearningLogInput input = new LearningLogInput();
		input.setContent( "Read about OpenAPI" );
		assertEquals( "Read about OpenAPI", input.getContent() );
	}
	
	@Test
	void testConstructor() {
		LearningLogInput input1 = new LearningLogInput( "Construct with content" );
		assertEquals( "Construct with content", input1.getContent() );
		LearningLogInput input2 = new LearningLogInput( "Construct with content and tags", "java" );
		assertEquals( "Construct with content and tags", input2.getContent() );
		assertEquals( "java", input2.getTags() );
	}
	
	@Test
	void testEqualsAndHashCode() {
		LearningLogInput a = new LearningLogInput( "test" );
		LearningLogInput b = new LearningLogInput( "test" );
		assertEquals( a, b );
		assertEquals( a.hashCode(), b.hashCode() );
	}
}
