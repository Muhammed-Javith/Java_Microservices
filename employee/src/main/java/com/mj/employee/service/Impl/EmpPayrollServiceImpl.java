package com.mj.employee.service.Impl;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.mj.employee.config.PayrollClient;
import com.mj.employee.controller.EmployeePayrollController;
import com.mj.employee.enity.Employee;
import com.mj.employee.exception.EmployeeAlreadyExistException;
import com.mj.employee.exception.EmployeeNotFoundException;
import com.mj.employee.exception.MissingFieldException;
import com.mj.employee.payload.EmployeeDto;
import com.mj.employee.payload.EmployeePayrollRequestDto;
import com.mj.employee.payload.EmployeePayrollResponseDto;
import com.mj.employee.payload.PayrollRequestDto;
import com.mj.employee.payload.PayrollResponseDto;
import com.mj.employee.repository.EmployeeRepository;
import com.mj.employee.service.EmpPayrollService;
import com.mj.employee.service.EmployeeService;

import feign.FeignException;

@Service
public class EmpPayrollServiceImpl implements EmpPayrollService {

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private PayrollClient payrollClient;

	@Value("${payroll.service.url}")
	private String payrollServiceUrl;

	Logger logger = LoggerFactory.getLogger(EmployeePayrollController.class);

	@Override
	public EmployeePayrollResponseDto createEmployeeWithPayroll(EmployeePayrollRequestDto employeePayrollReqDto)
			throws EmployeeAlreadyExistException, MissingFieldException {

		EmployeeDto employeeDto = modelMapper.map(employeePayrollReqDto, EmployeeDto.class);
		EmployeeDto createdEmployee = employeeService.createEmployee(employeeDto);

		EmployeePayrollResponseDto employeePayrollResponseDto = modelMapper.map(createdEmployee,
				EmployeePayrollResponseDto.class);

		PayrollRequestDto payrollRequestDto = employeePayrollReqDto.getPayrollInfo();
		payrollRequestDto.setEmployeeId(createdEmployee.getId());

		try {
			PayrollResponseDto payrollResDto = payrollClient.createPayroll(payrollRequestDto);
			logger.info("Received response from Payroll Service: {}", payrollResDto);
			employeePayrollResponseDto.setPayrollInfo(payrollResDto);
			return employeePayrollResponseDto;
		} catch (FeignException.BadRequest ex) {
			employeeService.deleteEmployee(createdEmployee.getId());
			throw new MissingFieldException("Please enter all payroll details to proceed with the request");
		} catch (FeignException.Conflict ex) {
			employeeService.deleteEmployee(createdEmployee.getId());
			throw new EmployeeAlreadyExistException(
					"Payroll details for Employee ID" + createdEmployee.getId() + " is already exist");
		}
	}

	@Override
	public EmployeePayrollResponseDto getEmployeeWithPayroll(Long id) {
		Employee employee = this.employeeRepository.findById(id)
				.orElseThrow(() -> new EmployeeNotFoundException("Employee id " + id + " is not found with Id"));
		EmployeePayrollResponseDto empPayrollResDto = modelMapper.map(employee, EmployeePayrollResponseDto.class);
		try {
			PayrollResponseDto payrollResDto = payrollClient.getPayrollByEmployeeId(id);
			empPayrollResDto.setPayrollInfo(payrollResDto);
			return empPayrollResDto;
		} catch (FeignException.NotFound ex) {
			logger.error("Payroll for Employee ID {} not found in Payroll Service", id);
			empPayrollResDto.setPayrollInfo(null);
		}
		return empPayrollResDto;
	}

	@Override
	public EmployeePayrollResponseDto updateEmployeeWithPayroll(Long id,
			EmployeePayrollRequestDto employeePayrollReqDto)
			throws EmployeeAlreadyExistException, MissingFieldException {
		EmployeeDto employeeDto = modelMapper.map(employeePayrollReqDto, EmployeeDto.class);
		EmployeeDto updatedEmployee = employeeService.updateEmployee(employeeDto, id);
		EmployeePayrollResponseDto employeePayrollResponseDto = modelMapper.map(updatedEmployee,
				EmployeePayrollResponseDto.class);
		PayrollRequestDto payrollRequestDto = employeePayrollReqDto.getPayrollInfo();
		try {
			PayrollResponseDto payrollResDto = payrollClient.updatePayroll(id, payrollRequestDto);
			logger.info("Received response from Payroll Service: {}", payrollResDto);
			employeePayrollResponseDto.setPayrollInfo(payrollResDto);
			return employeePayrollResponseDto;
		} catch (FeignException.NotFound ex) {
			throw new EmployeeNotFoundException("Payroll details not found forEmployeeID:" + id);
		} catch (FeignException.BadRequest ex) {
			throw new MissingFieldException("Please enter all valid payroll details to proceed with the request");
		}
	}

	@Override
	public void deleteEmployeeWithPayroll(Long id) {
		Employee employee = this.employeeRepository.findById(id)
				.orElseThrow(() -> new EmployeeNotFoundException("Employee id " + id + " is not found with Id"));
		try {
			payrollClient.deletePayroll(id);
			logger.info("Successfully deleted payroll for employee ID: {}", id);
			employeeRepository.delete(employee);
			logger.info("Successfully deleted employee with ID: {}", id);
		} catch (FeignException.NotFound ex) {
			logger.error("Payroll not found for employee ID: {}", id);
			throw new EmployeeNotFoundException("Payroll details not found forEmployeeID:" + id);
		}
	}

}
