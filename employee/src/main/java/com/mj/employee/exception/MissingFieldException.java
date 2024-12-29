package com.mj.employee.exception;

public class MissingFieldException extends Exception {

	private static final long serialVersionUID = 1L;

	public MissingFieldException(String message) {
		super(message);
	}

}
