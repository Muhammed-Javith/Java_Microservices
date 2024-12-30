package com.mj.employer.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class EmployerDto {

	private Long employeeId;

	private String department;

	private String designation;

	private String technology;

}
