package com.skilltrack.backend.repository;

import com.skilltrack.backend.entity.SummaryUsage;
import com.skilltrack.backend.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface SummaryUsageRepository extends JpaRepository<SummaryUsage, Long> {

	Optional<SummaryUsage> findByUserAndUsageDate( User user, LocalDate usageDate );

	@Modifying
	@Transactional
	@Query( """
			    UPDATE SummaryUsage s
			    SET s.count = s.count + 1
			    WHERE s.user = :user AND s.usageDate = :date AND s.count < :limit
			""" )
	int tryIncrement( @Param( "user" ) User user, @Param( "date" ) LocalDate date, @Param( "limit" ) int limit );

	@Modifying
	@Transactional
	@Query( value = "INSERT INTO summary_usage (id, user_id, usage_date, count) VALUES (:id, :userId, :date, 1)", nativeQuery = true )
	void insertNewUsage( @Param( "id" ) UUID id, @Param( "userId" ) UUID userId, @Param( "date" ) LocalDate date );
}
