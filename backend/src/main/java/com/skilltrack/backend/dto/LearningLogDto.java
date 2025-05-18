package com.skilltrack.backend.dto;

import com.skilltrack.backend.entity.LearningGoal;
import com.skilltrack.backend.entity.LearningLog;
import com.skilltrack.backend.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record LearningLogDto( UUID id, String username, String content, String tags, UUID goalId, String goalTitle,
                              LocalDateTime date ) {

	public static LearningLogDto from( LearningLog log ) {
		return new LearningLogDto(
				log.getId(),
				log.getUser().getUsername(),
				log.getContent(),
				log.getTags(),
				log.getGoal() != null ? log.getGoal().getId() : null,
				log.getGoal() != null ? log.getGoal().getTitle() : null,
				log.getCreatedAt() );
	}

	public static List<String> toContentList( List<LearningLogDto> logs ) {
		return logs.stream()
				.map( LearningLogDto::content )
				.filter( c -> c != null && !c.isBlank() )
				.toList();
	}

	public LearningLog toEntity( User user, LearningGoal goal ) {
		return new LearningLog( null, user, this.content, this.tags, goal, null );
	}

	public record PagedLogsResponse(
			List<LearningLogDto> content,
			int page,
			int size,
			int totalPages,
			long totalElements
	) {

	}
}
