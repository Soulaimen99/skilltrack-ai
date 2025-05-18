package com.skilltrack.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table( name = "quiz_answer" )
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizAnswer {

	@Id
	@GeneratedValue
	private UUID id;

	@OneToOne
	@JoinColumn( name = "quiz_question_id", nullable = false )
	private QuizQuestion quizQuestion;

	@NotNull
	private String answer;

	private int score;

	private boolean correct;

	@Column( name = "attempted_at", nullable = false )
	private LocalDateTime attemptedAt;

	@PrePersist
	public void prePersist() {
		if ( attemptedAt == null ) {
			attemptedAt = LocalDateTime.now();
		}
	}
}
