package com.skilltrack.ai.repository;

import com.skilltrack.ai.entity.LearningLog;
import com.skilltrack.ai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LearningLogRepository extends JpaRepository<LearningLog, UUID> {

	List<LearningLog> findByUser( User user );
}