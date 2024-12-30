package com.mj.employee.service;

import java.util.List;
import java.util.stream.Collectors;

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

import com.mj.employee.controller.EmployeeController;
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

@Service
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private RestTemplate restTemplate;

	@Value("${payroll.service.url}")
	private String payrollServiceUrl;

	Logger logger = LoggerFactory.getLogger(EmployeeController.class);

	// user employeDto to employeentity conversion
	public Employee mapToEntity(EmployeeDto employeeDto) {
		Employee employee = this.modelMapper.map(employeeDto, Employee.class);
		return employee;
	}

	// employeeentity to employedto conversion
	public EmployeeDto mapToDto(Employee employee) {
		EmployeeDto employeeDto = this.modelMapper.map(employee, EmployeeDto.class);
		return employeeDto;
	}

	@Override
	public EmployeeDto createEmployee(EmployeeDto employeeDto) throws EmployeeAlreadyExistException {
		try {
			getEmployeeByEmail(employeeDto.getEmail());
			throw new EmployeeAlreadyExistException("Employee email " + employeeDto.getEmail() + " already exists ");
		} catch (EmployeeNotFoundException ex) {
			Employee employee = this.mapToEntity(employeeDto);
			Employee savedemployee = this.employeeRepository.save(employee);
			return this.mapToDto(savedemployee);
		}
	}

	@Override
	public EmployeeDto getEmployeeById(Long id) {
		Employee employee = this.employeeRepository.findById(id)
				.orElseThrow(() -> new EmployeeNotFoundException("Employee id " + id + " is not found with Id"));
		return this.mapToDto(employee);
	}

	@Override
	public List<EmployeeDto> getAllEmployee() {
		List<Employee> employees = this.employeeRepository.findAll();
		return employees.stream().map(employee -> this.mapToDto(employee)).collect(Collectors.toList());
	}

	@Override
	public EmployeeDto updateEmployee(EmployeeDto employeeDto, Long id) throws EmployeeAlreadyExistException {
		Employee updateEmployee = this.employeeRepository.findById(id)
				.orElseThrow(() -> new EmployeeNotFoundException("Employee id " + id + " is not found with Id"));
		if (!updateEmployee.getEmail().equals(employeeDto.getEmail())
				&& this.employeeRepository.existsByEmail(employeeDto.getEmail())) {
			throw new EmployeeAlreadyExistException(
					"Employee email " + employeeDto.getEmail() + " already used by another Employee");
		}
		updateEmployee.setName(employeeDto.getName());
		updateEmployee.setEmail(employeeDto.getEmail());
		updateEmployee.setAddress(employeeDto.getAddress());
		Employee updatedEmployee = this.employeeRepository.save(updateEmployee);
		return this.mapToDto(updatedEmployee);
	}

	@Override
	public void deleteEmployee(Long id) throws EmployeeNotFoundException {
		Employee employee = this.employeeRepository.findById(id)
				.orElseThrow(() -> new EmployeeNotFoundException("Employee id " + id + " is not found with Id"));
		this.employeeRepository.delete(employee);
	}

	@Override
	public EmployeeDto getEmployeeByEmail(String email) {
		Employee employee = this.employeeRepository.findByEmail(email)
				.orElseThrow(() -> new EmployeeNotFoundException("Employee id " + email + " is not found with email"));
		return this.mapToDto(employee);

	}

	@Override
	public EmployeePayrollResponseDto createEmployeeWithPayroll(EmployeePayrollRequestDto employeePayrollReqDto)
			throws EmployeeAlreadyExistException, MissingFieldException {

		Employee employee = modelMapper.map(employeePayrollReqDto, Employee.class);
		EmployeeDto createdEmployee = createEmployee(this.mapToDto(employee));

		EmployeePayrollResponseDto employeePayrollResponseDto = modelMapper.map(createdEmployee,
				EmployeePayrollResponseDto.class);

		PayrollRequestDto payrollRequestDto = employeePayrollReqDto.getPayrollInfo();
		//payrollRequestDto.setEmployeeId(Long.valueOf(createdEmployee.getId()));

		try {
			String payrollCreateUrl = payrollServiceUrl + "create/"; // URL for MS2 (no employeeId in the path now)
			PayrollResponseDto payrollResDto = restTemplate.postForObject(payrollCreateUrl, payrollRequestDto,
					PayrollResponseDto.class);
			logger.info("Received response from Payroll Service: {}", employeePayrollResponseDto);
			employeePayrollResponseDto.setPayrollInfo(payrollResDto);
			return employeePayrollResponseDto;
		} catch (HttpClientErrorException.BadRequest ex) {
			throw new MissingFieldException("Please enter all payroll details to proceed with the request");
		} catch (HttpClientErrorException.Conflict ex) {
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
		//Employee employee = modelMapper.map(employeePayrollReqDto, Employee.class);
		EmployeeDto employeeDto = modelMapper.map(employeePayrollReqDto, EmployeeDto.class);
		//EmployeeDto employeeDto = this.mapToDto(employee);
		EmployeeDto updatedEmployee = updateEmployee(employeeDto, id);
		EmployeePayrollResponseDto employeePayrollResponseDto = modelMapper.map(updatedEmployee,
				EmployeePayrollResponseDto.class);
		PayrollRequestDto payrollRequestDto = employeePayrollReqDto.getPayrollInfo();
		try {
			String payrollUpdateUrl = payrollServiceUrl + "update/" + id; // URL for MS2 (no employeeId in the path now)
            PayrollResponseDto payrollResDto = restTemplate.exchange(payrollUpdateUrl, HttpMethod.PUT, new HttpEntity<>(payrollRequestDto), PayrollResponseDto.class).getBody();
            logger.info("Received response from Payroll Service: {}", payrollResDto);
            employeePayrollResponseDto.setPayrollInfo(payrollResDto);
            return employeePayrollResponseDto;
		}catch(HttpClientErrorException.NotFound ex) {
			//throw new EmployeeNotFoundException("Payroll details not found for Employee ID: " + id);
			throw new EmployeeNotFoundException(ex.getMessage());
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
