package com.skilltrack.ai.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table( name = "app_user" )
public class User {

	@Id
	@GeneratedValue
	private UUID id;

	@Column( unique = true )
	private String username;

	private String email;
}
