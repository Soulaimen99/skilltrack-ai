package com.skilltrack.ai.repository;

import com.skilltrack.ai.entity.Summary;
import com.skilltrack.ai.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface SummaryRepository extends JpaRepository<Summary, Long> {

	Page<Summary> findByUserAndCreatedAtBetween( User user, LocalDateTime from, LocalDateTime to, Pageable pageable );

	Page<Summary> findByUser( User user, Pageable pageable );
}
