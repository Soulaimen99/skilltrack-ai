package com.skilltrack.ai.dto;

import com.skilltrack.ai.model.LearningLog;

import java.time.LocalDateTime;
import java.util.UUID;

public record LearningLogDto( UUID id, String username, String content, String tags, LocalDateTime date ) {

	public static LearningLogDto from( LearningLog log ) {
		return new LearningLogDto(
				log.getId(),
				log.getUser().getUsername(),
				log.getContent(),
				log.getTags(),
				log.getDate()
		);
	}

	public LearningLog toEntity() {
		LearningLog log = new LearningLog();
		log.setContent( this.content );
		log.setTags( this.tags );
		log.setDate( this.date );
		return log;
	}
}
