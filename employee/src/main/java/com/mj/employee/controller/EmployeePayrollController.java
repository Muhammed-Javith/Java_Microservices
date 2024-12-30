package com.mj.employee.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mj.employee.annotation.EmployeeIdParam;
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

	@Operation(summary = "Get a Employee with Payroll Details by EmployeeId")
	@GetMapping("/getWithPayroll/{id}")
	public ResponseEntity<?> getEmployeeWithPayrollById(@EmployeeIdParam @PathVariable Long id) {
		EmployeePayrollResponseDto employeeWithPayroll = employeeService.getEmployeeWithPayroll(id);
		return ResponseEntity.status(HttpStatus.OK).body(employeeWithPayroll);
	}

	@Operation(summary = "Update a Employee with Payroll Details by EmployeeId")
	@PutMapping("/updateWithPayroll/{id}")
	public ResponseEntity<?> updateEmployee(@EmployeeIdParam @PathVariable Long id,
			@RequestBody EmployeePayrollRequestDto employeePayrollReqDto) throws EmployeeAlreadyExistException {
		EmployeePayrollResponseDto updatedEmployee = employeeService.updateEmployeeWithPayroll(id,
				employeePayrollReqDto);
		return ResponseEntity.status(HttpStatus.OK).body(updatedEmployee);
	}

	@Operation(summary = "Delete a Employee with Payroll Details by EmployeeId")
	@DeleteMapping("/deleteWithPayroll/{id}")
	public ResponseEntity<?> deleteEmployeeWithPayroll(@EmployeeIdParam @PathVariable Long id) {
		employeeService.deleteEmployeeWithPayroll(id);
		return ResponseEntity.status(HttpStatus.OK).body("EmpId " + id + " is deleted successfully.");
	}
}