package com.skilltrack.ai.repository;

import com.skilltrack.ai.entity.LearningGoal;
import com.skilltrack.ai.entity.Quiz;
import com.skilltrack.ai.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface QuizRepository extends JpaRepository<Quiz, UUID> {

	List<Quiz> findByUser( User user );

	List<Quiz> findByLearningGoal( LearningGoal learningGoal );

	List<Quiz> findByUserAndCompleted( User user, boolean completed );

	List<Quiz> findByLearningGoalAndCompleted( LearningGoal learningGoal, boolean completed );

	Page<Quiz> findByUser( User user, Pageable pageable );

	Page<Quiz> findByLearningGoal( LearningGoal learningGoal, Pageable pageable );

	Page<Quiz> findByUserAndStartedAtBetween( User user, LocalDateTime from, LocalDateTime to, Pageable pageable );

	Page<Quiz> findByUserAndCompletedAndStartedAtBetween( User user, boolean completed, LocalDateTime from, LocalDateTime to, Pageable pageable );
}