package com.mj.employee.Payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayrollDto {

	private Long employeeId;
	private double hra;
	private double basic;
	private double gross;

}
