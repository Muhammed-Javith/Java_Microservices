package com.mj.employee.service.Impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.mj.employee.controller.EmployeeController;
import com.mj.employee.payload.LoginDto;
import com.mj.employee.security.JWTService;
import com.mj.employee.service.EmployeeAuthService;

@Service
public class EmployeeAuthServiceImpl implements EmployeeAuthService {

	@Autowired
	private JWTService jwtService;

	@Autowired
	AuthenticationManager authManager;

	Logger logger = LoggerFactory.getLogger(EmployeeController.class);

	@Override
	public String verifyTheEmployee(LoginDto loginDto) {
		Authentication authentication = authManager
				.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
		if (authentication.isAuthenticated()) {
			logger.info("Employee logged in successfully: " + loginDto.getEmail());
			return jwtService.generateToken(loginDto.getEmail());
		} else {
			return "Token generation failed!";
		}
	}

}
