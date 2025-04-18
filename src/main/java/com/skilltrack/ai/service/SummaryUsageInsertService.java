package com.skilltrack.ai.service;

import com.skilltrack.ai.entity.User;
import com.skilltrack.ai.repository.SummaryUsageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class SummaryUsageInsertService {

	private final SummaryUsageRepository repo;

	public SummaryUsageInsertService( SummaryUsageRepository repo ) {
		this.repo = repo;
	}

	@Transactional( propagation = Propagation.REQUIRES_NEW )
	public void insertNewUsage( User user ) {
		repo.insertNewUsage( UUID.randomUUID(), user.getId(), user.getUsername(), LocalDate.now() );
	}
}