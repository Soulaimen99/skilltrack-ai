package com.skilltrack.ai.dto;

import com.skilltrack.ai.entity.LearningLog;
import com.skilltrack.ai.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

public record LearningLogDto( UUID id, String username, String content, String tags, LocalDateTime date ) {

	public static LearningLogDto from( LearningLog log ) {
		return new LearningLogDto( log.getId(), log.getUser().getUsername(), log.getContent(), log.getTags(), log.getCreatedAt() );
	}

	public LearningLog toEntity( User user ) {
		return new LearningLog( null, user, user.getUsername(), this.content, this.tags, LocalDateTime.now() );
	}
}
