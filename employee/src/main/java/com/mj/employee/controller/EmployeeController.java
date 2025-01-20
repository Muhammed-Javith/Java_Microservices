package com.mj.employee.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
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
import com.mj.employee.exception.EmployeeNotFoundException;
import com.mj.employee.exception.MissingFieldException;
import com.mj.employee.payload.EmployeeDto;
import com.mj.employee.service.EmployeeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Employee APIs", description = "CRUD operations for employees")
@RequestMapping("/api/employees")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	Logger logger = LoggerFactory.getLogger(EmployeeController.class);

	@Operation(summary = "Create a New Employee")
	@PostMapping("/create")
	public ResponseEntity<?> createEmployee(@RequestBody EmployeeDto employeeDto)
			throws MissingFieldException, EmployeeAlreadyExistException {
		logger.info("Received request to create employee and validating fields here");
		if (!StringUtils.hasText(employeeDto.getName()) || !StringUtils.hasText(employeeDto.getEmail())
				|| !StringUtils.hasText(employeeDto.getPassword())) {
			throw new MissingFieldException("Please enter Mandatory details to proceed");
		}
		logger.info("Create employee");
		EmployeeDto createdEmployee = employeeService.createEmployee(employeeDto);
		logger.info("return response");
		return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
	}

	@Operation(summary = "Get a Employee Details by EmployeeId")
	@GetMapping("/get/{id}")
	public ResponseEntity<?> getEmployeeById(@EmployeeIdParam @PathVariable Long id) throws EmployeeNotFoundException {
		EmployeeDto employee = employeeService.getEmployeeById(id);
		return ResponseEntity.status(HttpStatus.OK).body(employee);
	}

	@Operation(summary = "Get all Employee Details")
	@GetMapping("/getall")
	public ResponseEntity<?> getAllEmployees() {
		List<EmployeeDto> employees = employeeService.getAllEmployee();
		return ResponseEntity.status(HttpStatus.OK).body(employees);
	}

	@Operation(summary = "Update a Employee Details by EmployeeId")
	@PutMapping("/update/{id}")
	public ResponseEntity<?> updateEmployee(@EmployeeIdParam @PathVariable Long id,
			@RequestBody EmployeeDto employeeDto) throws EmployeeNotFoundException, EmployeeAlreadyExistException {
		EmployeeDto updatedEmployee = employeeService.updateEmployee(employeeDto, id);
		return ResponseEntity.status(HttpStatus.OK).body(updatedEmployee);
	}

	@Operation(summary = "Delete a Employee Details by EmployeeId")
	@DeleteMapping("/del/{id}")
	public ResponseEntity<?> deleteEmployee(@EmployeeIdParam @PathVariable Long id) {
		employeeService.deleteEmployee(id);
		return ResponseEntity.status(HttpStatus.OK)
				.body(Map.of("message", "EmpId " + id + " is deleted successfully."));
	}

}
