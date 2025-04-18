package com.skilltrack.ai.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table( name = "summary_usage", uniqueConstraints = @UniqueConstraint( columnNames = { "user_id", "usage_date" } ) )
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SummaryUsage {

	@Id
	@GeneratedValue
	private UUID id;

	@ManyToOne( optional = false )
	@JoinColumn( name = "user_id" )
	private User user;

	@Column( name = "username", nullable = false )
	private String username;

	@Column( name = "usage_date", nullable = false )
	private LocalDate usageDate;

	@Column( nullable = false )
	private int count = 0;
}
