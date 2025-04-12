package com.skilltrack.ai.dto;

import java.time.LocalDateTime;

public record SummaryDto(
		Long id,
		String username,
		String content,
		LocalDateTime createdAt
) {
}
