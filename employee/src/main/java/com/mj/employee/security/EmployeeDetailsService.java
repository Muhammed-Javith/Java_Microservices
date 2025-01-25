package com.mj.employee.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.mj.employee.enity.Employee;
import com.mj.employee.exception.EmployeeNotFoundException;
import com.mj.employee.repository.EmployeeRepository;

@Service
public class EmployeeDetailsService implements UserDetailsService {

	@Autowired
	private EmployeeRepository employeeRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws EmployeeNotFoundException {
		Employee employee = employeeRepository.findByEmail(username)
				.orElseThrow(() -> new EmployeeNotFoundException("Employee with email " + username + " not found"));
		return new EmployeeDetails(employee);
	}
}
