package com.mj.employer.exception;

public class EmployerAlreadyExistException extends Exception {
	private static final long serialVersionUID = 1L;

	public EmployerAlreadyExistException(String message) {
		super(message);
	}
}
