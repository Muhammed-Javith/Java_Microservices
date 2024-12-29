package com.mj.payroll.controller;

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

import com.mj.payroll.annotation.EmployeeIdParam;
import com.mj.payroll.exception.MissingFieldException;
import com.mj.payroll.exception.PayrollAlreadyExistException;
import com.mj.payroll.exception.PayrollNotFoundException;
import com.mj.payroll.payload.PayrollDto;
import com.mj.payroll.service.PayrollService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/payroll")
public class PayrollController {
	@Autowired
	private PayrollService payrollService;

	Logger logger = LoggerFactory.getLogger(PayrollController.class);

	@Operation(summary = "Create a New Payroll")
	@Tag(name = "Payroll APIs", description = "CRUD operations for payrolls")
	@PostMapping("/create")
	public ResponseEntity<?> createPayroll(@RequestBody PayrollDto payrollDto)
			throws MissingFieldException, PayrollAlreadyExistException {
		logger.info("Received request to create payroll and validating fields here");
		if (payrollDto.getEmployeeId() == null || payrollDto.getHra() <= 0 || payrollDto.getBasic() <= 0) {
			throw new MissingFieldException("Please enter all details to proceed");
		}
		logger.info("Create payroll");
		PayrollDto createdPayroll = payrollService.createPayroll(payrollDto);
		logger.info("Received response from Payroll Service: {}", createdPayroll);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdPayroll);
	}

	@Operation(summary = "Get Payroll Details by PayrollId")
	@Tag(name = "Payroll APIs", description = "CRUD operations for payrolls")
	@GetMapping("/get/{id}")
	public ResponseEntity<?> getPayrollById(@EmployeeIdParam @PathVariable Long id) throws PayrollNotFoundException {
		PayrollDto payroll = payrollService.getPayrollByEmployeeId(id);
		return ResponseEntity.status(HttpStatus.OK).body(payroll);
	}

	@Operation(summary = "Update Payroll Details by PayrollId")
	@Tag(name = "Payroll APIs", description = "CRUD operations for payrolls")
	@PutMapping("/update/{id}")
	public ResponseEntity<?> updatePayroll(@EmployeeIdParam @PathVariable Long id, @RequestBody PayrollDto payrollDto)
			throws PayrollNotFoundException, PayrollAlreadyExistException {
		PayrollDto updatedPayroll = payrollService.updatePayroll(payrollDto, id);
		return ResponseEntity.status(HttpStatus.OK).body(updatedPayroll);
	}

	@Operation(summary = "Delete Payroll by PayrollId")
	@Tag(name = "Payroll APIs", description = "CRUD operations for payrolls")
	@DeleteMapping("/del/{id}")
	public ResponseEntity<?> deletePayroll(@EmployeeIdParam @PathVariable Long id) {
		payrollService.deletePayroll(id);
		return ResponseEntity.status(HttpStatus.OK).body("Payroll with ID " + id + " is deleted successfully.");
	}

}
