package com.mj.payroll.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mj.payroll.exception.MissingFieldException;
import com.mj.payroll.exception.PayrollAlreadyExistException;
import com.mj.payroll.exception.PayrollNotFoundException;
import com.mj.payroll.payload.PayrollResponse;

@RestControllerAdvice
public class PayrollExceptionHandler {

	@ExceptionHandler(PayrollNotFoundException.class)
	public ResponseEntity<PayrollResponse<?>> PayrollNotFoundExceptionHandler(PayrollNotFoundException exception) {
		PayrollResponse<?> response = new PayrollResponse<>(exception.getMessage(), HttpStatus.NOT_FOUND.value());
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(MissingFieldException.class)
	public ResponseEntity<PayrollResponse<?>> handleMissingFieldException(MissingFieldException exception) {
		PayrollResponse<?> response = new PayrollResponse<>(exception.getMessage(), HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(PayrollAlreadyExistException.class)
	public ResponseEntity<PayrollResponse<?>> handleEmailConflictException(PayrollAlreadyExistException ex) {
		PayrollResponse<?> response = new PayrollResponse<>(ex.getMessage(), HttpStatus.CONFLICT.value());
		return new ResponseEntity<>(response, HttpStatus.CONFLICT);
	}
}
