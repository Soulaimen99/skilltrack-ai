package com.skilltrack.ai.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table( name = "quiz" )
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {

	@Id
	@GeneratedValue
	private UUID id;

	@ManyToOne( optional = false )
	@JoinColumn( name = "user_id" )
	private User user;

	@ManyToOne( optional = false )
	@JoinColumn( name = "goal_id" )
	private LearningGoal learningGoal;

	@OneToMany( mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true )
	private List<QuizQuestion> quizQuestions;

	private int score;

	private int duration;

	private String feedback;

	private boolean completed;

	@Column( name = "started_at" )
	private LocalDateTime startedAt;

	@Column( name = "ended_at" )
	private LocalDateTime endedAt;

	@PrePersist
	public void prePersist() {
		if ( startedAt == null ) {
			startedAt = LocalDateTime.now();
		}
	}
}
