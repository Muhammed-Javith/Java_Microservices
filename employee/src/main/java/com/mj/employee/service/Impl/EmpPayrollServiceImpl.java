package com.mj.employee.service.Impl;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

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

@Service
public class EmpPayrollServiceImpl implements EmpPayrollService {

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private RestTemplate restTemplate;

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
			PayrollResponseDto payrollResDto = restTemplate.postForObject(payrollServiceUrl + "create",
					payrollRequestDto, PayrollResponseDto.class);
			logger.info("Received response from Payroll Service: {}", payrollResDto);
			employeePayrollResponseDto.setPayrollInfo(payrollResDto);
			return employeePayrollResponseDto;
		} catch (HttpClientErrorException.BadRequest ex) {
			employeeService.deleteEmployee(createdEmployee.getId());
			throw new MissingFieldException("Please enter all payroll details to proceed with the request");
		} catch (HttpClientErrorException.Conflict ex) {
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
			PayrollResponseDto payrollResDto = restTemplate.getForObject(payrollServiceUrl + "get/" + id,
					PayrollResponseDto.class, id);
			empPayrollResDto.setPayrollInfo(payrollResDto);
			return empPayrollResDto;
		} catch (HttpClientErrorException.NotFound ex) {
			logger.error("Payroll for Employee ID {} not found in Payroll Service", id);
			empPayrollResDto.setPayrollInfo(null);
		}
		return empPayrollResDto;
	}

	@Override
	public EmployeePayrollResponseDto updateEmployeeWithPayroll(Long id,
			EmployeePayrollRequestDto employeePayrollReqDto) throws EmployeeAlreadyExistException {
		EmployeeDto employeeDto = modelMapper.map(employeePayrollReqDto, EmployeeDto.class);
		EmployeeDto updatedEmployee = employeeService.updateEmployee(employeeDto, id);
		EmployeePayrollResponseDto employeePayrollResponseDto = modelMapper.map(updatedEmployee,
				EmployeePayrollResponseDto.class);
		PayrollRequestDto payrollRequestDto = employeePayrollReqDto.getPayrollInfo();
		try {
			String payrollUpdateUrl = payrollServiceUrl + "update/" + id; // URL for MS2 (no employeeId in the path now)
			PayrollResponseDto payrollResDto = restTemplate.exchange(payrollUpdateUrl, HttpMethod.PUT,
					new HttpEntity<>(payrollRequestDto), PayrollResponseDto.class).getBody();
			logger.info("Received response from Payroll Service: {}", payrollResDto);
			employeePayrollResponseDto.setPayrollInfo(payrollResDto);
			return employeePayrollResponseDto;
		} catch (HttpClientErrorException.NotFound ex) {
			throw new EmployeeNotFoundException("Payroll details not found forEmployeeID:" + id);
		}
	}

	@Override
	public void deleteEmployeeWithPayroll(Long id) {
		Employee employee = this.employeeRepository.findById(id)
				.orElseThrow(() -> new EmployeeNotFoundException("Employee id " + id + " is not found with Id"));
		try {
			restTemplate.delete(payrollServiceUrl + "del/" + id, id);
			logger.info("Successfully deleted payroll for employee ID: {}", id);
			employeeRepository.delete(employee);
			logger.info("Successfully deleted employee with ID: {}", id);
		} catch (HttpClientErrorException.NotFound ex) {
			logger.error("Payroll not found for employee ID: {}", id);
			throw new EmployeeNotFoundException(ex.getMessage());
		}
	}
}
