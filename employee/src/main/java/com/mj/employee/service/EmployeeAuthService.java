package com.mj.employee.service;

import com.mj.employee.payload.LoginDto;

public interface EmployeeAuthService {

	String verifyTheEmployee(LoginDto loginDto);

}
