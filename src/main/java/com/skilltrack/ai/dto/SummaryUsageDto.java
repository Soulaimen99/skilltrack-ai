package com.skilltrack.ai.dto;

import com.skilltrack.ai.entity.SummaryUsage;

import java.time.LocalDate;

public record SummaryUsageDto( String username, LocalDate usageDate, int count ) {

	public static SummaryUsageDto from( SummaryUsage usage ) {
		return new SummaryUsageDto(
				usage.getUser().getUsername(),
				usage.getUsageDate(),
				usage.getCount()
		);
	}
}
