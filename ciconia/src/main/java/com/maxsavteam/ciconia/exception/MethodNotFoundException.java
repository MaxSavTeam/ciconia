package com.maxsavteam.ciconia.exception;

/**
 * Thrown, when requested method not found.
 * */
public class MethodNotFoundException extends ExecutionException {

	public MethodNotFoundException() {
	}

	public MethodNotFoundException(String message) {
		super(message);
	}

	public MethodNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public MethodNotFoundException(Throwable cause) {
		super(cause);
	}
}
