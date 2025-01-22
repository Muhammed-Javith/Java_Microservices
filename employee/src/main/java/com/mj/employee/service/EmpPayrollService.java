package com.mj.employee.service;

import java.util.List;
import java.util.Map;

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

	List<EmployeePayrollResponseDto> createEmployees(List<EmployeePayrollRequestDto> employeePayrollReqDtos) throws EmployeeAlreadyExistException;

	 Map<String, Object>  getAllEmployeesWithPayroll(int page, int size, String sortBy, boolean ascending);

}
