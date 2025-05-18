package com.skilltrack.backend.service;

import com.skilltrack.backend.entity.User;
import com.skilltrack.backend.repository.SummaryUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SummaryUsageService {

	private final SummaryUsageRepository repo;

	@Transactional( propagation = Propagation.REQUIRES_NEW )
	public void insertNewUsage( User user ) {
		repo.insertNewUsage( UUID.randomUUID(), user.getId(), LocalDate.now() );
	}
}
