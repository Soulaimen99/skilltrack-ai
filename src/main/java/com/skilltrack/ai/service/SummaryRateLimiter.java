package com.skilltrack.ai.service;

import com.skilltrack.ai.entity.User;
import com.skilltrack.ai.repository.SummaryUsageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SummaryRateLimiter {

	private final SummaryUsageRepository repo;

	private final SummaryUsageInsertService insertService;

	@Value( "${summary.limit.daily:10}" )
	private int dailyLimit;

	public SummaryRateLimiter( SummaryUsageRepository repo, SummaryUsageInsertService insertService ) {
		this.repo = repo;
		this.insertService = insertService;
	}

	public boolean allow( User user ) {
		LocalDate today = LocalDate.now();
		if ( repo.tryIncrement( user, today, dailyLimit ) > 0 ) return true;

		try {
			insertService.insertNewUsage( user );
			return true;
		}
		catch ( DataIntegrityViolationException e ) {
			return repo.tryIncrement( user, today, dailyLimit ) > 0;
		}
	}

	public int remaining( User user ) {
		return repo.findByUserAndUsageDate( user, LocalDate.now() )
				.map( u -> Math.max( 0, dailyLimit - u.getCount() ) )
				.orElse( dailyLimit );
	}
}
