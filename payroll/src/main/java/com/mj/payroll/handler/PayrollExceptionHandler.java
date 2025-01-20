package com.mj.payroll.handler;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mj.payroll.exception.MissingFieldException;
import com.mj.payroll.exception.PayrollAlreadyExistException;
import com.mj.payroll.exception.PayrollNotFoundException;
import com.mj.payroll.payload.ErrorResponse;

@RestControllerAdvice
public class PayrollExceptionHandler {

	@ExceptionHandler(PayrollNotFoundException.class)
	public ResponseEntity<ErrorResponse> handlePayrollNotFoundException(PayrollNotFoundException exception) {
		return buildErrorResponse(exception.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(MissingFieldException.class)
	public ResponseEntity<ErrorResponse> handleMissingFieldException(MissingFieldException exception) {
		return buildErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(PayrollAlreadyExistException.class)
	public ResponseEntity<ErrorResponse> handleEmailConflictException(PayrollAlreadyExistException exception) {
		return buildErrorResponse(exception.getMessage(), HttpStatus.CONFLICT);
	}

	private ResponseEntity<ErrorResponse> buildErrorResponse(String message, HttpStatus status) {
		ErrorResponse errorResponse = new ErrorResponse(status.value(), status.getReasonPhrase(),
				LocalDateTime.now().toString(), message);
		return new ResponseEntity<>(errorResponse, status);
	}
}
