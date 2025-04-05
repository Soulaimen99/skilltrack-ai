package com.sou.model;

import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class LearningLogInput {
	
	@NotNull
	private String content;
	
	private String tags;
	
	public LearningLogInput( @NotNull String content, String tags ) {
		this.content = content;
		this.tags = tags;
	}
	
	public LearningLogInput( @NotNull String content ) {
		this( content, null );
	}
	
	public LearningLogInput() {
		this( "", null );
	}
	
	public void setContent( @NotNull String content ) {
		this.content = content;
	}
}