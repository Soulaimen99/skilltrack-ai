package com.skilltrack.ai.repository;

import com.skilltrack.ai.model.LearningLog;
import com.skilltrack.ai.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LearningLogRepository extends JpaRepository<LearningLog, Long> {
	List<LearningLog> findByUser( User user );
}
