package com.mj.employee.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mj.employee.Payload.EmployeeResponse;
import com.mj.employee.exception.EmployeeAlreadyExistException;
import com.mj.employee.exception.EmployeeNotFoundException;
import com.mj.employee.exception.MissingFieldException;

@RestControllerAdvice
public class EmployeeExceptionHandler {

	@ExceptionHandler(EmployeeNotFoundException.class)
	public ResponseEntity<EmployeeResponse<?>> EmployeeNotFoundExceptionHandler(EmployeeNotFoundException exception) {
		EmployeeResponse<?> response = new EmployeeResponse<>(exception.getMessage(), HttpStatus.NOT_FOUND.value());
		//return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		return new ResponseEntity<>(response,HttpStatus.OK);
	}

	@ExceptionHandler(MissingFieldException.class)
	public ResponseEntity<EmployeeResponse<?>> handleMissingFieldException(MissingFieldException exception) {
		EmployeeResponse<?> response = new EmployeeResponse<>(exception.getMessage(), HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(EmployeeAlreadyExistException.class)
	public ResponseEntity<EmployeeResponse<?>> handleEmailConflictException(EmployeeAlreadyExistException ex) {
		EmployeeResponse<?> response = new EmployeeResponse<>(ex.getMessage(), HttpStatus.CONFLICT.value());
		return new ResponseEntity<>(response, HttpStatus.CONFLICT);
	}
}
