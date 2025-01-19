package com.ccms.service.utilities;

/**
 * A simple model class to represent an error response.
 * <p>
 * This class is used to return error details in a standardized format. Typically, it is used for
 * API error responses, where an error message and additional details about the error are provided
 * to the client or user.
 * </p>
 * 
 * <p>
 * It contains two properties:
 * <ul>
 *     <li>{@code error} - A brief message indicating the error (e.g., "Bad Request", "Not Found")</li>
 *     <li>{@code details} - A more detailed explanation of the error (e.g., why the request failed)</li>
 * </ul>
 * </p>
 * 
 * @author [Your Name]
 * @since 1.0
 */

public class ErrorResponse {

	private String error;
	private String details;

	public ErrorResponse(String error, String details) {
		this.error = error;
		this.details = details;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	@Override
	public String toString() {
		return "ErrorResponse [error=" + error + ", details=" + details + "]";
	}

}
