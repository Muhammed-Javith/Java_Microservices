package com.mj.employee.Payload;

import com.fasterxml.jackson.annotation.JsonInclude;

//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class EmployeeResponse<T> {
//	private String statusMessage;
//	private int statusCode;
//	private T data;
//}

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeResponse<T> {
	private String statusMessage;
	private int statusCode;
	private T employeeDetails;

	public EmployeeResponse(String statusMessage, int statusCode, T employeeDetails) {
		super();
		this.statusMessage = statusMessage;
		this.statusCode = statusCode;
		this.employeeDetails = employeeDetails;
	}

	public EmployeeResponse(String statusMessage, int statusCode) {
		super();
		this.statusMessage = statusMessage;
		this.statusCode = statusCode;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public T getEmployeeDetails() {
		return employeeDetails;
	}

	public void setEmployeeDetails(T employeeDetails) {
		this.employeeDetails = employeeDetails;
	}

}
