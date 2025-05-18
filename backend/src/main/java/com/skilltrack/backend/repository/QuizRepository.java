package com.skilltrack.backend.repository;

import com.skilltrack.backend.entity.LearningGoal;
import com.skilltrack.backend.entity.Quiz;
import com.skilltrack.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface QuizRepository extends JpaRepository<Quiz, UUID> {

	List<Quiz> findByUser( User user );

	List<Quiz> findByLearningGoal( LearningGoal learningGoal );

	Page<Quiz> findByUser( User user, Pageable pageable );

	Page<Quiz> findByUserAndCompletedAndStartedAtBetween( User user, boolean completed, LocalDateTime from, LocalDateTime to, Pageable pageable );
}