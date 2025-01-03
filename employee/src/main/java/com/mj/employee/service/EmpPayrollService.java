package com.mj.employee.service;

import com.mj.employee.exception.EmployeeAlreadyExistException;
import com.mj.employee.exception.MissingFieldException;
import com.mj.employee.payload.EmployeePayrollRequestDto;
import com.mj.employee.payload.EmployeePayrollResponseDto;

public interface EmpPayrollService {

	EmployeePayrollResponseDto createEmployeeWithPayroll(EmployeePayrollRequestDto employeePayrollReqDto)
			throws EmployeeAlreadyExistException, MissingFieldException;

	EmployeePayrollResponseDto getEmployeeWithPayroll(Long id);

	EmployeePayrollResponseDto updateEmployeeWithPayroll(Long id, EmployeePayrollRequestDto employeePayrollReqDto)
			throws EmployeeAlreadyExistException, MissingFieldException;

	void deleteEmployeeWithPayroll(Long id);

}
