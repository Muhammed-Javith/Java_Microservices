package com.mj.payroll.service;

import com.mj.payroll.exception.PayrollAlreadyExistException;
import com.mj.payroll.payload.PayrollDto;

public interface PayrollService {
	PayrollDto createPayroll(PayrollDto payrollDto) throws PayrollAlreadyExistException;

	PayrollDto getPayrollByEmployeeId(Long id);

	PayrollDto updatePayroll(PayrollDto payrollDto, Long id) throws PayrollAlreadyExistException;

	void deletePayroll(Long id);
}
