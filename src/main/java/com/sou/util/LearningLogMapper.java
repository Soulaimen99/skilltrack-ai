package com.sou.util;

import com.sou.model.dto.LearningLogOutput;
import com.sou.model.entity.LearningLog;

public class LearningLogMapper {

	private LearningLogMapper() {
		// Private constructor to prevent instantiation
	}

	public static LearningLogOutput toOutput( LearningLog entity ) {
		if ( entity == null ) {
			return null;
		}

		return LearningLogOutput.builder()
				.id( entity.getId() )
				.username( entity.getUser().getUsername() )
				.content( entity.getContent() )
				.tags( entity.getTags() )
				.date( entity.getDate() )
				.build();
	}
}