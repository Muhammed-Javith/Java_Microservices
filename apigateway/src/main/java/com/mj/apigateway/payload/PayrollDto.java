package com.mj.apigateway.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollDto {
	//private Long employeeId;
	private double hra;
	private double basic;
	private double totalSalary;
}
