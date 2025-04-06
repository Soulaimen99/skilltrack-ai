package com.sou.repository;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import com.sou.model.LearningLog;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class LearningLogRepository implements PanacheRepository<LearningLog> {
	
	@Transactional
	public void deleteTestData() {
		delete( "tags = ?1", "TEST_DATA" );
	}
	
	public List<LearningLog> retrieveUserLogs( Long userid ) {
		return list( "user.id", userid );
	}
}
