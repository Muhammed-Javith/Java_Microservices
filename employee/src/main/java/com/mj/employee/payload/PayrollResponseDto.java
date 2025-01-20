package com.mj.employee.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollResponseDto {
	// private Long employeeId;
	private double hra;
	private double basic;
	private double ctc;
	private double deductions;
	private double netSalary;
}
