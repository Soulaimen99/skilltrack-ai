package com.sou.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import com.sou.model.LearningLog;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class LearningLogRepository implements PanacheRepository<LearningLog> {
	
	@Transactional
	public void deleteTestData() {
		delete( "tags = ?1", "TEST" );
	}
}
