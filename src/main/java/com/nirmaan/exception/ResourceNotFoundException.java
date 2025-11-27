package com.nirmaan.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception to indicate that a requested resource (e.g., Quiz, User)
 * could not be found in the system. * It automatically maps to HTTP status 404
 * (Not Found) when thrown from a Spring MVC controller thanks to
 * the @ResponseStatus annotation.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

	// Unique serial version ID for serialization
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor that accepts a custom error message. * @param message The detail
	 * message (e.g., "Quiz not found with id: 10").
	 */
	public ResourceNotFoundException(String message) {
		// Calls the constructor of the parent class (RuntimeException)
		super(message);
	}
}