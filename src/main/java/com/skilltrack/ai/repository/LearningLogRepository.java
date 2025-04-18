package com.skilltrack.ai.repository;

import com.skilltrack.ai.entity.LearningLog;
import com.skilltrack.ai.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface LearningLogRepository extends JpaRepository<LearningLog, UUID> {

	Page<LearningLog> findByUserAndCreatedAtBetween( User user, LocalDateTime from, LocalDateTime to, Pageable pageable );

	Page<LearningLog> findByUser( User user, Pageable pageable );
}