package com.skilltrack.backend.entity;

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
@Table( name = "instruction" )
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Instruction {

	@Id
	@GeneratedValue
	private UUID id;

	@ManyToOne( optional = false )
	@JoinColumn( name = "user_id" )
	private User user;

	@ManyToOne( optional = false )
	@JoinColumn( name = "goal_id" )
	private LearningGoal learningGoal;

	@Column( nullable = false, columnDefinition = "TEXT" )
	private String advice;

	@Column( name = "created_at" )
	private LocalDateTime createdAt;

	@PrePersist
	public void prePersist() {
		if ( createdAt == null ) {
			createdAt = LocalDateTime.now();
		}
	}
}
