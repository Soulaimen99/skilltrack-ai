package com.skilltrack.ai.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema( description = "Learning Log entry" )
@Entity
@Data
public class LearningLog {

	@Id
	@GeneratedValue
	private UUID id;

	@ManyToOne
	private User user;

	private String content;

	private String tags;

	private LocalDateTime date = LocalDateTime.now();
}
