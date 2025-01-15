package com.mj.payroll.exception;

public class PayrollAlreadyExistException extends Exception {
	private static final long serialVersionUID = 1L;

	public PayrollAlreadyExistException(String message) {
		super(message);
	}
}
