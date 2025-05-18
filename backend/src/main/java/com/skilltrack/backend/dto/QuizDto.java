package com.skilltrack.backend.dto;

import com.skilltrack.backend.entity.Quiz;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record QuizDto(
		UUID id,
		UUID userId,
		String username,
		UUID goalId,
		String goalTitle,
		int score,
		int duration,
		String feedback,
		boolean completed,
		LocalDateTime startedAt,
		LocalDateTime endedAt,
		List<QuizQuestionDto> questions
) {

	public static QuizDto from( Quiz quiz ) {
		return new QuizDto(
				quiz.getId(),
				quiz.getUser().getId(),
				quiz.getUser().getUsername(),
				quiz.getLearningGoal().getId(),
				quiz.getLearningGoal().getTitle(),
				quiz.getScore(),
				quiz.getDuration(),
				quiz.getFeedback(),
				quiz.isCompleted(),
				quiz.getStartedAt(),
				quiz.getEndedAt(),
				quiz.getQuizQuestions() != null
						? quiz.getQuizQuestions().stream().map( QuizQuestionDto::from ).toList()
						: List.of()
		);
	}

	public record PagedQuizzesResponse(
			List<QuizDto> content,
			int page,
			int size,
			int totalPages,
			long totalElements
	) {

	}
}
