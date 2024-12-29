package com.mj.payroll.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mj.payroll.exception.PayrollAlreadyExistException;
import com.mj.payroll.exception.PayrollNotFoundException;
import com.mj.payroll.exception.MissingFieldException;

@RestControllerAdvice
public class PayrollExceptionHandler {

	@ExceptionHandler(PayrollNotFoundException.class)
	public ResponseEntity<String> handleEmployeeNotFoundException(PayrollNotFoundException exception) {
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(MissingFieldException.class)
	public ResponseEntity<String> handleMissingFieldException(MissingFieldException exception) {
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(PayrollAlreadyExistException.class)
	public ResponseEntity<String> handleEmailConflictException(PayrollAlreadyExistException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
	}
}
