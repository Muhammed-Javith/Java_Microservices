package com.mj.employer.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mj.employer.exception.MissingFieldException;
import com.mj.employer.exception.EmployerAlreadyExistException;
import com.mj.employer.exception.EmployerNotFoundException;

@RestControllerAdvice
public class PayrollExceptionHandler {

	@ExceptionHandler(EmployerNotFoundException.class)
	public ResponseEntity<String> handleEmployeeNotFoundException(EmployerNotFoundException exception) {
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(MissingFieldException.class)
	public ResponseEntity<String> handleMissingFieldException(MissingFieldException exception) {
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(EmployerAlreadyExistException.class)
	public ResponseEntity<String> handleEmailConflictException(EmployerAlreadyExistException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
	}
}
