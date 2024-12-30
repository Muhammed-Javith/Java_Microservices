package com.mj.employer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Employer {
	
	@Id
	private Long employeeId;

	@Column(nullable = false)
	private String department;

	@Column(nullable = false)
	private String designation;

	@Column(nullable = false)
	private String technology;

}
