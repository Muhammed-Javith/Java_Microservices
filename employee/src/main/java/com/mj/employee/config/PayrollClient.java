package com.mj.employee.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.mj.employee.payload.PayrollRequestDto;
import com.mj.employee.payload.PayrollResponseDto;

@FeignClient(name = "payroll-service", url = "${payroll.service.url}")
public interface PayrollClient {

	@PostMapping("create")
	PayrollResponseDto createPayroll(@RequestBody PayrollRequestDto employeePayrollReqDto);

	@GetMapping("get/{id}")
	PayrollResponseDto getPayrollByEmployeeId(@PathVariable Long id);

	@PutMapping("update/{id}")
	PayrollResponseDto updatePayroll(@PathVariable Long id, @RequestBody PayrollRequestDto employeePayrollReqDto);

	@DeleteMapping("del/{id}")
	void deletePayroll(@PathVariable Long id);

}
