package com.mj.employee.Payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeePayrollDto {
	private Long id;
	private String name;
	private String email;
	private String address;
	private PayrollDto payrollInfo;

}
