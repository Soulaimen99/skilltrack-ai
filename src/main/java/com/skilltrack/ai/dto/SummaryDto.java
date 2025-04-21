package com.skilltrack.ai.dto;

import com.skilltrack.ai.entity.Summary;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record SummaryDto( UUID id, String username, String content, LocalDateTime createdAt ) {

	public static SummaryDto from( Summary summary ) {
		return new SummaryDto(
				summary.getId(),
				summary.getUsername(),
				summary.getContent(),
				summary.getCreatedAt()
		);
	}

	public record PagedSummariesResponse(
			List<SummaryDto> content,
			int page,
			int size,
			int totalPages,
			long totalElements
	) {

	}
}
