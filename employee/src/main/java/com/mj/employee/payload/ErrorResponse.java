package com.mj.employee.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

	private int statusCode;
	private String StatusMessage;
	private String errorMessage;
	private String timeStamp;

}
