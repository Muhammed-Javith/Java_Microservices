package com.mj.payroll.service.Impl;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.mj.payroll.controller.PayrollController;
import com.mj.payroll.entity.Payroll;
import com.mj.payroll.exception.PayrollAlreadyExistException;
import com.mj.payroll.exception.PayrollNotFoundException;
import com.mj.payroll.payload.PayrollDto;
import com.mj.payroll.repository.PayrollRepository;
import com.mj.payroll.service.PayrollService;

@Service
public class PayrollServiceImpl implements PayrollService {
	@Autowired
	private PayrollRepository payrollRepository;

	@Autowired
	private ModelMapper modelMapper;

	Logger logger = LoggerFactory.getLogger(PayrollController.class);

	// user payrollDto to payrollentity conversion
	public Payroll mapToEntity(PayrollDto payrollDto) {
		Payroll payroll = this.modelMapper.map(payrollDto, Payroll.class);
		return payroll;
	}

	// payrolleentity to payrolldto conversion
	public PayrollDto mapToDto(Payroll payroll) {
		PayrollDto payrollDto = this.modelMapper.map(payroll, PayrollDto.class);
		return payrollDto;
	}

	@Override
	public PayrollDto createPayroll(PayrollDto payrollDto) throws PayrollAlreadyExistException {
		Optional<Payroll> existingPayroll = this.payrollRepository.findById(payrollDto.getEmployeeId());
		logger.info("Received response from Payroll Service: {}", existingPayroll);
		if (existingPayroll.isPresent()) {
			logger.info("Received response from Payroll Service: {}");
			throw new PayrollAlreadyExistException("Employee Id " + payrollDto.getEmployeeId() + " already exists.");
		} else {
			double gross = payrollDto.getHra() + payrollDto.getBasic();
			payrollDto.setTotalSalary(gross);
			Payroll payroll = this.mapToEntity(payrollDto);
			Payroll savedPayroll = this.payrollRepository.save(payroll);
			logger.info("Received response from Payroll Service: {}", savedPayroll);
			return this.mapToDto(savedPayroll);
		}
	}

	@Cacheable(value = "payroll", key = "#id")
	@Override
	public PayrollDto getPayrollByEmployeeId(Long id) {
		Payroll payroll = this.payrollRepository.findById(id)
				.orElseThrow(() -> new PayrollNotFoundException("Payroll id " + id + " is not found with Id"));
		return this.mapToDto(payroll);
	}

	@CachePut(value = "payroll", key = "#id")
	@Override
	public PayrollDto updatePayroll(PayrollDto payrollDto, Long id) throws PayrollAlreadyExistException {
		Payroll updatePayroll = this.payrollRepository.findById(id)
				.orElseThrow(() -> new PayrollNotFoundException("Payroll id " + id + " is not found with Id"));
		updatePayroll = this.mapToEntity(payrollDto);
		updatePayroll.setEmployeeId(id);
		updatePayroll.setTotalSalary(payrollDto.getHra() + payrollDto.getBasic());
		Payroll updatedPayroll = this.payrollRepository.save(updatePayroll);
		return this.mapToDto(updatedPayroll);
	}

	@CacheEvict(value = "payroll", key = "#id")
	@Override
	public void deletePayroll(Long id) throws PayrollNotFoundException {
		Payroll payroll = this.payrollRepository.findById(id)
				.orElseThrow(() -> new PayrollNotFoundException("Payroll id " + id + " is not found with Id"));
		this.payrollRepository.delete(payroll);
	}

}
