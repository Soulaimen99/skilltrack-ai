package com.skilltrack.ai.repository;

import com.skilltrack.ai.entity.LearningLog;
import com.skilltrack.ai.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface LearningLogRepository extends JpaRepository<LearningLog, UUID> {

	List<LearningLog> findByUser( User user );

	Page<LearningLog> findByUser( User user, Pageable pageable );

	Page<LearningLog> findByUserAndCreatedAtBetween( User user, LocalDateTime from, LocalDateTime to, Pageable pageable );

	Page<LearningLog> findByUserAndGoalId( User user, UUID goalId, Pageable pageable );

	Page<LearningLog> findByUserAndGoalIdAndCreatedAtBetween( User user, UUID goalId, LocalDateTime from, LocalDateTime to, Pageable pageable );

	List<LearningLog> findByUserAndCreatedAtAfter( User user, LocalDateTime after );

	@Query( "SELECT MAX(l.createdAt) FROM LearningLog l WHERE l.user = :user" )
	LocalDateTime findLastLogDateByUser( @Param( "user" ) User user );
}
