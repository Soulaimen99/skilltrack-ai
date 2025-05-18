package com.skilltrack.backend.dto;

import com.skilltrack.backend.entity.LearningGoal;
import com.skilltrack.backend.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record LearningGoalDto( UUID id, String username, String title, String description, LocalDateTime date ) {

	public static LearningGoalDto from( LearningGoal goal ) {
		return new LearningGoalDto( goal.getId(), goal.getUser().getUsername(), goal.getTitle(), goal.getDescription(), goal.getCreatedAt() );
	}

	public LearningGoal toEntity( User user ) {
		return new LearningGoal( null, user, this.title, this.description, null );
	}

	public record PagedGoalsResponse(
			List<LearningGoalDto> content,
			int page,
			int size,
			int totalPages,
			long totalElements
	) {

	}
}
