package com.sou.repository;

import com.sou.model.entity.LearningLog;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class LearningLogRepository implements PanacheRepository<LearningLog> {

	public List<LearningLog> retrieveUserLogs( Long userid ) {
		return list( "user.id", userid );
	}

}
