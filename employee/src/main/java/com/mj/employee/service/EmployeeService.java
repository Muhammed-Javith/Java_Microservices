package com.mj.employee.service;

import org.springframework.data.domain.Page;

import com.mj.employee.exception.EmployeeAlreadyExistException;
import com.mj.employee.payload.EmployeeDto;

public interface EmployeeService {
	EmployeeDto createEmployee(EmployeeDto employeeDto) throws EmployeeAlreadyExistException;

	EmployeeDto getEmployeeById(Long id);

	EmployeeDto getEmployeeByEmail(String email);

	Page<EmployeeDto> getAllEmployees(int page, int size, String sortBy, boolean ascending);

	EmployeeDto updateEmployee(EmployeeDto employeeDto, Long id) throws EmployeeAlreadyExistException;

	void deleteEmployee(Long id);

}
