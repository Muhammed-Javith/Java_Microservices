package com.mj.employee.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mj.employee.annotation.EmployeeIdParam;
import com.mj.employee.exception.EmployeeAlreadyExistException;
import com.mj.employee.exception.EmployeeNotFoundException;
import com.mj.employee.exception.InvalidFileException;
import com.mj.employee.exception.MissingFieldException;
import com.mj.employee.payload.EmployeeDto;
import com.mj.employee.payload.EmployeePayrollRequestDto;
import com.mj.employee.payload.EmployeePayrollResponseDto;
import com.mj.employee.payload.PayrollResponseDto;
import com.mj.employee.service.EmpPayrollService;
import com.mj.employee.service.EmployeeService;
import com.mj.employee.util.CSVProcessorUtility;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Microservice APIs", description = "Employee Payroll Microservive API Communication")
@RequestMapping("/api/employee/salary")
public class EmployeePayrollController {

	@Autowired
	private EmpPayrollService empPayrollService;

	@Autowired
	private EmployeeService employeeService;

	private final ObjectMapper objectMapper = new ObjectMapper();

	Logger logger = LoggerFactory.getLogger(EmployeePayrollController.class);

	@Operation(summary = "Create Employee with Payroll")
	@CircuitBreaker(name = "payrollServiceBreaker", fallbackMethod = "createEmployeeWithPayrollFallback")
	@PostMapping("/create")
	public ResponseEntity<?> createEmployeeWithPayroll(@RequestBody EmployeePayrollRequestDto employeePayrollReqDto)
			throws EmployeeAlreadyExistException, MissingFieldException {
		if (!StringUtils.hasText(employeePayrollReqDto.getName())
				|| !StringUtils.hasText(employeePayrollReqDto.getEmail())
				|| !StringUtils.hasText(employeePayrollReqDto.getPassword())) {
			throw new MissingFieldException("Please enter all Employee details to proceed");
		}
		try {
			EmployeePayrollResponseDto employeeWithPayroll = empPayrollService
					.createEmployeeWithPayroll(employeePayrollReqDto);
			logger.info("heelleloeoe");
			return ResponseEntity.status(HttpStatus.CREATED).body(employeeWithPayroll);
		} catch (EmployeeAlreadyExistException ex) {
			logger.info("jello" + ex.getMessage());
			String message = ex.getMessage();
			if (message != null && message.startsWith("{") && message.endsWith("}")) {
				try {
					// Try to parse the exception message as JSON
					JsonNode rootNode = objectMapper.readTree(message);
					JsonNode errorMessageNode = rootNode.get("error");

					// Return the JSON error message if valid
					return ResponseEntity.status(HttpStatus.CONFLICT)
							.body(Map.of("message", errorMessageNode.asText()));
				} catch (Exception e) {
					// If parsing fails, log the error and return the original message
					logger.info("Parsing error: " + e.getMessage());
					return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
				}
			} else {
				// If it's not JSON, return the message directly
				return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", message));
			}
		}

	}

	@Operation(summary = "Get a Employee with Payroll Details by EmployeeId")
	@CircuitBreaker(name = "payrollServiceBreaker", fallbackMethod = "getPayrollServiceFallback")
	@GetMapping("/get/{id}")
	public ResponseEntity<?> getEmployeeWithPayrollById(@EmployeeIdParam @PathVariable Long id) {
		EmployeePayrollResponseDto employeeWithPayroll = empPayrollService.getEmployeeWithPayroll(id);
		return ResponseEntity.status(HttpStatus.OK).body(employeeWithPayroll);
	}

	@Operation(summary = "Update a Employee with Payroll Details by EmployeeId")
	@PutMapping("/update/{id}")
	public ResponseEntity<?> updateEmployee(@EmployeeIdParam @PathVariable Long id,
			@RequestBody EmployeePayrollRequestDto employeePayrollReqDto)
			throws EmployeeAlreadyExistException, MissingFieldException {
		EmployeePayrollResponseDto updatedEmployee = empPayrollService.updateEmployeeWithPayroll(id,
				employeePayrollReqDto);
		return ResponseEntity.status(HttpStatus.OK).body(updatedEmployee);
	}

	@Operation(summary = "Delete a Employee with Payroll Details by EmployeeId")
	@CircuitBreaker(name = "payrollServiceBreaker", fallbackMethod = "delPayrollServiceFallback")
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteEmployeeWithPayroll(@EmployeeIdParam @PathVariable Long id) {
		try {
			empPayrollService.deleteEmployeeWithPayroll(id);
			return ResponseEntity.status(HttpStatus.OK)
					.body(Map.of("message", "EmpId " + id + " is deleted successfully."));
		} catch (EmployeeNotFoundException ex) {
			logger.info("hello");
			logger.info("Employee not found: {}", ex.getMessage());
			try {
				JsonNode rootNode = objectMapper.readTree(ex.getMessage());
				JsonNode errorMessageNode = rootNode.get("error");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", errorMessageNode.asText()));
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", e.getMessage()));
			}
		}
	}

