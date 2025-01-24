package com.mj.employee.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.mj.employee.controller.EmployeePayrollController;
import com.mj.employee.exception.MissingFieldException;
import com.mj.employee.payload.EmployeePayrollRequestDto;
import com.mj.employee.payload.PayrollRequestDto;

public class CSVProcessorUtility {

	static Logger logger = LoggerFactory.getLogger(EmployeePayrollController.class);

	public static List<EmployeePayrollRequestDto> processCsvAndSendToPayroll(MultipartFile file) {

		// Variable to hold the CSV records
		List<CSVRecord> records;
		CSVFormat csvFormat = CSVFormat.DEFAULT.builder().setSkipHeaderRecord(true).setHeader("name", "email",
				"password", "role", "designation", "department", "phoneNumber", "address", "hra", "basic", "deductions")
				.build();
		try (CSVParser csvParser = new CSVParser(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8),
				csvFormat)) {
			// Store the records in a variable for later use
			records = csvParser.getRecords();

		} catch (IOException e) {
			// Handle the error if CSV parsing fails
			throw new RuntimeException("Error reading CSV file", e);
		}

		// List to hold the final mapped DTOs
		List<EmployeePayrollRequestDto> resultList = new ArrayList<>();

		// Process each record and map it to EmployeePayrollRequestDto
		for (CSVRecord record : records) {
			try {
				validateRequiredFields(record);
				EmployeePayrollRequestDto dto = mapToEmployeePayrollRequestDto(record);
				resultList.add(dto);
			} catch (MissingFieldException e) {
				// Handle validation failure
				throw new MissingFieldException("Missing required fields in CSV record");
			} catch (Exception e) {
				// Catch any other unexpected errors for each record
				System.out.println("Error processing record: " + e.getMessage());
			}
		}
		// Return the final list of DTOs
		return resultList;
	}

	private static void validateRequiredFields(CSVRecord record) throws MissingFieldException {
		if (isEmpty(record.get("name")) || isEmpty(record.get("email")) || isEmpty(record.get("password"))
				|| isEmpty(record.get("hra")) || isEmpty(record.get("basic"))) {
			throw new MissingFieldException("Missing required fields in CSV record");
		}

	}

	private static boolean isEmpty(String field) {
		return field == null || field.trim().isEmpty();
	}

	private static EmployeePayrollRequestDto mapToEmployeePayrollRequestDto(CSVRecord record) {

		// Create instances of the DTOs
		EmployeePayrollRequestDto employeePayrollReqDto = new EmployeePayrollRequestDto();
		PayrollRequestDto payrollDto = new PayrollRequestDto();

		employeePayrollReqDto.setName(record.get("name"));
		employeePayrollReqDto.setEmail(record.get("email"));
		employeePayrollReqDto.setPassword(record.get("password"));
		employeePayrollReqDto.setRole(record.get("role"));
		employeePayrollReqDto.setDesignation(record.get("designation"));
		employeePayrollReqDto.setDepartment(record.get("department"));
		employeePayrollReqDto.setPhoneNumber(Long.parseLong(record.get("phoneNumber")));
		employeePayrollReqDto.setAddress(record.get("address"));
		payrollDto.setHra(Double.parseDouble(record.get("hra")));
		payrollDto.setBasic(Double.parseDouble(record.get("basic")));
		payrollDto.setDeductions(Double.parseDouble(record.get("deductions")));

		employeePayrollReqDto.setPayrollInfo(payrollDto);
		logger.info("Received request from Employee Service: {}", employeePayrollReqDto);
		return employeePayrollReqDto;
	}
}
