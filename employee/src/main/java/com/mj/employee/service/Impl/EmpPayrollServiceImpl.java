package com.mj.employee.service.Impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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

	Logger logger = LoggerFactory.getLogger(EmployeePayrollController.class);

	@Override
	public EmployeePayrollResponseDto createEmployeeWithPayroll(EmployeePayrollRequestDto employeePayrollReqDto)
			throws EmployeeAlreadyExistException, MissingFieldException {

		EmployeeDto employeeDto = modelMapper.map(employeePayrollReqDto, EmployeeDto.class);
		EmployeeDto createdEmployee = null;
		try {
			// Try to create the employee
			createdEmployee = employeeService.createEmployee(employeeDto);
		} catch (EmployeeAlreadyExistException ex) {
			// If employee already exists, return the exception
			logger.error("Employee already exists: {}", ex.getMessage());
			throw new EmployeeAlreadyExistException(ex.getMessage());
		}
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
			throw new MissingFieldException(ex.contentUTF8());
		} catch (FeignException.Conflict ex) {
			logger.info(ex.getMessage());
			employeeService.deleteEmployee(createdEmployee.getId());
			throw new EmployeeAlreadyExistException(ex.contentUTF8());
		}
	}

	// @Cacheable(value = "employees", key = "#id")
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
	public Map<String, Object> getAllEmployeesWithPayroll(int page, int size, String sortBy, boolean ascending) {
		Sort.Direction direction = ascending ? Sort.Direction.ASC : Sort.Direction.DESC;
		Pageable pageable = PageRequest.of(page, size, direction, sortBy);
		Page<Employee> employeesPage = employeeRepository.findAll(pageable);
		// Initialize a counter for skipped employees
		AtomicInteger skippedCount = new AtomicInteger();
		// Filter and map employees with payroll details
		List<EmployeePayrollResponseDto> employeePayrollList = employeesPage.getContent().stream().map(employee -> {
			try {
				// Fetch payroll details for the employee
				PayrollResponseDto payroll = payrollClient.getPayrollByEmployeeId(employee.getId());
				// Map employee to EmployeePayrollResponseDto
				return EmployeePayrollResponseDto.builder().id(employee.getId()).name(employee.getName())
						.email(employee.getEmail()).password(employee.getPassword()).level(employee.getLevel())
						.designation(employee.getDesignation()).department(employee.getDepartment())
						.phoneNumber(employee.getPhoneNumber()).address(employee.getAddress()).payrollInfo(payroll)
						.build();
			} catch (Exception ex) {
				// Increment the skipped count if payroll details are not found or an exception
				// occurs
				skippedCount.incrementAndGet();
				return null; // Returning null for skipped employees
			}
		}).filter(Objects::nonNull) // Remove null values (skipped employees)
				.collect(Collectors.toList());

		// Prepare the response map
		Map<String, Object> response = new LinkedHashMap<>();
		response.put("Employee with Payroll details", employeePayrollList);
		response.put("Employee who doesn't have Payroll details", skippedCount.get());
		response.put("Total elements", employeesPage.getTotalElements());
		response.put("Total pages", employeesPage.getTotalPages());
		response.put("Current page", employeesPage.getNumber());
		return response;
	}

	@CachePut(value = "employees", key = "#id")
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
			throw new EmployeeNotFoundException(ex.contentUTF8());
		} catch (FeignException.BadRequest ex) {
			throw new MissingFieldException(ex.contentUTF8());
		}
	}

	@CacheEvict(value = "employees", key = "#id")
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
			logger.error("Payroll not found for employee ID: {}", id, ex.contentUTF8());
			throw new EmployeeNotFoundException(ex.contentUTF8());
		}
	}

	@Override
	public List<EmployeePayrollResponseDto> createEmployees(List<EmployeePayrollRequestDto> employeePayrollReqDtos)
			throws EmployeeAlreadyExistException {
		List<EmployeePayrollResponseDto> employeePayrollResponseDtos = new ArrayList<>();

		for (EmployeePayrollRequestDto employeePayrollReqDto : employeePayrollReqDtos) {
			EmployeeDto employeeDto = modelMapper.map(employeePayrollReqDto, EmployeeDto.class);
			EmployeeDto createdEmployee;
			try {
				createdEmployee = employeeService.createEmployee(employeeDto);
			} catch (EmployeeAlreadyExistException e) {
				// Skip this employee if the email already exists
				logger.warn("Skipping employee with email {}: {}", employeeDto.getEmail(), e.getMessage());
				continue;
			}

			EmployeePayrollResponseDto employeePayrollResponseDto = modelMapper.map(createdEmployee,
					EmployeePayrollResponseDto.class);
			PayrollRequestDto payrollRequestDto = employeePayrollReqDto.getPayrollInfo();
			payrollRequestDto.setEmployeeId(createdEmployee.getId());

			try {
				PayrollResponseDto payrollResDto = payrollClient.createPayroll(payrollRequestDto);
				logger.info("Received response from Payroll Service: {}", payrollResDto);

				employeePayrollResponseDto.setPayrollInfo(payrollResDto);
				employeePayrollResponseDtos.add(employeePayrollResponseDto);
			} catch (FeignException.BadRequest ex) {
				employeeService.deleteEmployee(createdEmployee.getId());
				throw new MissingFieldException(ex.contentUTF8());
			} catch (FeignException.Conflict ex) {
				employeeService.deleteEmployee(createdEmployee.getId());
				throw new EmployeeAlreadyExistException(ex.contentUTF8());
			}
		}

		return employeePayrollResponseDtos;
	}

}
