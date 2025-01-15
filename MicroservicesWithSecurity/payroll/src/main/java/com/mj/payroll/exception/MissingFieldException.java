package com.mj.payroll.exception;

public class MissingFieldException extends Exception {

	private static final long serialVersionUID = 1L;

	public MissingFieldException(String message) {
		super(message);
	}

}
