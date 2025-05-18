package com.skilltrack.ai.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table( name = "quiz_question" )
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestion {

	@Id
	@GeneratedValue
	private UUID id;

	@ManyToOne
	@JoinColumn( name = "quiz_id", nullable = false )
	private Quiz quiz;

	@Enumerated( EnumType.STRING )
	private QuizType type;

	@NotNull
	private String question;

	@Column
	private String options;

	@OneToOne( mappedBy = "quizQuestion", cascade = CascadeType.ALL, orphanRemoval = true )
	@Nullable
	private QuizAnswer answer;

	@Column( name = "correct_answer" )
	private String correctAnswer;

	private int score;

	private int duration;

	@Column( name = "created_at", nullable = false )
	private LocalDateTime createdAt;

	@PrePersist
	public void prePersist() {
		if ( createdAt == null ) {
			createdAt = LocalDateTime.now();
		}
	}
}
