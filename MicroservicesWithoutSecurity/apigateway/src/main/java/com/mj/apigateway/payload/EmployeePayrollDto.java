package com.mj.apigateway.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeePayrollDto {
	private Long id;
	private String name;
	private String email;
	private String address;
	private PayrollDto payrollInfo;
}
