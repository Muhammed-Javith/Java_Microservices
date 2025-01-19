package com.mj.apigateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mj.apigateway.service.FallbackService;

@RestController
public class GatewayController {

	private final FallbackService fallbackService;

	public GatewayController(FallbackService fallbackService) {
		this.fallbackService = fallbackService;
	}

	@PostMapping("/employeeServiceFallBack")
	public ResponseEntity<?> employeeServicePostFallBack() {
		return fallbackService.EmployeeFallbackResponse();
	}

	@GetMapping("/employeeServiceFallBack")
	public ResponseEntity<?> employeeServiceFallBack() {
		return fallbackService.EmployeeFallbackResponse();
	}

	@PutMapping("/employeeServiceFallBack")
	public ResponseEntity<?> employeeServicePutFallBack() {
		return fallbackService.EmployeeFallbackResponse();
	}

	@DeleteMapping("/employeeServiceFallBack")
	public ResponseEntity<?> employeeServiceDeleteFallBack() {
		return fallbackService.EmployeeFallbackResponse();
	}

	@GetMapping("/payrollServiceFallBack")
	public ResponseEntity<?> payrollServiceFallBack() {
		return fallbackService.PayrollFallbackResponse();
	}

	@PostMapping("/payrollServiceFallBack")
	public ResponseEntity<?> payrollServicePostFallBack() {
		return fallbackService.PayrollFallbackResponse();
	}

	@PutMapping("/payrollServiceFallBack")
	public ResponseEntity<?> PayrollServicePutFallBack() {
		return fallbackService.PayrollFallbackResponse();
	}

	@DeleteMapping("/payrollServiceFallBack")
	public ResponseEntity<?> payrollServiceDeleteFallBack() {
		return fallbackService.PayrollFallbackResponse();
	}

	@GetMapping("/employee-salaryServiceFallBack")
	public ResponseEntity<?> employeeSalaryServiceFallBack() {
		return fallbackService.EmployeePayrollFallbackResponse();
	}

	@PostMapping("/employee-salaryServiceFallBack")
	public ResponseEntity<?> employeeSalaryServicePostFallBack() {
		return fallbackService.EmployeePayrollFallbackResponse();
	}

	@DeleteMapping("/employee-salaryServiceFallBack")
	public ResponseEntity<?> employeeSalaryServiceDeleteFallBack() {
		return fallbackService.EmployeePayrollFallbackResponse();
	}

}
