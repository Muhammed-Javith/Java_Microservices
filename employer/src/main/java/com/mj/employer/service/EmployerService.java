package com.mj.employer.service;

import com.mj.employer.exception.EmployerAlreadyExistException;
import com.mj.employer.payload.EmployerDto;

public interface EmployerService {

	EmployerDto createEmployer(EmployerDto employerDto) throws EmployerAlreadyExistException;

	EmployerDto getEmployerByEmployeeId(Long id);

	EmployerDto updateEmployer(EmployerDto employerDto, Long id) throws EmployerAlreadyExistException;

	void deleteEmployer(Long id);

}
