package com.mj.payroll.service;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mj.payroll.entity.Payroll;
import com.mj.payroll.exception.PayrollAlreadyExistException;
import com.mj.payroll.exception.PayrollNotFoundException;
import com.mj.payroll.payload.PayrollDto;
import com.mj.payroll.repository.PayrollRepository;

@Service
public class PayrollServiceImpl implements PayrollService {
	@Autowired
	private PayrollRepository payrollRepository;

	@Autowired
	private ModelMapper modelMapper;

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

		if (existingPayroll.isPresent()) {
			throw new PayrollAlreadyExistException("Payroll Id " + payrollDto.getEmployeeId() + " already exists.");
		} else {
			double gross = payrollDto.getHra() + payrollDto.getBasic();
			payrollDto.setGross(gross);
			Payroll payroll = this.mapToEntity(payrollDto);
			Payroll savedPayroll = this.payrollRepository.save(payroll);
			return this.mapToDto(savedPayroll);
		}
	}

	@Override
	public PayrollDto getPayrollByEmployeeId(Long id) {
		Payroll payroll = this.payrollRepository.findById(id)
				.orElseThrow(() -> new PayrollNotFoundException("Payroll id " + id + " is not found with Id"));
		return this.mapToDto(payroll);
	}

	@Override
	public PayrollDto updatePayroll(PayrollDto payrollDto, Long id) throws PayrollAlreadyExistException {
		Payroll updatePayroll = this.payrollRepository.findById(id)
				.orElseThrow(() -> new PayrollNotFoundException("Payroll id " + id + " is not found with Id"));
		updatePayroll.setHra(payrollDto.getHra());
		updatePayroll.setBasic(payrollDto.getBasic());
	    updatePayroll.setGross(payrollDto.getHra() + payrollDto.getBasic());
		Payroll updatedPayroll = this.payrollRepository.save(updatePayroll);
		return this.mapToDto(updatedPayroll);
	}

	@Override
	public void deletePayroll(Long id) throws PayrollNotFoundException {
		Payroll payroll = this.payrollRepository.findById(id)
				.orElseThrow(() -> new PayrollNotFoundException("Payroll id " + id + " is not found with Id"));
		this.payrollRepository.delete(payroll);
	}

}
