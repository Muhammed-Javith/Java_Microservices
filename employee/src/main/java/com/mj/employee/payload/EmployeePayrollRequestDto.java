package com.mj.employee.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeePayrollRequestDto {
	private String name;
	private String email;
	private String address;
	private PayrollRequestDto payrollInfo;
}
