package com.mj.apigateway.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

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

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;

	private String role;

	private String designation;

	private String department;

	private Long phoneNumber;

	private String address;
	private PayrollDto payrollInfo;
}
