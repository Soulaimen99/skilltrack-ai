package com.skilltrack.ai.repository;

import com.skilltrack.ai.model.LearningLog;
import com.skilltrack.ai.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LearningLogRepository extends JpaRepository<LearningLog, UUID> {
	List<LearningLog> findByUser( User user );
}
