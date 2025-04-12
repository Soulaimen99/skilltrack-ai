package com.skilltrack.ai.repository;

import com.skilltrack.ai.model.Summary;
import com.skilltrack.ai.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SummaryRepository extends JpaRepository<Summary, Long> {
	List<Summary> findByUserOrderByCreatedAtDesc( User user );

}
