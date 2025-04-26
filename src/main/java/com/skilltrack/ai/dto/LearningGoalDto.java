package com.skilltrack.ai.dto;

import com.skilltrack.ai.entity.LearningGoal;
import com.skilltrack.ai.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record LearningGoalDto( UUID id, String username, String title, String description, LocalDateTime date ) {

	public static LearningGoalDto from( LearningGoal goal ) {
		return new LearningGoalDto( goal.getId(), goal.getUser().getUsername(), goal.getTitle(), goal.getDescription(), goal.getCreatedAt() );
	}

	public LearningGoal toEntity( User user ) {
		return new LearningGoal( null, user, user.getUsername(), this.title, this.description, null );
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
