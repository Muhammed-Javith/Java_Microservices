package com.mj.employee.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeePayrollResponseDto {
	private Long id;
	private String name;
	private String email;
	//private String password;
	private String level;
	private String designation;
	private String department;
	private Long phoneNumber;
	private String address;
	private PayrollResponseDto payrollInfo;
}
