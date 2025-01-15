package com.mj.employee.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayrollRequestDto {
	private Long employeeId;
	private double hra;
	private double basic;

}
