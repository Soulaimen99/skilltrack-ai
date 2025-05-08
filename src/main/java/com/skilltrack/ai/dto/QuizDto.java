package com.skilltrack.ai.dto;

import com.skilltrack.ai.entity.LearningGoal;
import com.skilltrack.ai.entity.Quiz;
import com.skilltrack.ai.entity.User;

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

	public Quiz toEntity( User user, LearningGoal learningGoal ) {
		Quiz quiz = new Quiz();
		quiz.setUser( user );
		quiz.setLearningGoal( learningGoal );
		quiz.setScore( this.score );
		quiz.setDuration( this.duration );
		quiz.setFeedback( this.feedback );
		quiz.setCompleted( this.completed );
		quiz.setStartedAt( this.startedAt != null ? this.startedAt : LocalDateTime.now() );
		quiz.setEndedAt( this.endedAt );
		return quiz;
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
