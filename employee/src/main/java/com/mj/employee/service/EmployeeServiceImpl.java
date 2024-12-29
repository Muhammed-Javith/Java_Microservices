package com.mj.employee.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

		PayrollRequestDto payrollRequestDto = employeePayrollReqDto.getPayrollInfo();
		payrollRequestDto.setEmployeeId(Long.valueOf(createdEmployee.getId()));

		EmployeePayrollResponseDto employeePayrollResponseDto = modelMapper.map(createdEmployee,
				EmployeePayrollResponseDto.class);

		// ResponseEntity<PayrollResponseDto> payrollResponse =
		// callPayrollService(payrollRequestDto);
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

//	    //employeePayrollResponseDto.setPayrollInfo(payrollResponse.getBody());
//	    
//	    return employeePayrollResponseDto;
//
//		//ResponseEntity<String> response = callPayrollService(payrollDto);
//		//employeePayrollDto.setPayrollInfo(response.getStatusCode() == HttpStatus.CREATED ? payrollDto : null);
//		//return employeePayrollDto;
	}

//	private ResponseEntity<String> callPayrollService(PayrollRequestDto payrollDto)
//			throws EmployeeAlreadyExistException, MissingFieldException {
//		try {
//			logger.info("Calling Payroll service with URL: {}", payrollServiceUrl + "create/");
//			logger.info("Payload: {}", payrollDto);
//			return restTemplate.exchange(payrollServiceUrl + "create/", HttpMethod.POST, new HttpEntity<>(payrollDto),
//					String.class);
//		} catch (HttpClientErrorException.BadRequest ex) {
//			throw new MissingFieldException("Please enter all payroll details to proceed with the request");
//		} catch (HttpClientErrorException.Conflict ex) {
//			throw new EmployeeAlreadyExistException(
//					"Payroll details for Employee ID" + payrollDto.getEmployeeId() + " is already exist");
//		}
//	}

//	@Override
//	public EmployeePayrollDto getEmployeeWithPayroll(Long id) {
//		Employee employee = this.employeeRepository.findById(id)
//				.orElseThrow(() -> new EmployeeNotFoundException("Employee id " + id + " is not found with Id"));
//		EmployeePayrollDto employeePayrollDto = modelMapper.map(employee, EmployeePayrollDto.class);
//		try {
//			PayrollDto payrollDto = restTemplate.getForObject(payrollServiceUrl + "get/" + id, PayrollDto.class, id);
//			employeePayrollDto.setPayrollInfo(payrollDto);
//			return employeePayrollDto;
//		} catch (HttpClientErrorException.NotFound ex) {
//			logger.error("Payroll for Employee ID {} not found in Payroll Service", id);
//			employeePayrollDto.setPayrollInfo(null);
//		}
//		return employeePayrollDto;
//
//	}
}
