package com.sou.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import io.quarkus.security.jpa.RolesValue;
import lombok.Data;

@Data
@Entity
@Table( name = "user_roles" )
public class Role {
	
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private Long id;
	
	@ManyToOne
	@JoinColumn( name = "user_id", nullable = false )
	private User user;
	
	@RolesValue
	@Column( nullable = false )
	private String role;
	
}
