package com.skilltrack.ai.dto;

import java.util.List;

public record PagedLogsResponse(
		List<LearningLogDto> content,
		int page,
		int size,
		int totalPages,
		long totalElements
) {

}
