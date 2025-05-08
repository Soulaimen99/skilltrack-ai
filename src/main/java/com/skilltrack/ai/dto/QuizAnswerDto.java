package com.skilltrack.ai.dto;

import com.skilltrack.ai.entity.QuizAnswer;
import com.skilltrack.ai.entity.QuizQuestion;

import java.time.LocalDateTime;
import java.util.UUID;

public record QuizAnswerDto(
		UUID id,
		UUID questionId,
		String answer,
		int score,
		boolean correct,
		LocalDateTime attemptedAt
) {

	public static QuizAnswerDto from( QuizAnswer quizAnswer ) {
		return new QuizAnswerDto(
				quizAnswer.getId(),
				quizAnswer.getQuizQuestion().getId(),
				quizAnswer.getAnswer(),
				quizAnswer.getScore(),
				quizAnswer.isCorrect(),
				quizAnswer.getAttemptedAt()
		);
	}

	public QuizAnswer toEntity( QuizQuestion quizQuestion ) {
		QuizAnswer quizAnswer = new QuizAnswer();
		quizAnswer.setQuizQuestion( quizQuestion );
		quizAnswer.setAnswer( this.answer );
		quizAnswer.setScore( this.score );
		quizAnswer.setCorrect( this.correct );
		quizAnswer.setAttemptedAt( this.attemptedAt != null ? this.attemptedAt : LocalDateTime.now() );
		return quizAnswer;
	}
}