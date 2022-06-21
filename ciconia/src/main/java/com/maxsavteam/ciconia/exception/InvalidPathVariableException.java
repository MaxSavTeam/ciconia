package com.maxsavteam.ciconia.exception;

public class InvalidPathVariableException extends CiconiaRuntimeException {

	public InvalidPathVariableException() {
		super();
	}

	public InvalidPathVariableException(String message) {
		super(message);
	}

	public InvalidPathVariableException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidPathVariableException(Throwable cause) {
		super(cause);
	}
}
