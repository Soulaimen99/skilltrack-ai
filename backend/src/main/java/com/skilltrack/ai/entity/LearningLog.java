package com.skilltrack.ai.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table( name = "learning_log" )
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LearningLog {

	@Id
	@GeneratedValue
	private UUID id;

	@ManyToOne( optional = false )
	@JoinColumn( name = "user_id" )
	private User user;

	private String content;

	private String tags;

	@ManyToOne
	@JoinColumn( name = "goal_id" )
	private LearningGoal goal;

	@Column( name = "created_at", nullable = false )
	private LocalDateTime createdAt;

	@PrePersist
	public void prePersist() {
		if ( createdAt == null ) {
			createdAt = LocalDateTime.now();
		}
	}
}
