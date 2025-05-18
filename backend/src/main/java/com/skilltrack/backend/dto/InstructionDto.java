package com.skilltrack.backend.dto;

import com.skilltrack.backend.entity.Instruction;

import java.time.LocalDateTime;
import java.util.UUID;

public record InstructionDto( UUID id, String username, UUID goalId, String advice, LocalDateTime createdAt ) {

	public static InstructionDto from( Instruction instruction ) {
		return new InstructionDto(
				instruction.getId(),
				instruction.getUser().getUsername(),
				instruction.getLearningGoal().getId(),
				instruction.getAdvice(),
				instruction.getCreatedAt()
		);
	}
}

