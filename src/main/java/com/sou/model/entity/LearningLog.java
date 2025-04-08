package com.sou.model.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table( name = "learning_log" )
public class LearningLog {
	
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;
	
	@ManyToOne
	@JoinColumn( name = "user_id" )
	@JsonBackReference
	private User user;
	
	@Column( columnDefinition = "TEXT", nullable = false )
	private String content;
	
	@Column( columnDefinition = "TEXT" )
	private String tags;
	
	private LocalDate date;
	
}
