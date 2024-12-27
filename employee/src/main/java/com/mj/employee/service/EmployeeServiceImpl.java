package com.mj.employee.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mj.employee.Payload.EmployeeDto;
import com.mj.employee.config.EmployeeMapper;
import com.mj.employee.entity.Employee;
import com.mj.employee.exception.EmployeeAlreadyExistException;
import com.mj.employee.exception.EmployeeNotFoundException;
import com.mj.employee.repository.EmployeeRepository;

@Service
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;

	private final EmployeeMapper employeeMapper;

	@Autowired
	public EmployeeServiceImpl(EmployeeMapper employeeMapper) {
		this.employeeMapper = employeeMapper;
	}

	@Override
	public EmployeeDto createEmployee(EmployeeDto employeeDto) throws EmployeeAlreadyExistException {
		try {
			getEmployeeByEmail(employeeDto.getEmail());
			throw new EmployeeAlreadyExistException("Employee email " + employeeDto.getEmail() + " already exists ");
		} catch (EmployeeNotFoundException ex) {
			Employee employee = employeeMapper.mapToEntity(employeeDto);
			Employee savedemployee = this.employeeRepository.save(employee);
			return employeeMapper.mapToDto(savedemployee);
		}
	}

	@Override
	public EmployeeDto getEmployeeById(Long id) {
		Employee employee = this.employeeRepository.findById(id)
				.orElseThrow(() -> new EmployeeNotFoundException("Employee id " + id + " is not found with Id"));
		return employeeMapper.mapToDto(employee);
	}

	@Override
	public List<EmployeeDto> getAllEmployee() {
		List<Employee> employees = this.employeeRepository.findAll();
		return employees.stream().map(employee -> employeeMapper.mapToDto(employee)).collect(Collectors.toList());
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
		return employeeMapper.mapToDto(updatedEmployee);
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
		return employeeMapper.mapToDto(employee);

	}
}
