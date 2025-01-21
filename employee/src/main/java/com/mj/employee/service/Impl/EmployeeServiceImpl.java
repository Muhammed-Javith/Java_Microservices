package com.mj.employee.service.Impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.mj.employee.controller.EmployeeController;
import com.mj.employee.enity.Employee;
import com.mj.employee.exception.EmployeeAlreadyExistException;
import com.mj.employee.exception.EmployeeNotFoundException;
import com.mj.employee.payload.EmployeeDto;
import com.mj.employee.repository.EmployeeRepository;
import com.mj.employee.service.EmployeeService;

@Service
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private ModelMapper modelMapper;

	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

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
			employee.setPassword(encoder.encode(employee.getPassword()));
			Employee savedemployee = this.employeeRepository.save(employee);
			return this.mapToDto(savedemployee);
		}
	}

	@Cacheable(value = "employees", key = "#id")
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

	@CachePut(value = "employees", key = "#id")
	@Override
	public EmployeeDto updateEmployee(EmployeeDto employeeDto, Long id) throws EmployeeAlreadyExistException {
		Employee updateEmployee = this.employeeRepository.findById(id)
				.orElseThrow(() -> new EmployeeNotFoundException("Employee id " + id + " is not found with Id"));
		if (!updateEmployee.getEmail().equals(employeeDto.getEmail())
				&& this.employeeRepository.existsByEmail(employeeDto.getEmail())) {
			throw new EmployeeAlreadyExistException(
					"Employee email " + employeeDto.getEmail() + " already used by another Employee");
		}
		updateEmployee = this.mapToEntity(employeeDto);
		updateEmployee.setId(id);
		Employee updatedEmployee = this.employeeRepository.save(updateEmployee);
		return this.mapToDto(updatedEmployee);
	}

	@CacheEvict(value = "employees", key = "#id")
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

}
