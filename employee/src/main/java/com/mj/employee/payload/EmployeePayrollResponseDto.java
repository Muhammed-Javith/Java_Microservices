package com.mj.employee.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
	@JsonIgnore
	private String password;
	private String role;
	private String designation;
	private String department;
	private Long phoneNumber;
	private String address;
	private PayrollResponseDto payrollInfo;
}
