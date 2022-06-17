package com.maxsavteam.ciconia.exceptions;

public class ExecutionException extends CiconiaRuntimeException {

	public ExecutionException() {
	}

	public ExecutionException(String message) {
		super(message);
	}

	public ExecutionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExecutionException(Throwable cause) {
		super(cause);
	}
}
