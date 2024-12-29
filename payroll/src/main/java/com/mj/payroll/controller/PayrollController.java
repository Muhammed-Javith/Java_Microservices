package com.mj.payroll.controller;

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
import com.mj.payroll.payload.PayrollResponse;
import com.mj.payroll.service.PayrollService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/payroll")
@Tag(name = "Payroll APIs", description = "CRUD operations for Payroll")
public class PayrollController {

	@Autowired
	private PayrollService payrollService;

	@Operation(summary = "Create a New Employee")
	@PostMapping("/create")
	public ResponseEntity<PayrollResponse<PayrollDto>> createPayroll(@RequestBody PayrollDto payrollDto)
			throws MissingFieldException, PayrollAlreadyExistException {
		if (payrollDto.getEmployeeId() == null || payrollDto.getHra() <= 0 || payrollDto.getBasic() <= 0) {
			throw new MissingFieldException("Please enter all details to proceed");
		}
		PayrollDto createdPayroll = payrollService.createPayroll(payrollDto);
		PayrollResponse<PayrollDto> response = new PayrollResponse<>("Payroll created successfully",
				HttpStatus.CREATED.value(), createdPayroll);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@Operation(summary = "Get Payroll Details by EmployeeId")
	@GetMapping("/get/{id}")
	public ResponseEntity<PayrollResponse<?>> getPayrollById(@EmployeeIdParam @PathVariable Long id)
			throws PayrollNotFoundException {
		PayrollDto payroll = payrollService.getPayrollByEmployeeId(id);
		PayrollResponse<PayrollDto> response = new PayrollResponse<>("Payroll details retrieved successfully",
				HttpStatus.OK.value(), payroll);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Operation(summary = "Update Payroll Details by EmployeeId")
	@PutMapping("/update/{id}")
	public ResponseEntity<PayrollResponse<PayrollDto>> updatePayroll(@EmployeeIdParam @PathVariable Long id,
			@RequestBody PayrollDto payrollDto) throws PayrollNotFoundException, PayrollAlreadyExistException {
		PayrollDto updatedPayroll = payrollService.updatePayroll(payrollDto, id);
		PayrollResponse<PayrollDto> response = new PayrollResponse<>(
				"Payroll details for the id " + id + " updated successfully", HttpStatus.OK.value(), updatedPayroll);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Operation(summary = "Delete Payroll Details by EmployeeId")
	@DeleteMapping("/del/{id}")
	public ResponseEntity<PayrollResponse<?>> deletePayroll(@EmployeeIdParam @PathVariable Long id) {
		payrollService.deletePayroll(id);
		PayrollResponse<?> response = new PayrollResponse<>(
				"Payroll details for the id " + id + " deleted successfully", HttpStatus.OK.value());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
