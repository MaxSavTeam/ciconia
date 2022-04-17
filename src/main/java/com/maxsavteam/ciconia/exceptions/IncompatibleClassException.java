package com.maxsavteam.ciconia.exceptions;

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
