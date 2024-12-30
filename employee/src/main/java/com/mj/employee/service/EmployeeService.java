package com.mj.employee.service;

import java.util.List;

import com.mj.employee.exception.EmployeeAlreadyExistException;
import com.mj.employee.payload.EmployeeDto;

public interface EmployeeService {
	EmployeeDto createEmployee(EmployeeDto employeeDto) throws EmployeeAlreadyExistException;

	EmployeeDto getEmployeeById(Long id);

	EmployeeDto getEmployeeByEmail(String email);

	List<EmployeeDto> getAllEmployee();

	EmployeeDto updateEmployee(EmployeeDto employeeDto, Long id) throws EmployeeAlreadyExistException;

	void deleteEmployee(Long id);

}
