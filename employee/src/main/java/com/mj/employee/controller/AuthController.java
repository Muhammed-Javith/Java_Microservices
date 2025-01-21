package com.mj.employee.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mj.employee.payload.LoginDto;
import com.mj.employee.service.EmployeeAuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Security APIs", description = "Authentication and Authorization for employees")
@RequestMapping("/api/auth")
@RestController
public class AuthController {

	@Autowired
	private EmployeeAuthService employeeAuthService;

	Logger logger = LoggerFactory.getLogger(EmployeeController.class);

	@Operation(summary = "Verify the Login Employee")
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
		logger.info("Received request to verify the employee");
		Map<String, Object> response = new LinkedHashMap<>();
		String empToken = employeeAuthService.verifyTheEmployee(loginDto);
		if ("Token generation failed!".equals(empToken)) {
			response.put("message", "Token generation failed! Please check your credentials and try again.");
		} else {
			response.put("message", "Employee logged in successfully");
			response.put("Access Token", empToken);
		}
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

}
