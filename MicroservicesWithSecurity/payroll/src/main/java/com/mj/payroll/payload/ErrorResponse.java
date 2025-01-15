package com.mj.payroll.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

	private int status;
	private String message;
	private String timeStamp;
	private String error;

}
