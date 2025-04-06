package com.sou.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;

@Entity
@Table( name = "users" )
@UserDefinition
public class User {
	
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	public Long id;
	
	@Username
	@Column( unique = true, nullable = false )
	public String username;
	
	@Password
	@Column( nullable = false )
	public String password;
	
	@Roles
	@OneToMany( mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY )
	public List<Role> roles;
	
	@OneToMany( mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY )
	@JsonManagedReference
	public List<LearningLog> learningLogs;
	
}
