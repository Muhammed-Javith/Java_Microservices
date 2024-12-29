package com.mj.employee.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.mj.employee.Payload.EmployeeDto;
import com.mj.employee.Payload.EmployeePayrollDto;
import com.mj.employee.Payload.PayrollDto;
import com.mj.employee.Payload.PayrollResponse;
import com.mj.employee.entity.Employee;
import com.mj.employee.exception.EmployeeAlreadyExistException;
import com.mj.employee.exception.EmployeeNotFoundException;
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

	private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

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
	public EmployeePayrollDto getEmployeeWithPayroll(Long id) {
		Employee employee = this.employeeRepository.findById(id)
				.orElseThrow(() -> new EmployeeNotFoundException("Employee id " + id + " is not found with Id"));
		EmployeePayrollDto employeePayrollDto = modelMapper.map(employee, EmployeePayrollDto.class);
		logger.info("Employee with payroll details: {}", employeePayrollDto);
		ParameterizedTypeReference<PayrollResponse<PayrollDto>> responseType = new ParameterizedTypeReference<PayrollResponse<PayrollDto>>() {
		};
		ResponseEntity<PayrollResponse<PayrollDto>> responseEntity = restTemplate
				.exchange(payrollServiceUrl + "get/" + id, HttpMethod.GET, null, responseType);

		PayrollResponse<PayrollDto> response = responseEntity.getBody();

		// Check if the response is successful
		if (response != null && response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
			PayrollDto payrollDto = response.getPayrollInfo(); // Assuming getPayrollInfo() is present in
																// PayrollResponse
			if (payrollDto != null) {
				employeePayrollDto.setPayrollInfo(payrollDto);
			} else {
				throw new RuntimeException("Payroll information not found for Employee ID: " + id);
			}
		} else {
			// Handle error responses if status is not 2xx (200, 201, etc.)
			throw new RuntimeException("Something went wrong while retrieving payroll details for Employee ID: " + id);
		}
		return employeePayrollDto;
	}

}
