package com.skilltrack.ai.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table( name = "summary" )
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Summary {

	@Id
	@GeneratedValue
	private UUID id;

	@ManyToOne( optional = false )
	@JoinColumn( name = "user_id" )
	private User user;

	@Column( name = "username", nullable = false )
	private String username;

	@Column( columnDefinition = "TEXT" )
	private String content;

	@Column( name = "created_at" )
	private LocalDateTime createdAt = LocalDateTime.now();
}
