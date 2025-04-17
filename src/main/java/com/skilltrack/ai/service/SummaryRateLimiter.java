package com.skilltrack.ai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SummaryRateLimiter {

	private final Map<String, UserSummaryLimit> usageMap = new ConcurrentHashMap<>();

	@Value( "${summary.limit.daily:10}" )
	private int dailyLimit;

	public synchronized boolean allow( String username ) {
		LocalDate today = LocalDate.now();
		UserSummaryLimit usage = usageMap.computeIfAbsent( username, u -> new UserSummaryLimit() );

		if ( usage.date == null || !usage.date.equals( today ) ) {
			usage.date = today;
			usage.count = 0;
		}

		if ( usage.count >= dailyLimit ) {
			return false;
		}

		usage.count++;
		return true;
	}

	public int remaining( String username ) {
		UserSummaryLimit usage = usageMap.get( username );
		if ( usage == null || !LocalDate.now().equals( usage.date ) ) return dailyLimit;
		return Math.max( 0, dailyLimit - usage.count );
	}

	private static class UserSummaryLimit {

		int count;

		LocalDate date;
	}
}
