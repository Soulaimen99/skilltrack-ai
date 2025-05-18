package com.skilltrack.backend.dto;

public record LearningInsightsDto(
		int logsLast7Days,
		int logsLast30Days,
		String mostUsedTag,
		int daysSinceLastLog
) {

}
