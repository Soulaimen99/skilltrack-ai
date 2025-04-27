package com.skilltrack.ai.dto;

import com.skilltrack.ai.entity.Instruction;

import java.time.LocalDateTime;
import java.util.UUID;

public record InstructionDto( UUID id, String username, String advice, LocalDateTime createdAt ) {

	public static InstructionDto from( Instruction instruction ) {
		return new InstructionDto(
				instruction.getId(),
				instruction.getUser().getUsername(),
				instruction.getAdvice(),
				instruction.getCreatedAt()
		);
	}
}

