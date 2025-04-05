package com.sou.model;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LearningLogTest {
	
	@Test
	void testFields() {
		LearningLog log = new LearningLog();
		log.setId( 1L );
		log.setContent( "Test content" );
		log.setTags( "java,test" );
		log.setDate( LocalDate.of( 2025, 4, 5 ) );
		
		assertEquals( 1L, log.getId() );
		assertEquals( "Test content", log.getContent() );
		assertEquals( "java,test", log.getTags() );
		assertEquals( LocalDate.of( 2025, 4, 5 ), log.getDate() );
	}
	
	@Test
	void testEquality() {
		LearningLog a = new LearningLog();
		a.setId( 1L );
		a.setContent( "Test" );
		a.setTags( "x" );
		a.setDate( LocalDate.now() );
		
		LearningLog b = new LearningLog();
		b.setId( 1L );
		b.setContent( "Test" );
		b.setTags( "x" );
		b.setDate( a.getDate() );
		
		assertEquals( a, b );
		assertEquals( a.hashCode(), b.hashCode() );
	}
}
