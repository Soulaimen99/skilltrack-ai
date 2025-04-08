package com.sou.model.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LearningLogOutput {
	
	@NotNull
	private Long id;
	
	@NotNull
	private String username;
	
	@NotNull
	private String content;
	
	private String tags;
	
	@NotNull
	private LocalDate date;
	
}