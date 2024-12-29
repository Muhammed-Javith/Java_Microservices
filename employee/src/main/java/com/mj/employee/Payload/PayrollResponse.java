package com.mj.employee.Payload;

public class PayrollResponse<T> {
	private String statusMessage;
	private int statusCode;
	private T payrollInfo;
	
	public PayrollResponse() {
		super();
	}
	public PayrollResponse(String statusMessage, int statusCode) {
		super();
		this.statusMessage = statusMessage;
		this.statusCode = statusCode;
	}
	public PayrollResponse(String statusMessage, int statusCode, T payrollInfo) {
		super();
		this.statusMessage = statusMessage;
		this.statusCode = statusCode;
		this.payrollInfo = payrollInfo;
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
	public T getPayrollInfo() {
		return payrollInfo;
	}
	public void setPayrollInfo(T payrollInfo) {
		this.payrollInfo = payrollInfo;
	}
	


}
