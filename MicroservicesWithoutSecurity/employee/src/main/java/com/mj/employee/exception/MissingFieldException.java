package com.mj.employee.exception;

public class MissingFieldException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MissingFieldException(String message) {
		super(message);
	}

}
