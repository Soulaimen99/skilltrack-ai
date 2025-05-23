package com.skilltrack.backend.repository;

import com.skilltrack.backend.entity.LearningGoal;
import com.skilltrack.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface LearningGoalRepository extends JpaRepository<LearningGoal, UUID> {

	List<LearningGoal> findByUser( User user );

	Page<LearningGoal> findByUser( User user, Pageable pageable );

	Page<LearningGoal> findByUserAndCreatedAtBetween( User user, LocalDateTime from, LocalDateTime to, Pageable pageable );
}
