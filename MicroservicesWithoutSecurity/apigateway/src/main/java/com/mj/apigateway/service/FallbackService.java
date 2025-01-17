package com.mj.apigateway.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mj.apigateway.payload.EmployeeDto;
import com.mj.apigateway.payload.EmployeePayrollDto;
import com.mj.apigateway.payload.PayrollDto;

@Service
public class FallbackService {

	public ResponseEntity<?> EmployeePayrollFallbackResponse() {
		PayrollDto fallbackPayroll = PayrollDto.builder().hra(-1.0).basic(-1.0).totalSalary(-1.0).build();

		EmployeePayrollDto fallbackResponse = EmployeePayrollDto.builder().id(-1L).name("dummy")
				.email("dummy@gmail.com").address("dummy-y").payrollInfo(fallbackPayroll).build();

		Map<String, Object> response = new LinkedHashMap<>();
		response.put("message", "Employee service is currently unavailable. Returning fallback data");
		response.put("fallbackData", fallbackResponse);

		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
	}

	public ResponseEntity<?> EmployeeFallbackResponse() {
		EmployeeDto fallbackResponse = EmployeeDto.builder().id(-1L).name("dummy").email("dummy@gmail.com")
				.address("dummy-x").build();
		Map<String, Object> response = new LinkedHashMap<>();
		response.put("message", "Employee service is currently unavailable. Returning fallback data");
		response.put("fallbackData", fallbackResponse);
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
	}

	public ResponseEntity<?> PayrollFallbackResponse() {
		PayrollDto fallbackResponse = PayrollDto.builder().hra(-1.0).basic(-1.0).totalSalary(-1.0).build();
		Map<String, Object> response = new LinkedHashMap<>();
		response.put("message", "Employee service is currently unavailable. Returning fallback data");
		response.put("fallbackData", fallbackResponse);
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);

	}
}
