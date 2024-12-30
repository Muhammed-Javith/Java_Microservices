package com.mj.employee.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayrollResponseDto {
	//private Long employeeId;
	private double hra;
	private double basic;
	private double totalSalary;
}