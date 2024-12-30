package com.mj.employee.service;

import java.util.List;

import com.mj.employee.exception.EmployeeAlreadyExistException;
import com.mj.employee.exception.MissingFieldException;
import com.mj.employee.payload.EmployeeDto;
import com.mj.employee.payload.EmployeePayrollRequestDto;
import com.mj.employee.payload.EmployeePayrollResponseDto;

public interface EmployeeService {
	EmployeeDto createEmployee(EmployeeDto employeeDto) throws EmployeeAlreadyExistException;

	EmployeeDto getEmployeeById(Long id);

	EmployeeDto getEmployeeByEmail(String email);

	List<EmployeeDto> getAllEmployee();

	EmployeeDto updateEmployee(EmployeeDto employeeDto, Long id) throws EmployeeAlreadyExistException;

	void deleteEmployee(Long id);

	EmployeePayrollResponseDto createEmployeeWithPayroll(EmployeePayrollRequestDto employeePayrollReqDto)
			throws EmployeeAlreadyExistException, MissingFieldException;
	
	EmployeePayrollResponseDto getEmployeeWithPayroll(Long id);
	
	EmployeePayrollResponseDto updateEmployeeWithPayroll(Long id, EmployeePayrollRequestDto employeePayrollReqDto) throws EmployeeAlreadyExistException;

	void deleteEmployeeWithPayroll(Long id);

}
