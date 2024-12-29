package com.mj.employee.service;

import java.util.List;

import com.mj.employee.Payload.EmployeeDto;
import com.mj.employee.Payload.EmployeePayrollDto;
import com.mj.employee.exception.EmployeeAlreadyExistException;

public interface EmployeeService {
	EmployeeDto createEmployee(EmployeeDto employeeDto) throws EmployeeAlreadyExistException;

	EmployeeDto getEmployeeById(Long id);

	EmployeeDto getEmployeeByEmail(String email);

	List<EmployeeDto> getAllEmployee();

	EmployeeDto updateEmployee(EmployeeDto employeeDto, Long id) throws EmployeeAlreadyExistException;

	void deleteEmployee(Long id);

	EmployeePayrollDto getEmployeeWithPayroll(Long id);

}
