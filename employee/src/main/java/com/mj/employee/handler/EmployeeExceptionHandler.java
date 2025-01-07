package com.mj.employee.handler;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mj.employee.exception.EmployeeAlreadyExistException;
import com.mj.employee.exception.EmployeeNotFoundException;
import com.mj.employee.exception.InvalidFileException;
import com.mj.employee.exception.MissingFieldException;
import com.mj.employee.payload.ErrorResponse;

@RestControllerAdvice
public class EmployeeExceptionHandler {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@ExceptionHandler({ EmployeeNotFoundException.class, MissingFieldException.class,
			EmployeeAlreadyExistException.class, InvalidFileException.class})
	public ResponseEntity<ErrorResponse> handleExceptions(Exception exception) {
		HttpStatus status = getStatusFromException(exception);
		String message = exception.getMessage();
		return buildErrorResponse(message, status);
	}

	private HttpStatus getStatusFromException(Exception exception) {
		if (exception instanceof EmployeeNotFoundException) {
			return HttpStatus.NOT_FOUND;
		} else if (exception instanceof MissingFieldException) {
			return HttpStatus.BAD_REQUEST;
		} else if (exception instanceof EmployeeAlreadyExistException) {
			return HttpStatus.CONFLICT;
		}
		else if (exception instanceof InvalidFileException) {
            return HttpStatus.UNSUPPORTED_MEDIA_TYPE;
        }
		return HttpStatus.INTERNAL_SERVER_ERROR; // Default status
	}

	private ResponseEntity<ErrorResponse> buildErrorResponse(String message, HttpStatus status) {
		// Check if the message is a JSON string
		String extractedMessage = isJsonString(message) ? extractErrorMessage(message) : message;
		ErrorResponse errorResponse = new ErrorResponse(status.value(), extractedMessage,
				LocalDateTime.now().toString(), status.getReasonPhrase());
		return new ResponseEntity<>(errorResponse, status);
	}

	private boolean isJsonString(String message) {
		try {
			objectMapper.readTree(message); // Try parsing as JSON
			return true;
		} catch (Exception e) {
			return false; // If parsing fails, it's not a JSON string
		}
	}

	private String extractErrorMessage(String jsonString) {
		try {
			JsonNode rootNode = objectMapper.readTree(jsonString);
			JsonNode errorMessageNode = rootNode.get("errorMessage"); // Extract the errorMessage node
			if (errorMessageNode != null) {
				return errorMessageNode.asText(); // Return the error message text
			}
		} catch (Exception e) {
			// Fallback to a default message if JSON parsing fails
			return "Some issues with response from MicroService 2";
		}
		return "Some issue with MS2"; // Fallback if errorMessage is not found
	}
}
