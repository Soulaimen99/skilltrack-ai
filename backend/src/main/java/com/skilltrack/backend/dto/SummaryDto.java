package com.skilltrack.backend.dto;

import com.skilltrack.backend.entity.Summary;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record SummaryDto( UUID id, String username, String content, LocalDateTime createdAt ) {

	public static SummaryDto from( Summary summary ) {
		return new SummaryDto(
				summary.getId(),
				summary.getUser().getUsername(),
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
