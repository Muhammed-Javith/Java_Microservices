package com.mj.employee.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mj.employee.exception.EmployeeAlreadyExistException;
import com.mj.employee.exception.MissingFieldException;
import com.mj.employee.payload.EmployeePayrollRequestDto;
import com.mj.employee.payload.EmployeePayrollResponseDto;
import com.mj.employee.service.EmployeeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Microservice APIs", description = "Employee Payroll Microservive API Communication")
@RequestMapping("/api/employees")
public class EmployeePayrollController {

	@Autowired
	private EmployeeService employeeService;

	Logger logger = LoggerFactory.getLogger(EmployeePayrollController.class);

	@Operation(summary = "Create Employee with Payroll")
	@PostMapping("/createWithPayroll")
	public ResponseEntity<?> createEmployeeWithPayroll(@RequestBody EmployeePayrollRequestDto employeePayrollDto)
			throws EmployeeAlreadyExistException, MissingFieldException {
		 EmployeePayrollResponseDto employeeWithPayroll = employeeService.createEmployeeWithPayroll(employeePayrollDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(employeeWithPayroll);
	}

//	@Operation(summary = "Get a Payroll Details by EmployeeId")
//	@GetMapping("/getWithPayroll/{id}")
//	public ResponseEntity<?> getEmployeeWithPayrollById(@EmployeeIdParam @PathVariable Long id) {
//		EmployeePayrollDto employeeWithPayroll = employeeService.getEmployeeWithPayroll(id);
//		return ResponseEntity.status(HttpStatus.OK).body(employeeWithPayroll);
//	}

}
