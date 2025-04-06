package com.sou.model;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
@Builder
public class LearningLogInput {
	
	@NotNull
	private String content;
	
	private LocalDate date;
	
	@Size( max = 255 )
	private String tags;
	
	public LearningLogInput( @NotNull String content, LocalDate date, String tags ) {
		this.content = content;
		this.date = date;
		this.tags = tags;
	}
	
	public LearningLogInput( @NotNull String content, String tags ) {
		this( content, LocalDate.now(), tags );
	}
	
	public LearningLogInput( @NotNull String content ) {
		this( content, LocalDate.now(), null );
	}
	
	public LearningLogInput() {
		this( "", LocalDate.now(), null );
	}
	
	public void setContent( @NotNull String content ) {
		this.content = content;
	}
}