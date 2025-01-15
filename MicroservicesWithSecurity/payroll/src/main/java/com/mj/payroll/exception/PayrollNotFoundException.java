package com.mj.payroll.exception;

public class PayrollNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PayrollNotFoundException(String message) {
		super(message);
	}
}
