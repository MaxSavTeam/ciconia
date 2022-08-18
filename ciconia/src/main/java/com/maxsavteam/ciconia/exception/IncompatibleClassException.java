package com.maxsavteam.ciconia.exception;

/**
 * Thrown, when parameter from request cannot be converted to mapped method parameter type.
 * */
public class IncompatibleClassException extends ExecutionException {

	public IncompatibleClassException() {
	}

	public IncompatibleClassException(String message) {
		super(message);
	}

	public IncompatibleClassException(String message, Throwable cause) {
		super(message, cause);
	}

	public IncompatibleClassException(Throwable cause) {
		super(cause);
	}
}
