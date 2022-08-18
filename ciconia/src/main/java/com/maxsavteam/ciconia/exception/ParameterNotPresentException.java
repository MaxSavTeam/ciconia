package com.maxsavteam.ciconia.exception;

/**
 * Thrown when a parameter, required for method, not presented in request
 * */
public class ParameterNotPresentException extends ExecutionException {

	public ParameterNotPresentException() {
	}

	public ParameterNotPresentException(String message) {
		super(message);
	}

	public ParameterNotPresentException(String message, Throwable cause) {
		super(message, cause);
	}

	public ParameterNotPresentException(Throwable cause) {
		super(cause);
	}
}
