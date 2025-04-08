package com.sou.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LearningLogInput {
	
	@NotNull
	private String content;
	
	@Size( max = 255 )
	private String tags;
	
}