package com.skilltrack.backend.dto;

import java.util.List;
import java.util.UUID;

public record InstructionRequestDto(
		UUID goalId,
		List<LearningLogDto> logs
) {

}
