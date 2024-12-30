package com.mj.employer.service.Impl;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mj.employer.controller.EmployerController;
import com.mj.employer.entity.Employer;
import com.mj.employer.exception.EmployerAlreadyExistException;
import com.mj.employer.exception.EmployerNotFoundException;
import com.mj.employer.payload.EmployerDto;
import com.mj.employer.repository.EmployerRepository;
import com.mj.employer.service.EmployerService;

@Service
public class EmployerServiceImpl implements EmployerService {
	@Autowired
	private EmployerRepository employerRepository;

	@Autowired
	private ModelMapper modelMapper;

	Logger logger = LoggerFactory.getLogger(EmployerController.class);

	// EmployerDto to Employer entity conversion
	public Employer mapToEntity(EmployerDto employerDto) {
		Employer employer = this.modelMapper.map(employerDto, Employer.class);
		return employer;
	}

	// Employer entity to EmployerDto conversion
	public EmployerDto mapToDto(Employer employer) {
		EmployerDto employerDto = this.modelMapper.map(employer, EmployerDto.class);
		return employerDto;
	}

	@Override
	public EmployerDto createEmployer(EmployerDto employerDto) throws EmployerAlreadyExistException {
		Optional<Employer> existingEmployer = this.employerRepository.findById(employerDto.getEmployeeId());
		logger.info("Received response from Employer Service: {}", existingEmployer);
		if (existingEmployer.isPresent()) {
			logger.info("Received response from Employer Service: {}");
			throw new EmployerAlreadyExistException("Employee Id " + employerDto.getEmployeeId() + " already exists.");
		} else {
			Employer employer = this.mapToEntity(employerDto);
			Employer savedEmployer = this.employerRepository.save(employer);
			logger.info("Received response from Employer Service: {}", savedEmployer);
			return this.mapToDto(savedEmployer);
		}
	}

	@Override
	public EmployerDto getEmployerByEmployeeId(Long id) {
		Employer employer = this.employerRepository.findById(id)
				.orElseThrow(() -> new EmployerNotFoundException("Employer id " + id + " is not found with Id"));
		return this.mapToDto(employer);
	}

	@Override
	public EmployerDto updateEmployer(EmployerDto employerDto, Long id) throws EmployerAlreadyExistException {
		Employer updateEmployer = this.employerRepository.findById(id)
				.orElseThrow(() -> new EmployerNotFoundException("Employer id " + id + " is not found with Id"));
		updateEmployer = this.mapToEntity(employerDto);
		updateEmployer.setEmployeeId(id);
		Employer updatedEmployer = this.employerRepository.save(updateEmployer);
		return this.mapToDto(updatedEmployer);
	}

	@Override
	public void deleteEmployer(Long id) throws EmployerNotFoundException {
		Employer employer = this.employerRepository.findById(id)
				.orElseThrow(() -> new EmployerNotFoundException("Employer id " + id + " is not found with Id"));
		this.employerRepository.delete(employer);
	}
}