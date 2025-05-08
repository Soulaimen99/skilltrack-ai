package com.skilltrack.ai.dto;

import com.skilltrack.ai.entity.Quiz;
import com.skilltrack.ai.entity.QuizQuestion;
import com.skilltrack.ai.entity.QuizType;

import java.time.LocalDateTime;
import java.util.UUID;

public record QuizQuestionDto(
		UUID id,
		UUID quizId,
		QuizType type,
		String question,
		String options,
		String correctAnswer,
		int score,
		int duration,
		LocalDateTime createdAt,
		QuizAnswerDto answer
) {

	public static QuizQuestionDto from( QuizQuestion quizQuestion ) {
		return new QuizQuestionDto(
				quizQuestion.getId(),
				quizQuestion.getQuiz().getId(),
				quizQuestion.getType(),
				quizQuestion.getQuestion(),
				quizQuestion.getOptions(),
				quizQuestion.getCorrectAnswer(),
				quizQuestion.getScore(),
				quizQuestion.getDuration(),
				quizQuestion.getCreatedAt(),
				quizQuestion.getAnswer() != null ? QuizAnswerDto.from( quizQuestion.getAnswer() ) : null
		);
	}

	public QuizQuestion toEntity( Quiz quiz ) {
		QuizQuestion quizQuestion = new QuizQuestion();
		quizQuestion.setQuiz( quiz );
		quizQuestion.setType( this.type );
		quizQuestion.setQuestion( this.question );
		quizQuestion.setOptions( this.options );
		quizQuestion.setCorrectAnswer( this.correctAnswer );
		quizQuestion.setScore( this.score );
		quizQuestion.setDuration( this.duration );
		quizQuestion.setCreatedAt( this.createdAt != null ? this.createdAt : LocalDateTime.now() );
		return quizQuestion;
	}
}
