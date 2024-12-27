package com.mj.employee.controller;

import java.util.List;

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

import com.mj.employee.Payload.EmployeeDto;
import com.mj.employee.Payload.EmployeeResponse;
import com.mj.employee.exception.EmployeeAlreadyExistException;
import com.mj.employee.exception.EmployeeNotFoundException;
import com.mj.employee.exception.MissingFieldException;
import com.mj.employee.service.EmployeeService;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	// Create a new employee
	@PostMapping("/create")
	public ResponseEntity<EmployeeResponse<EmployeeDto>> createEmployee(@RequestBody EmployeeDto employeeDto)
			throws MissingFieldException, EmployeeAlreadyExistException {
		if (!StringUtils.hasText(employeeDto.getName()) || !StringUtils.hasText(employeeDto.getEmail())) {
			throw new MissingFieldException("Please enter all details to proceed");
		}
		EmployeeDto createdEmployee = employeeService.createEmployee(employeeDto);
		EmployeeResponse<EmployeeDto> response = new EmployeeResponse<>("Employee created successfully",
				HttpStatus.CREATED.value(), createdEmployee);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// Get employee by ID
	@GetMapping("/get/{id}")
	public ResponseEntity<EmployeeResponse<?>> getEmployeeById(@PathVariable Long id) throws EmployeeNotFoundException {
		EmployeeDto employee = employeeService.getEmployeeById(id);
		EmployeeResponse<EmployeeDto> response = new EmployeeResponse<>("Employee details retrieved successfully",
				HttpStatus.OK.value(), employee);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// Get all employees
	@GetMapping("/getall")
	public ResponseEntity<EmployeeResponse<List<EmployeeDto>>> getAllEmployees() {
		List<EmployeeDto> employees = employeeService.getAllEmployee();
		EmployeeResponse<List<EmployeeDto>> response = new EmployeeResponse<>("Employees retrieved successfully",
				HttpStatus.OK.value(), employees);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// Update an employee by ID
	@PutMapping("/update/{id}")
	public ResponseEntity<EmployeeResponse<EmployeeDto>> updateEmployee(@PathVariable Long id,
			@RequestBody EmployeeDto employeeDto) throws EmployeeNotFoundException, EmployeeAlreadyExistException {
		EmployeeDto updatedEmployee = employeeService.updateEmployee(employeeDto, id);
		EmployeeResponse<EmployeeDto> response = new EmployeeResponse<>("Employee details for the id " + id + " updated successfully",
				HttpStatus.OK.value(), updatedEmployee);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// Delete an employee by ID
	@DeleteMapping("/del/{id}")
	public ResponseEntity<EmployeeResponse<?>> deleteEmployee(@PathVariable Long id) {
		employeeService.deleteEmployee(id);
		EmployeeResponse<?> response = new EmployeeResponse<>("Employee deleted successfully",
				HttpStatus.NO_CONTENT.value());
		return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
	}
}
