package com.mj.employer.controller;

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

import com.mj.employer.annotation.EmployeeIdParam;
import com.mj.employer.exception.EmployerAlreadyExistException;
import com.mj.employer.exception.EmployerNotFoundException;
import com.mj.employer.exception.MissingFieldException;
import com.mj.employer.payload.EmployerDto;
import com.mj.employer.service.EmployerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Employer APIs", description = "CRUD operations for employers")
@RestController
@RequestMapping("/api/employer")
public class EmployerController {

	@Autowired
	private EmployerService employerService;

	Logger logger = LoggerFactory.getLogger(EmployerController.class);

	@Operation(summary = "Create a New Employer")
	@PostMapping("/create")
	public ResponseEntity<?> createEmployer(@RequestBody EmployerDto employerDto)
			throws MissingFieldException, EmployerAlreadyExistException {
		logger.info("Received request to create employer and validating fields here");
		if (employerDto.getEmployeeId() == null || employerDto.getDesignation() == null
				|| employerDto.getDepartment() == null) {
			throw new MissingFieldException("Please enter all details to proceed");
		}
		logger.info("Create employer");
		EmployerDto createdEmployer = employerService.createEmployer(employerDto);
		logger.info("Received response from Employer Service: {}", createdEmployer);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployer);
	}

	@Operation(summary = "Get Employer Details by EmployerId")
	@GetMapping("/get/{id}")
	public ResponseEntity<?> getEmployerById(@EmployeeIdParam @PathVariable Long id) throws EmployerNotFoundException {
		EmployerDto employer = employerService.getEmployerByEmployeeId(id);
		return ResponseEntity.status(HttpStatus.OK).body(employer);
	}

	@Operation(summary = "Update Employer Details by EmployerId")
	@PutMapping("/update/{id}")
	public ResponseEntity<?> updateEmployer(@EmployeeIdParam @PathVariable Long id,
			@RequestBody EmployerDto employerDto) throws EmployerNotFoundException, EmployerAlreadyExistException {
		EmployerDto updatedEmployer = employerService.updateEmployer(employerDto, id);
		return ResponseEntity.status(HttpStatus.OK).body(updatedEmployer);
	}

	@Operation(summary = "Delete Employer by EmployerId")
	@DeleteMapping("/del/{id}")
	public ResponseEntity<?> deleteEmployer(@EmployeeIdParam @PathVariable Long id) {
		employerService.deleteEmployer(id);
		return ResponseEntity.status(HttpStatus.OK).body("Employer with ID " + id + " is deleted successfully.");
	}

}
