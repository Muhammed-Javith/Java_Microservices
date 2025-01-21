package com.mj.employee.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mj.employee.payload.LoginDto;
import com.mj.employee.service.EmployeeAuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "Employee APIs", description = "Authentication and Authorization for employees")
@RequestMapping("/api/auth")
@RestController
public class AuthController {

	@Autowired
	private EmployeeAuthService employeeAuthService;

	Logger logger = LoggerFactory.getLogger(EmployeeController.class);

	@Operation(summary = "EMS Home Page")
	@GetMapping("/")
	public ResponseEntity<?> employeeHome() {
		return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Welcome to Employee Management System"));
	}

	@Operation(summary = "CSRF TOKEN Generator")
	@GetMapping("/csrf_token")
	public CsrfToken getCsrfToken(HttpServletRequest request) {
		return (CsrfToken) request.getAttribute("_csrf");
	}

	@Operation(summary = "Verify the Login Employee")
	@PostMapping("/login")
	public String login(@RequestBody LoginDto loginDto) {
		logger.info("Received request to verify the employee");
		return employeeAuthService.verifyTheEmployee(loginDto);
	}

}
