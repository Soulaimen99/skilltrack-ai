package com.sou.model;

import java.time.LocalDate;

import com.sou.model.entity.LearningLog;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class LearningLogTest {
	
	@Test
	void testFieldInitialization() {
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
		LocalDate today = LocalDate.now();
		
		LearningLog log1 = new LearningLog();
		log1.setId( 1L );
		log1.setContent( "Test" );
		log1.setTags( "tag" );
		log1.setDate( today );
		
		LearningLog log2 = new LearningLog();
		log2.setId( 1L );
		log2.setContent( "Test" );
		log2.setTags( "tag" );
		log2.setDate( today );
		
		assertEquals( log1, log2, "Logs with identical fields should be equal" );
		assertEquals( log1.hashCode(), log2.hashCode(), "HashCodes should match for identical objects" );
	}
	
	@Test
	void testMissingOptionalFields() {
		LearningLog log = new LearningLog();
		log.setContent( "Content only" );
		
		assertNull( log.getTags() );
		assertNull( log.getDate() );
	}
}