	@Operation(summary = "Create Employee with Payroll by CSV")
	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createEmployeeWithCsv(@RequestParam("file") MultipartFile file)
			throws EmployeeAlreadyExistException, MissingFieldException {
		try {
			if (!"text/csv".equals(file.getContentType()) && !file.getOriginalFilename().endsWith(".csv")) {
				throw new InvalidFileException("Invalid file format. Please upload a valid CSV file.");
			}
			List<EmployeePayrollRequestDto> employeePayrollReqDtos = CSVProcessorUtility
					.processCsvAndSendToPayroll(file);
			logger.info("Processing CSV file and creating employees: {}", employeePayrollReqDtos);
			List<EmployeePayrollResponseDto> responseDtos = empPayrollService.createEmployees(employeePayrollReqDtos);
			// return ResponseEntity.status(HttpStatus.CREATED).body(responseDtos);
			return ResponseEntity.status(HttpStatus.CREATED).body(
					Map.of("message", "Uploaded and processed " + responseDtos.size() + " employees successfully."));
		} catch (InvalidFileException e) {
			return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(Map.of("message", e.getMessage()));
		} catch (MissingFieldException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Error processing the file."));
		}
	}

	public ResponseEntity<?> getPayrollServiceFallback(Long id, Exception ex) {
		logger.info("Fallback is executed because service is down : ", ex.getMessage());
		EmployeeDto employeeDto = employeeService.getEmployeeById(id);
		PayrollResponseDto fallbackPayroll = PayrollResponseDto.builder().hra(-1.0).basic(-1.0).ctc(-1.0)
				.deductions(-1.0).netSalary(-1.0).build();
		EmployeePayrollResponseDto fallbackResponse = EmployeePayrollResponseDto.builder().id(employeeDto.getId())
				.name(employeeDto.getName()).email(employeeDto.getEmail()).password(employeeDto.getPassword())
				.role(employeeDto.getRole()).designation(employeeDto.getDesignation())
				.department(employeeDto.getDepartment()).phoneNumber(employeeDto.getPhoneNumber())
				.address(employeeDto.getAddress()).payrollInfo(fallbackPayroll).build();
		Map<String, Object> response = new LinkedHashMap<>();
		response.put("message", "Payroll service is currently unavailable. Returning fallback data for requested data");
		response.put("fallbackData", fallbackResponse);
		logger.info("Returning fallback data for ID : {}", id);
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);

	}

	public ResponseEntity<?> createEmployeeWithPayrollFallback(EmployeePayrollRequestDto employeePayrollReqDto,
			Exception ex) {
		logger.info("Fallback is executed because service is down: {}", ex.getMessage());
		EmployeeDto employeeDto = EmployeeDto.builder().name(employeePayrollReqDto.getName())
				.email(employeePayrollReqDto.getEmail()).password(employeePayrollReqDto.getPassword())
				.role(employeePayrollReqDto.getRole()).designation(employeePayrollReqDto.getDesignation())
				.department(employeePayrollReqDto.getDepartment()).phoneNumber(employeePayrollReqDto.getPhoneNumber())
				.address(employeePayrollReqDto.getAddress()).build();
		try {
			EmployeeDto createdEmployee = employeeService.getEmployeeByEmail(employeeDto.getEmail());
			if (createdEmployee != null) {
				Long employeeId = createdEmployee.getId();
				logger.info("Deleting partially created employee with ID: {}", employeeId);
				employeeService.deleteEmployee(employeeId); // Cleanup the partial entry
			}
		} catch (EmployeeNotFoundException e) {
			logger.warn("No partial entry found for employee with email: {}", employeePayrollReqDto.getEmail());
		}
		// Create a fallback payroll response
		PayrollResponseDto fallbackPayroll = PayrollResponseDto.builder().hra(-1.0).basic(-1.0).netSalary(-1.0).build();
		// Create a fallback employee response
		EmployeePayrollResponseDto fallbackResponse = EmployeePayrollResponseDto.builder().id(-1L).name("dummy")
				.email("dummy@gmail.com").password("********").role("dummmy Role").designation("Dummy")
				.department("dummy").phoneNumber(-1L).address("dummyx").payrollInfo(fallbackPayroll).build();
		Map<String, Object> response = new LinkedHashMap<>();
		response.put("message",
				"Payroll service is currently unavailable. Returning fallback data for the created employee.");
		response.put("fallbackData", fallbackResponse);
		logger.info("Returning fallback data for employee: {}", employeePayrollReqDto.getName());
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
	}

	public ResponseEntity<?> delPayrollServiceFallback(Long id, Exception ex) {
		logger.info("Fallback is executed because service is down: {}", ex.getMessage());
		// Create a fallback payroll response
		PayrollResponseDto fallbackPayroll = PayrollResponseDto.builder().hra(-1.0).basic(-1.0).netSalary(-1.0).build();
		// Create a fallback employee response
		EmployeePayrollResponseDto fallbackResponse = EmployeePayrollResponseDto.builder().id(-1L).name("dummy")
				.email("dummy@gmail.com").password("********").role("dummmy Role").designation("Dummy")
				.department("dummy").phoneNumber(-1L).address("dummyx").payrollInfo(fallbackPayroll).build();
		Map<String, Object> response = new LinkedHashMap<>();
		response.put("message", "Payroll service is currently unavailable. Returning fallback data for requested data");
		response.put("fallbackData", fallbackResponse);
		logger.info("Returning fallback data for ID : {}", id);
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
	}
}
