package com.mj.payroll.entity;

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
public class Payroll {
	@Id
	private Long employeeId;

	@Column(nullable = false)
	private double hra;

	@Column(nullable = false)
	private double basic;

	@Column(nullable = false)
	private double ctc;

	@Column(nullable = false)
	private double deductions;

	@Column(nullable = false)
	private double netSalary;
}
